package com.udacity.richard.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.udacity.richard.movieproject.data.MoviesContract;
import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

import java.util.List;

/**
 * Created by richard on 3/16/16.
 */
public class MovieListAdapter extends CursorAdapter {

    public MovieListAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.movie_list_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String imageUrl = cursor.getString(cursor.getColumnIndex(MoviesListContract.COLUMN_POSTER_PATH));
        viewHolder.movieName.setText(imageUrl);

    }


    public static class ViewHolder{

        public TextView movieName;
        public ViewHolder(View itemView) {
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
        }
    }
}
