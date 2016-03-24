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
import android.widget.ListView;

import com.udacity.richard.movieproject.data.MoviesContract;

import static com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int MOVIES_LIST_LOADER = 0;
    private ListView mListView;

    private static final String[] MOVIES_LIST_COLUMNS = {
            MoviesListContract._ID,
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
        //TODO debug
        getContext().getContentResolver().delete(MoviesContract.MoviesListContract.CONTENT_URI,
                null, null);

        getLoaderManager().initLoader(MOVIES_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        mMovieListAdapter = new MovieListAdapter(getContext(), null, 0);

        FetchMoviesTask fmt = new FetchMoviesTask(getContext(), mMovieListAdapter);
        fmt.execute(MoviesListContract.COLUMN_IS_POPULAR);
        mListView = (ListView) view.findViewById(R.id.lvMovieList);
        mListView.setAdapter(mMovieListAdapter);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri popularMoviesList = MoviesListContract.buildPopularListUri();
        Log.e(LOG_TAG, MoviesContract.MoviesListContract.CONTENT_URI.toString() );
        return new CursorLoader(getActivity(),
                MoviesContract.MoviesListContract.CONTENT_URI,
                null,
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
