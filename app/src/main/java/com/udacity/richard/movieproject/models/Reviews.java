package com.udacity.richard.movieproject.models;

import java.util.List;

/**
 * Created by richard on 3/20/16.
 */
public class Reviews {

    private List<Results> results;

    public List<Results> getResults() {
        return results;
    }

    public class Results {
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