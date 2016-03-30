package com.udacity.richard.movieproject.models;

import java.util.List;

/**
 * Created by richard on 3/20/16.
 */
public class Reviews {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public class Result {
        private String author;
        private String content;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }
}