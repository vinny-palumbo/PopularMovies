package com.vinnypalumbo.vinnysmovies.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Vincent on 2016-01-26.
 */
public class MovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("vinny-debug", "MovieSyncService - onCreate");
        Log.d("PopularMoviesSyncAdapte", "onCreate-MovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("vinny-debug", "MovieSyncService - onBind");
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
