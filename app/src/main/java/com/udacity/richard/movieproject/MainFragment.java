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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.richard.movieproject.sync.MoviesSyncAdapter;

import static com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    public interface Callback {
        public void onItemSelected(Long movieId);
    }

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int POPULAR_LIST_LOADER = 0;
    private static final int TOP_RATED_LIST_LOADER = 1;
    private static final int FAVORITES_LOADER = 2;
    private int mPosition;

    private TextView mToolbarTitle;

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
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        mToolbarTitle = (TextView) getActivity().findViewById(R.id.main_toolbar_title);
        if(mToolbarTitle == null){
            Log.e(LOG_TAG, "null toolbar");
        }
        RecyclerView rVMovieList = (RecyclerView) view.findViewById(R.id.rvMovieList);

        mMovieListAdapter = new MovieListAdapter(getContext(), new MovieListAdapter.MovieListAdapterOnClickHandler(){
            @Override
            public void onClick(Long movieId, MovieListAdapter.ViewHolder vh) {
                ((Callback)getActivity()).onItemSelected(movieId);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_sort_popularity:
                clearOtherLoaders(POPULAR_LIST_LOADER);
                getLoaderManager().restartLoader(POPULAR_LIST_LOADER, null, this);
                break;
            case R.id.menu_sort_rating:
                clearOtherLoaders(TOP_RATED_LIST_LOADER);
                getLoaderManager().restartLoader(TOP_RATED_LIST_LOADER, null, this);
                break;
            case R.id.menu_sort_favorites:
                clearOtherLoaders(FAVORITES_LOADER);
                getLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearOtherLoaders(int loaderToKeep){
        LoaderManager lm = getLoaderManager();
        if(POPULAR_LIST_LOADER != loaderToKeep){
            lm.destroyLoader(POPULAR_LIST_LOADER);
        }
        if(TOP_RATED_LIST_LOADER != loaderToKeep){
            lm.destroyLoader(TOP_RATED_LIST_LOADER);
        }
        if(FAVORITES_LOADER != loaderToKeep){
            lm.destroyLoader(FAVORITES_LOADER);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri categoryMoviesList;
        switch(id){
            case POPULAR_LIST_LOADER:
                categoryMoviesList = MoviesListContract.buildPopularListUri();
                mToolbarTitle.setText(R.string.toolbar_popular_movies_title);
                break;
            case TOP_RATED_LIST_LOADER:
                categoryMoviesList = MoviesListContract.buildTopRatedListUri();
                mToolbarTitle.setText(R.string.toolbar_top_rated_movies_title);
                break;
            case FAVORITES_LOADER:
                categoryMoviesList = MoviesListContract.buildFavoritesListUri();
                mToolbarTitle.setText(R.string.toolbar_favorites_title);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }

        return new CursorLoader(getActivity(),
                categoryMoviesList,
                MOVIES_LIST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "Loader tag: " + loader.getId());
        mMovieListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }
}
