package com.example.ahmed.syncserver.SyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class SyncService extends Service {
    /**
     * Lock use to synchronize instantiation of SyncAdapter.
     */
    private static final Object LOCK = new Object();
    private static SyncAdapter syncAdapter;


    @Override
    public void onCreate() {
        // SyncAdapter is not Thread-safe
        synchronized (LOCK) {
            // Instantiate our SyncAdapter
            syncAdapter = new SyncAdapter(this, false);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return our SyncAdapter's IBinder
        return syncAdapter.getSyncAdapterBinder();
    }

}
