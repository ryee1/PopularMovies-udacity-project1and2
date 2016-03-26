package com.udacity.richard.movieproject.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by richard on 3/25/16.
 */
public class MoviesSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter mMoviesSyncAdapter = null;
    @Override
    public void onCreate() {
        Log.d("MovieSyncService", "onCreate");
        synchronized (sSyncAdapterLock){
            if(mMoviesSyncAdapter == null){
                mMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
