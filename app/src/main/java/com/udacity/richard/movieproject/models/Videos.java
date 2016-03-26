package com.udacity.richard.movieproject.models;

import java.util.List;

/**
 * Created by richard on 3/25/16.
 */
public class Videos {
    private List<Results> results;

    public List<Results> getResults() {
        return results;
    }

    public class Results{
        private String key;
        private String id;
        private String name;
        private String site;

        public String getKey() {
            return key;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }
    }
}
