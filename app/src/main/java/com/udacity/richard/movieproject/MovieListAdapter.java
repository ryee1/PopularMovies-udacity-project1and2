package com.udacity.richard.movieproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by richard on 3/16/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private List<Contact> mContact;

    public MovieListAdapter(List<Contact> contacts){
        mContact = contacts;
    }
    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View movieListView = inflater.inflate(R.layout.movie_list_recyclerview, parent, false);

        return new ViewHolder(movieListView);
       }

    @Override
    public void onBindViewHolder(MovieListAdapter.ViewHolder holder, int position) {
        Contact contact = mContact.get(position);
        TextView movieName = holder.movieName;

        if(contact.isOnline()){
            movieName.setText(contact.getName());
        }
        else{
            movieName.setText(contact.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mContact.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView movieName;
        public ViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.movie_name);
        }
    }
}
