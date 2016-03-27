package com.udacity.richard.movieproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.udacity.richard.movieproject.R;
import com.udacity.richard.movieproject.Utility;
import com.udacity.richard.movieproject.api.RestAdapter;
import com.udacity.richard.movieproject.data.MoviesContract;
import com.udacity.richard.movieproject.models.Config;
import com.udacity.richard.movieproject.models.Movies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.udacity.richard.movieproject.api.ApiKey.API_KEY;

/**
 * Created by richard on 3/25/16.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    private static final String category_popular = MoviesContract.MoviesListContract.COLUMN_IS_POPULAR;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        fetchConfigFromApiIntoSharedPreferences();
        bulkInsertMoviesList(fetchMoviesfromApi(MoviesContract.MoviesListContract.COLUMN_IS_POPULAR),
                fetchMoviesfromApi(MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED));
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
        }
        return newAccount;
    }

    private void fetchConfigFromApiIntoSharedPreferences(){
        Call<Config> call;
        final Config configData;
        String baseUrl = "";
        String[] imagesSize = {};

        call = RestAdapter.getAdapter().getConfig(API_KEY);
        try{
            configData = call.execute().body();
            baseUrl = configData.images.base_url;
            imagesSize = configData.images.poster_sizes;
        }catch(IOException e){
            Log.e(LOG_TAG, "fetchConfigFromApiIntoSharedPreferences IOException");
        }
        Utility.putConfigInSharedPreferences(getContext(), baseUrl, imagesSize);
    }

    private Movies fetchMoviesfromApi(String category) {
        Call<Movies> call;
        switch (category) {
            case MoviesContract.MoviesListContract.COLUMN_IS_POPULAR:
                call = RestAdapter.getAdapter().getPopular(API_KEY);
                break;
            case MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED:
                call = RestAdapter.getAdapter().getTopRated(API_KEY);
                break;
            default:
                throw new UnsupportedOperationException("Unknown category_popular: " + category_popular);
        }
        try {
            return call.execute().body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "fetchMoviesFromApi IOException");
        }
        return null;
    }

    private void bulkInsertMoviesList(Movies popularMoviesListData, Movies topRatedMoviesListData) {

        if(popularMoviesListData == null && topRatedMoviesListData == null){
            return;
        }

        List<ContentValues> cvList = new ArrayList<>();
        ContentValues cv;
        for(Movies.Result result : popularMoviesListData.getResults()){
            cv = insertMovieListToContentValues(result);
            cv.put(MoviesContract.MoviesListContract.COLUMN_IS_POPULAR, 1);
            cvList.add(cv);
        }
        for(Movies.Result result : topRatedMoviesListData.getResults()){
            cv = insertMovieListToContentValues(result);
            cv.put(MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED, 1);
            cvList.add(cv);
        }
        ContentValues[] cvArray = cvList.toArray(new ContentValues[cvList.size()]);
        getContext().getContentResolver()
                .bulkInsert(MoviesContract.MoviesListContract.CONTENT_URI, cvArray);
    }

    private ContentValues insertMovieListToContentValues(Movies.Result result) {
        String title;
        int movie_id;
        String poster_path;
        double vote_average;
        int vote_count;
        double popularity;
        String release_date;
        String overview;

        ContentValues movieValues = new ContentValues();
        title = result.getTitle();
        movie_id = result.getId();
        poster_path = Utility.buildImageUri(getContext(), Utility.IMAGE_SIZE_SMALL, result.getPoster_path().substring(1));
        vote_average = result.getVote_average();
        vote_count = result.getVote_count();
        popularity = result.getPopularity();
        release_date = result.getRelease_date();
        overview = result.getOverview();

        movieValues.put(MoviesContract.MoviesListContract.COLUMN_TITLE, title);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_MOVIE_ID, movie_id);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_POSTER_PATH, poster_path);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_VOTE_AVERAGE, vote_average);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_VOTE_COUNT, vote_count);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_POPULARITY, popularity);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_RELEASE_DATE, release_date);
        movieValues.put(MoviesContract.MoviesListContract.COLUMN_OVERVIEW, overview);

        return movieValues;
    }
}
