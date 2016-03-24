package com.udacity.richard.movieproject.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 3/20/16.
 */
public class Movies {
    private List<Result> results = new ArrayList<>();

    public List<Result> getResults() {
        return results;
    }

    public class Result {
        private String poster_path;
        private String overview;
        private String release_date;
        private Integer id;
        private String title;
        private String backdrop_path;
        private Double popularity;
        private Integer vote_count;
        private Double vote_average;

        public String getPoster_path() {
            return poster_path;
        }

        public String getOverview() {
            return overview;
        }

        public String getRelease_date() {
            return release_date;
        }

        public Integer getId() {
            return id;
        }


        public String getTitle() {
            return title;
        }

        public String getBackdrop_path() {
            return backdrop_path;
        }

        public Double getPopularity() {
            return popularity;
        }

        public Integer getVote_count() {
            return vote_count;
        }

        public Double getVote_average() {
            return vote_average;
        }


    }
}
