package com.udacity.richard.movieproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.richard.movieproject.api.ApiService;
import com.udacity.richard.movieproject.api.ApiKey;
import com.udacity.richard.movieproject.api.RestAdapter;
import com.udacity.richard.movieproject.models.Movies;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    public static MainFragment newInstance(){
        MainFragment f = new MainFragment();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView rvMovieList = (RecyclerView) view.findViewById(R.id.rvMovieList);
        ArrayList<Contact> contacts = Contact.createContactsList(20);
        MovieListAdapter adapter = new MovieListAdapter(contacts);
        rvMovieList.setAdapter(adapter);
        rvMovieList.setLayoutManager(new GridLayoutManager(getContext(), 2));


        ApiService service = RestAdapter.getAdapter();

        Call<Movies> call = service.getPopular(ApiKey.API_KEY);
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.e(TAG, response.body().getResults().get(0).getOverview());
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Log.e(TAG, "onFailure");

            }
        });

        return view;
    }

}
