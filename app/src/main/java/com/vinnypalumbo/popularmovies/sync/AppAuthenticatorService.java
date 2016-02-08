package com.vinnypalumbo.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Vincent on 2016-01-26.
 */
public class AppAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private AppAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.d("vinny-debug", "AppAuthenticatorService - onCreate");
        // Create a new authenticator object
        mAuthenticator = new AppAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("vinny-debug", "AppAuthenticatorService - onBind");
        return mAuthenticator.getIBinder();
    }
}
