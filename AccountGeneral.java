package com.example.ahmed.syncserver.AccountDetails;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.example.ahmed.syncserver.DbContract;
import com.example.ahmed.syncserver.SyncAdapter.SyncAdapter;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class AccountGeneral {
    /**
     * This is the type of account we are using. i.e. we can specify our app or apps
     * to have different types, such as 'read-only', 'sync-only', & 'admin'.
     */
    private static final String ACCOUNT_TYPE = "com.example.syncaccount";

    /**
     * This is the name that appears in the Android 'Accounts' settings.
     */
    private static final String ACCOUNT_NAME = "ExampleSync";


    /**
     * Gets the standard sync account for our app.
     * @return {@link Account}
     */
    public static Account getAccount() {
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }

    /**
     * Creates the standard sync account for our app.
     * @param c {@link Context}
     */
    public static void createSyncAccount(Context c) {
        // Flag to determine if this is a new account or not
        boolean created = false;

        // Get an account and the account manager
        Account account = getAccount();
        AccountManager manager = (AccountManager)c.getSystemService(Context.ACCOUNT_SERVICE);

        // Attempt to explicitly create the account with no password or extra data
        // ERROR CALL :(
        if (manager.addAccountExplicitly(account, null, null)) {
            final String AUTHORITY = DbContract.CONTENT_AUTHORITY;
            final long SYNC_FREQUENCY = 60 * 60; // 1 hour (seconds)

            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            created = true;
        }

        // Force a sync if the account was just created
        if (created) {
            SyncAdapter.performSync();
        }
    }

}
