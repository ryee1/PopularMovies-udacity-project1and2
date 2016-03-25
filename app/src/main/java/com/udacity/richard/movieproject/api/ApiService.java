package com.udacity.richard.movieproject.api;

import com.udacity.richard.movieproject.models.Config;
import com.udacity.richard.movieproject.models.Movies;
import com.udacity.richard.movieproject.models.Reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by richard on 3/17/16.
 */
public interface ApiService {

        @GET("configuration")
    Call<Config> getConfig(@Query("api_key") String api_key);

    @GET("movie/popular")
    Call<Movies> getPopular(@Query("api_key") String api_key);

//    @GET("movie/top_rated")
//    Call<Movies> getTopRated(@Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String api_key);

//    @GET("movie/{id}/videos")
//    Call<VideoModel> getVideos(@Path("id") int id, @Query("api_key") String api_key);
}