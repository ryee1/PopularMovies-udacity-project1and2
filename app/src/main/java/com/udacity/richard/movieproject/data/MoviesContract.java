package com.udacity.richard.movieproject.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by richard on 3/20/16.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.richard.movieproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES_LIST = "movieslist";

    public static final class MoviesListContract implements BaseColumns {

        public static final String CATEGORY_POPULAR = "popular";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIES_LIST)
                .build();

        public static final String TABLE_NAME = "movies_list";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_IS_POPULAR = "is_popular";
        public static final String COLUMN_IS_FAVORITES = "is_favorites";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";
        public static final String COLUMN_VIDEOS = "videos";
        public static final String COLUMNS_REVIEWS = "reviews";


        public static Uri buildMoviesListRowUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildPopularListUri(){
            return CONTENT_URI.buildUpon().appendPath(CATEGORY_POPULAR).build();
        }

        public static String getCategoryFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }
}
