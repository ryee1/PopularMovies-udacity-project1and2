package com.udacity.richard.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

/**
 * Created by richard on 3/16/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder>{


    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private Cursor mCursor;
    final private Context mContext;
    final private MovieListAdapterOnClickHandler mClickHandler;

    public interface MovieListAdapterOnClickHandler {
        void onClick(Long movieId, ViewHolder vh);
    }

    public MovieListAdapter(Context context, MovieListAdapterOnClickHandler dh){
        mContext = context;
        mClickHandler = dh;
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
        return new ViewHolder(movieListView);
       }

    @Override
    public void onBindViewHolder(MovieListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        ImageView moviePoster = holder.moviePoster;

        String imageUrl = mCursor.getString(mCursor.getColumnIndex(MoviesListContract.COLUMN_POSTER_PATH));
        Glide.with(mContext)
                .load(imageUrl)
                .override(Utility.getScreenWidth(mContext)/2,700)
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView moviePoster;
        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster_image);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onClick(mCursor.getLong(mCursor.getColumnIndex(MoviesListContract.COLUMN_MOVIE_ID)),
                    this);
        }
    }
}
