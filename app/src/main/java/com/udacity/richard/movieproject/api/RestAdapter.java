package com.udacity.richard.movieproject.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by richard on 3/17/16.
 */
public class RestAdapter {
    private static ApiService sApiService = null;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private RestAdapter(){

    }

    public static ApiService getAdapter(){
        if(sApiService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            sApiService = retrofit.create(ApiService.class);
        }
        return sApiService;
    }

}
