package com.example.ahmed.syncserver;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ahmed.syncserver.AccountDetails.AccountGeneral;
import com.example.ahmed.syncserver.SyncAdapter.SyncAdapter;

public class MainActivity extends AppCompatActivity {
    /**
     * This is our example content observer.
     */
    private UserObserver userObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create your sync account   ERROR WHEN THIS FUNCTION IS CALLED :(
        AccountGeneral.createSyncAccount(this);

        // Perform a manual sync by calling this:
        SyncAdapter.performSync();

        // Setup example content observer
        userObserver = new UserObserver();
    }
    @Override
    protected void onStart(){
        super.onStart();
        // Register the observer at the start of our activity
        // Register the observer at the start of our activity
        getContentResolver().registerContentObserver(
                DbContract.Users.CONTENT_URI, // Uri to observe (our articles)
                true, // Observe its descendants
                userObserver); // The observer
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (userObserver != null) {
            // Unregister the observer at the stop of our activity
            getContentResolver().unregisterContentObserver(userObserver);
        }
    }

    private void refreshUsers() {
        Log.i(getClass().getName(), "User data has changed!");
    }


    /**
     * Example content observer for observing article data changes.
     */
    private final class UserObserver extends ContentObserver {
        private UserObserver() {
            // Ensure callbacks happen on the UI thread
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // Handle your data changes here!!!
            refreshUsers();
        }
    }

}



