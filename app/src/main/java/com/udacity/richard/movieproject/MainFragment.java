package com.udacity.richard.movieproject;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.richard.movieproject.data.MoviesContract;
import com.udacity.richard.movieproject.sync.MoviesSyncAdapter;

import static com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    public interface Callback {
        public void onItemSelected(Uri movieIdUri);
    }

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int MOVIES_LIST_LOADER = 0;
    private int mPosition;

    private static final String[] MOVIES_LIST_COLUMNS = {
            MoviesListContract.COLUMN_POSTER_PATH,
            MoviesListContract.COLUMN_MOVIE_ID
    };

    private MovieListAdapter mMovieListAdapter;
    public static MainFragment newInstance(){
        MainFragment f = new MainFragment();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView rVMovieList = (RecyclerView) view.findViewById(R.id.rvMovieList);

        mMovieListAdapter = new MovieListAdapter(getContext(), new MovieListAdapter.MovieListAdapterOnClickHandler(){
            @Override
            public void onClick(Long movieId, MovieListAdapter.ViewHolder vh) {
                ((Callback)getActivity()).onItemSelected(Uri.parse(Long.toString(movieId)));
                mPosition = vh.getAdapterPosition();
            }
        });

        MoviesSyncAdapter.syncImmediately(getContext());

        rVMovieList.setAdapter(mMovieListAdapter);
        rVMovieList.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.movies_list_columns)));
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri categoryMoviesList = MoviesListContract.buildTopRatedListUri();

        Log.e(LOG_TAG, MoviesContract.MoviesListContract.CONTENT_URI.toString() );

        return new CursorLoader(getActivity(),
                categoryMoviesList,
                MOVIES_LIST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "Cursor count:" + data.getCount());
        mMovieListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }
}
