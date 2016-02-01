package com.vinnypalumbo.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Vincent on 2016-01-26.
 */
public class PopularMoviesAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PopularMoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.d("vinny-debug", "PopularMoviesAuthenticatorService - onCreate");
        // Create a new authenticator object
        mAuthenticator = new PopularMoviesAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("vinny-debug", "PopularMoviesAuthenticatorService - onBind");
        return mAuthenticator.getIBinder();
    }
}
