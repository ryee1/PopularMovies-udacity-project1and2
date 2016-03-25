package com.udacity.richard.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.richard.movieproject.data.MoviesContract;
import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

import java.util.List;

/**
 * Created by richard on 3/16/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private Cursor mCursor;
    final private Context mContext;

    private int mWidth;
    private int mHeight;


    public MovieListAdapter(Context context){
        mContext = context;
    }
    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieListView;
        if(parent instanceof RecyclerView) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            movieListView = inflater.inflate(R.layout.movie_list_recyclerview, parent, false);
        }
        else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
        mWidth = parent.getWidth();
        mHeight = parent.getHeight();
        return new ViewHolder(movieListView);
       }

    @Override
    public void onBindViewHolder(MovieListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        ImageView moviePoster = holder.moviePoster;

        String imageUrl = mCursor.getString(mCursor.getColumnIndex(MoviesListContract.COLUMN_POSTER_PATH));

        Glide.with(mContext)
                .load(imageUrl)
              //  .override(mWidth/2, mHeight /2)
                .fitCenter()
                .into(moviePoster);
    }

    @Override
    public int getItemCount() {
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView moviePoster;
        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster_image);
        }
    }
}
