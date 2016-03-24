package com.udacity.richard.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.richard.movieproject.data.MoviesContract;
import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

import java.util.List;

/**
 * Created by richard on 3/16/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private Cursor mCursor;
    final private Context mContext;

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
        return new ViewHolder(movieListView);
       }

    @Override
    public void onBindViewHolder(MovieListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        TextView movieName = holder.movieName;

        String imageUrl = mCursor.getString(mCursor.getColumnIndex(MoviesListContract.COLUMN_POSTER_PATH));
        movieName.setText(imageUrl);
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

        public TextView movieName;
        public ViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
        }
    }
}
