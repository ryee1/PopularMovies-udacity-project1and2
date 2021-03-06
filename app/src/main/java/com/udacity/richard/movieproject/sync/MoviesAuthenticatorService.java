package com.udacity.richard.movieproject.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by richard on 3/25/16.
 */
public class MoviesAuthenticatorService extends Service {
    private MoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new MoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
