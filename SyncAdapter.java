package com.example.ahmed.syncserver.SyncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.example.ahmed.syncserver.AccountDetails.AccountGeneral;
import com.example.ahmed.syncserver.DbContract;
import com.example.ahmed.syncserver.InfoParser;
import com.example.ahmed.syncserver.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SYNC_ADAPTER";
    /**
     * This gives us access to our local data source.
     */
    private final ContentResolver resolver;


    public SyncAdapter(Context c, boolean autoInit) {
        this(c, autoInit, false);
    }

    public SyncAdapter(Context c, boolean autoInit, boolean parallelSync) {
        super(c, autoInit, parallelSync);
        this.resolver = c.getContentResolver();
    }

    /**
     * This method is run by the Android framework, on a new Thread, to perform a sync.
     *
     * @param account    Current account
     * @param extras     Bundle extras
     * @param authority  Content authority
     * @param provider   {@link ContentProviderClient}
     * @param syncResult Object to write stats to
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.w(TAG, "Starting synchronization...");

        try {
            // Synchronize our news feed
            syncUserInfo(syncResult);

            // Add any other things you may want to sync

        } catch (IOException ex) {
            Log.e(TAG, "Error synchronizing!", ex);
            syncResult.stats.numIoExceptions++;
        } catch (JSONException ex) {
            Log.e(TAG, "Error synchronizing!", ex);
            syncResult.stats.numParseExceptions++;
        } catch (RemoteException | OperationApplicationException ex) {
            Log.e(TAG, "Error synchronizing!", ex);
            syncResult.stats.numAuthExceptions++;
        }

        Log.w(TAG, "Finished synchronization!");
    }

    /**
     * Performs synchronization of our pretend news feed source.
     * @param syncResult Write our stats to this
     */
    private void syncUserInfo(SyncResult syncResult) throws IOException, JSONException, RemoteException, OperationApplicationException {
        final String rssFeedEndpoint = "http://10.0.0.2/syncdemo/syncinfo.php";

        // We need to collect all the network items in a hash table
        Log.i(TAG, "Fetching server entries...");
        Map<String, User> networkEntries = new HashMap<>();

        // Parse the pretend json news feed
        String jsonFeed = download(rssFeedEndpoint);
        JSONArray jsonArticles = new JSONArray(jsonFeed);
        for (int i = 0; i < jsonArticles.length(); i++) {
            User user = InfoParser.parse(jsonArticles.optJSONObject(i));
            networkEntries.put(user.getName(), user);

        }

        // Create list for batching ContentProvider transactions
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        // Compare the hash table of network entries to all the local entries
        Log.i(TAG, "Fetching local entries...");
        Cursor c = resolver.query(DbContract.Users.CONTENT_URI, null, null, null, null);
        assert c != null;
        c.moveToFirst();

        String id;
        String name;
        String email;
        String password;
        String link;

        User found;
        for (int i = 0; i < c.getCount(); i++) {
            syncResult.stats.numEntries++;

            // Create local article entry
            id = c.getString(c.getColumnIndex(DbContract.Users.COL_ID));
            name = c.getString(c.getColumnIndex(DbContract.Users.COL_NAME));
            email = c.getString(c.getColumnIndex(DbContract.Users.COL_EMAIL));
            password = c.getString(c.getColumnIndex(DbContract.Users.COL_PASSWORD));
            link = c.getString(c.getColumnIndex(DbContract.Users.COL_LINK));

            // Try to retrieve the local entry from network entries
            found = networkEntries.get(id);
            if (found != null) {
                // The entry exists, remove from hash table to prevent re-inserting it
                networkEntries.remove(id);

                // Check to see if it needs to be updated
                if (!link.equals(found.getLink())
                        || !name.equals(found.getName())
                        || !email.equals(found.getEmail())
                        || !password.equals(found.getPassword())) {
                    // Batch an update for the existing record
                    Log.i(TAG, "Scheduling update: " + name);
                    batch.add(ContentProviderOperation.newUpdate(DbContract.Users.CONTENT_URI)
                            .withSelection(DbContract.Users.COL_ID + "='" + id + "'", null)
                            .withValue(DbContract.Users.COL_NAME, found.getName())
                            .withValue(DbContract.Users.COL_EMAIL, found.getEmail())
                            .withValue(DbContract.Users.COL_PASSWORD, found.getPassword())
                            .withValue(DbContract.Users.COL_LINK, found.getLink())
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Entry doesn't exist, remove it from the local database
                Log.i(TAG, "Scheduling delete: " + id);
                batch.add(ContentProviderOperation.newDelete(DbContract.Users.CONTENT_URI)
                        .withSelection(DbContract.Users.COL_ID+ "='" + id + "'", null)
                        .build());
                syncResult.stats.numDeletes++;
            }
            c.moveToNext();
        }
        c.close();

        // Add all the new entries
        for (User user: networkEntries.values()) {
            Log.i(TAG, "Scheduling insert: " + user.getId());
            batch.add(ContentProviderOperation.newInsert(DbContract.Users.CONTENT_URI)
                    .withValue(DbContract.Users.COL_EMAIL, user.getEmail())
                    .withValue(DbContract.Users.COL_PASSWORD, user.getPassword())
                    .withValue(DbContract.Users.COL_NAME, user.getName())
                    .withValue(DbContract.Users.COL_LINK, user.getLink())
                    .build());
            syncResult.stats.numInserts++;
        }

        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        resolver.applyBatch(DbContract.CONTENT_AUTHORITY, batch);
        resolver.notifyChange(DbContract.Users.CONTENT_URI, // URI where data was modified
                null, // No local observer
                false); // IMPORTANT: Do not sync to network
    }

    /**
     * A blocking method to stream the server's content and build it into a string.
     * @param url API call
     * @return String response
     */
    private String download(String url) throws IOException {
        // Ensure we ALWAYS close these!
        HttpURLConnection client = null;
        InputStream is = null;

        try {
            // Connect to the server using GET protocol
            URL server = new URL(url);
            client = (HttpURLConnection)server.openConnection();
            client.connect();

            // Check for valid response code from the server
            int status = client.getResponseCode();
            is = (status == HttpURLConnection.HTTP_OK)
                    ? client.getInputStream() : client.getErrorStream();

            // Build the response or error as a string
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            for (String temp; ((temp = br.readLine()) != null);) {
                sb.append(temp);
            }

            return sb.toString();
        } finally {
            if (is != null) { is.close(); }
            if (client != null) { client.disconnect(); }
        }
    }

    /**
     * Manual force Android to perform a sync with our SyncAdapter.
     */
    public static void performSync() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(AccountGeneral.getAccount(),
                DbContract.CONTENT_AUTHORITY, b);
    }

}