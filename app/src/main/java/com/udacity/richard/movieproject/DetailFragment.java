package com.udacity.richard.movieproject;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    public static final String DETAIL_URI = "com.udacity.richard.movieproject.detail_uri";

    public static DetailFragment newInstance(Parcelable uri){
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, uri);
        f.setArguments(args);
        return f;
    }
    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detail, container, false);

        return view;
    }

}
