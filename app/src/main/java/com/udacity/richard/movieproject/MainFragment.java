package com.udacity.richard.movieproject;



import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.richard.movieproject.data.MoviesContract;

import static com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int MOVIES_LIST_LOADER = 0;

    private static final String[] MOVIES_LIST_COLUMNS = {
            MoviesListContract.COLUMN_POSTER_PATH
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
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView rvMovieList = (RecyclerView) view.findViewById(R.id.rvMovieList);

        mMovieListAdapter = new MovieListAdapter(getContext());

        FetchMoviesTask fmt = new FetchMoviesTask(getContext(), mMovieListAdapter);
        fmt.execute(MoviesListContract.COLUMN_IS_POPULAR);

        rvMovieList.setAdapter(mMovieListAdapter);
        rvMovieList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri popularMoviesList = MoviesListContract.buildPopularListUri();
        Log.e(LOG_TAG, MoviesContract.MoviesListContract.CONTENT_URI.toString() );
        return new CursorLoader(getActivity(),
                MoviesContract.MoviesListContract.CONTENT_URI,
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
