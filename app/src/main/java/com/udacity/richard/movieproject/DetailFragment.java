package com.udacity.richard.movieproject;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.udacity.richard.movieproject.api.ApiKey;
import com.udacity.richard.movieproject.api.RestAdapter;
import com.udacity.richard.movieproject.models.Reviews;
import com.udacity.richard.movieproject.models.Videos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "com.udacity.richard.movieproject.detail_uri";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final int MOVIE_DETAIL_LOADER = 0;
    private long mMovieId;

    private TextView mTitle;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mOverView;
    private Button mFavoritesButton;
    private ImageView mPoster;
    private TextView mToolbarTitle;

    public static DetailFragment newInstance(Parcelable uri) {
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, uri);
        f.setArguments(args);
        return f;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mTitle = (TextView) view.findViewById(R.id.detail_title_textview);
        mVoteAverage = (TextView) view.findViewById(R.id.detail_vote_average_textview);
        mReleaseDate = (TextView) view.findViewById(R.id.detail_release_date_textview);
        mOverView = (TextView) view.findViewById(R.id.detail_overview_textview);
        mPoster = (ImageView) view.findViewById(R.id.detail_poster);
        mFavoritesButton = (Button) view.findViewById(R.id.detail_favorites_button);
        mToolbarTitle = (TextView) getActivity().findViewById(R.id.main_toolbar_title);

        mMovieId = MoviesListContract.getMovieIdFromUri(
                (Uri) getArguments().getParcelable(DETAIL_URI));

        RestAdapter.getAdapter().getReviews(mMovieId, ApiKey.API_KEY)
                .enqueue(new Callback<Reviews>() {
                    @Override
                    public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                        Gson gson = new Gson();
                        ContentValues cv = new ContentValues();
                        cv.put(MoviesListContract.COLUMNS_REVIEWS, gson.toJson(response.body()));

                        getContext().getContentResolver().update(
                                MoviesListContract.CONTENT_URI,
                                cv,
                                MoviesListContract.COLUMN_MOVIE_ID + " = " + mMovieId,
                                null
                        );
                        addReviews(response.body().getResults());
                    }

                    @Override
                    public void onFailure(Call<Reviews> call, Throwable t) {
                        Cursor c = getContext().getContentResolver()
                                .query(
                                        MoviesListContract.CONTENT_URI,
                                        new String[]{MoviesListContract.COLUMNS_REVIEWS},
                                        MoviesListContract.COLUMN_MOVIE_ID + " = " + mMovieId,
                                        null,
                                        null
                                );
                        if(c == null || !c.moveToFirst()){
                            Log.e(LOG_TAG, "Empty Cursor onFailure");
                            return;
                        }
                        Gson gson = new Gson();
                        addReviews(gson.fromJson(c.getString(c.getColumnIndex(MoviesListContract.COLUMNS_REVIEWS)),
                                Reviews.class).getResults());
                        c.close();
                    }
                });

        RestAdapter.getAdapter().getVideos(mMovieId, ApiKey.API_KEY)
                .enqueue(new Callback<Videos>() {
                    @Override
                    public void onResponse(Call<Videos> call, Response<Videos> response) {
                        Gson gson = new Gson();
                        ContentValues cv = new ContentValues();
                        cv.put(MoviesListContract.COLUMN_VIDEOS, gson.toJson(response.body()));

                        getContext().getContentResolver().update(
                                MoviesListContract.CONTENT_URI,
                                cv,
                                MoviesListContract.COLUMN_MOVIE_ID + " = " + mMovieId,
                                null
                        );
                        addVideos(response.body().getResults());
                    }

                    @Override
                    public void onFailure(Call<Videos> call, Throwable t) {
                        Cursor c = getContext().getContentResolver()
                                .query(
                                        MoviesListContract.CONTENT_URI,
                                        new String[]{MoviesListContract.COLUMN_VIDEOS},
                                        MoviesListContract.COLUMN_MOVIE_ID + " = " + mMovieId,
                                        null,
                                        null
                        );
                        if(c == null || !c.moveToFirst()){
                            Log.e(LOG_TAG, "Empty Cursor onFailure");
                            return;
                        }
                        Gson gson = new Gson();
                        addVideos(gson.fromJson(c.getString(c.getColumnIndex(MoviesListContract.COLUMN_VIDEOS)),
                                Videos.class).getResults());
                        c.close();
                    }
                });
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader");
        switch (id) {
            case MOVIE_DETAIL_LOADER:
                long movieId = MoviesListContract.getMovieIdFromUri((Uri) getArguments().getParcelable(DETAIL_URI));
                return new CursorLoader(
                        getContext(),
                        MoviesListContract.buildMoviesListRowUri(movieId),
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException("Unknown id: " + id);
        }
    }

    private void addReviews(List<Reviews.Result> reviews) {
        if (reviews.size() <= 0) {
            return;
        }
        LinearLayout reviewContainer = (LinearLayout) getActivity().findViewById(R.id.review_container);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView titleReview = new TextView(getContext());
        titleReview.setPadding(0, 12, 0, 0);
        // titleReview.setText(R.string.detail_reviews_title);
        // titleReview.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.large_textsize));
        reviewContainer.addView(titleReview);
        for (int i = 0; i < reviews.size(); i++) {
            TextView reviewTextView = new TextView(getContext());
            reviewTextView.setLayoutParams(layoutParams);
            //  reviewTextView.setTextColor(getResources().getColor(R.color.black));
            reviewTextView.setPadding(0, 12, 0, 0);
            // reviewTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.medium_textsize));
            reviewTextView.setText(reviews.get(i).getContent() + " - " +
                    reviews.get(i).getAuthor());
            reviewContainer.addView(reviewTextView);
        }
    }

    private void addVideos(List<Videos.Results> videos) {
        if (videos.size() <= 0)
            return;
        LinearLayout videoContainer = (LinearLayout) getActivity().findViewById(R.id.video_container);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < videos.size(); i++) {
            final String key = videos.get(i).getKey();
            Button button = new Button(getContext());
            button.setText(videos.get(i).getName());
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(YOUTUBE_BASE_URL + key));
                    Log.e(LOG_TAG, (Uri.parse(YOUTUBE_BASE_URL + key)).toString());
                    startActivity(intent);
                }
            });
            videoContainer.addView(button);
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(MoviesListContract.COLUMN_POSTER_PATH)))
                    .override(Utility.getScreenWidth(getContext()) / 2, 700)
                    .fitCenter()
                    .into(mPoster);
            mTitle.setText(data.getString(data.getColumnIndex(MoviesListContract.COLUMN_TITLE)));
            mVoteAverage.setText("Rating: " + data.getString(data.getColumnIndex(MoviesListContract.COLUMN_VOTE_AVERAGE))
                    + "/10");
            mReleaseDate.setText(data.getString(data.getColumnIndex(MoviesListContract.COLUMN_RELEASE_DATE))
                    .substring(0, 4));
            mOverView.setText(data.getString(data.getColumnIndex(MoviesListContract.COLUMN_OVERVIEW)));

            if(!getResources().getBoolean(R.bool.twopane)){
                mToolbarTitle.setText(data.getString(data.getColumnIndex(MoviesListContract.COLUMN_TITLE)));

            }
            if (data.getInt(data.getColumnIndex(MoviesListContract.COLUMN_IS_FAVORITES)) == 0) {
                mFavoritesButton.setText(R.string.detail_button_add_favorites);
                mFavoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyFavoritesOnClick(1);
                    }
                });
            } else {
                mFavoritesButton.setText(R.string.detail_button_remove_favorites);
                mFavoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyFavoritesOnClick(0);
                    }
                });
            }
        }
    }

    private void modifyFavoritesOnClick(int setFavorites) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesListContract.COLUMN_IS_FAVORITES, setFavorites);
        getContext().getContentResolver().update(
                MoviesListContract.CONTENT_URI,
                cv,
                MoviesListContract.COLUMN_MOVIE_ID + " = " + mMovieId,
                null
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
