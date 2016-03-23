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
        private String posterPath;
        private String overview;
        private String releaseDate;
        private Integer id;
        private String title;
        private String backdropPath;
        private Double popularity;
        private Integer voteCount;
        private Double voteAverage;

        public String getPosterPath() {
            return posterPath;
        }

        public String getOverview() {
            return overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public Integer getId() {
            return id;
        }


        public String getTitle() {
            return title;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public Double getPopularity() {
            return popularity;
        }

        public Integer getVoteCount() {
            return voteCount;
        }

        public Double getVoteAverage() {
            return voteAverage;
        }
    }
}
