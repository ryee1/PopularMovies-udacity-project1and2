package com.udacity.richard.movieproject;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.richard.movieproject.api.RestAdapter;
import com.udacity.richard.movieproject.models.Config;
import com.udacity.richard.movieproject.models.Movies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.udacity.richard.movieproject.api.ApiKey.API_KEY;
import static com.udacity.richard.movieproject.data.MoviesContract.*;

/**
 * Created by richard on 3/23/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private final Context mContext;
    private Movies mMoviesListData;
    private MovieListAdapter mAdapter;

    public FetchMoviesTask(Context mContext, MovieListAdapter adapter) {
        this.mContext = mContext;
        mAdapter = adapter;
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
        Utility.putConfigInSharedPreferences(mContext, baseUrl, imagesSize);
    }

    private void fetchMoviesfromApi(String category) {
        Call<Movies> call;
        final Movies returnedData;
        switch (category) {
            case MoviesListContract.COLUMN_IS_POPULAR:
                call = RestAdapter.getAdapter().getPopular(API_KEY);
                break;
            default:
                throw new UnsupportedOperationException("Unknown category: " + category);
        }
        try {
            mMoviesListData = call.execute().body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "fetchMoviesFromApi IOException");
        }
    }

    public void bulkInsertMoviesList(String category) {

        List<ContentValues> cvList = new ArrayList<>();
        ContentValues cv;
        for(Movies.Result result : mMoviesListData.getResults()){
            cv = insertMovieListToContentValues(category, result);
            cvList.add(cv);
        }

        ContentValues[] cvArray = cvList.toArray(new ContentValues[cvList.size()]);
        mContext.getContentResolver()
                .bulkInsert(MoviesListContract.CONTENT_URI, cvArray);
    }

    private ContentValues insertMovieListToContentValues(String category, Movies.Result result) {
        String title;
        int movie_id;
        String poster_path;
        double vote_average;
        int vote_count;
        double popularity;
        String release_date;
        String overview;
        int is_popular;

        ContentValues movieValues = new ContentValues();
        title = result.getTitle();
        movie_id = result.getId();
        poster_path = Utility.buildImageUri(mContext, Utility.IMAGE_SIZE_SMALL, result.getPoster_path().substring(1));
        vote_average = result.getVote_average();
        vote_count = result.getVote_count();
        popularity = result.getPopularity();
        release_date = result.getRelease_date();
        overview = result.getOverview();

        movieValues.put(MoviesListContract.COLUMN_TITLE, title);
        movieValues.put(MoviesListContract.COLUMN_MOVIE_ID, movie_id);
        movieValues.put(MoviesListContract.COLUMN_POSTER_PATH, poster_path);
        movieValues.put(MoviesListContract.COLUMN_VOTE_AVERAGE, vote_average);
        movieValues.put(MoviesListContract.COLUMN_VOTE_COUNT, vote_count);
        movieValues.put(MoviesListContract.COLUMN_POPULARITY, popularity);
        movieValues.put(MoviesListContract.COLUMN_RELEASE_DATE, release_date);
        movieValues.put(MoviesListContract.COLUMN_OVERVIEW, overview);
        switch (category) {
            case MoviesListContract.COLUMN_IS_POPULAR:
                movieValues.put(MoviesListContract.COLUMN_IS_POPULAR, 1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown category: " + category);
        }

        return movieValues;
    }

    @Override
    protected Void doInBackground(String... params) {
        String category = params[0];
        fetchConfigFromApiIntoSharedPreferences();
        fetchMoviesfromApi(category);
        bulkInsertMoviesList(category);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
