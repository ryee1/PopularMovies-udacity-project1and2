package com.udacity.richard.movieproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

/**
 * Created by richard on 3/21/16.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_LIST_TABLE = "CREATE TABLE " + MoviesListContract.TABLE_NAME
                + " (" + MoviesListContract._ID + " INTEGER PRIMARY KEY,"
                + MoviesListContract.COLUMN_TITLE + " TEXT NOT NULL,"
                + MoviesListContract.COLUMN_MOVIE_ID + " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL,"
                + MoviesListContract.COLUMN_POSTER_PATH + " TEXT NOT NULL,"
                + MoviesListContract.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"
                + MoviesListContract.COLUMN_VOTE_COUNT + " INTEGER NOT NULL,"
                + MoviesListContract.COLUMN_POPULARITY + " REAL NOT NULL,"
                + MoviesListContract.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                + MoviesListContract.COLUMN_OVERVIEW + " TEXT NOT NULL,"
                + MoviesListContract.COLUMN_IS_POPULAR + " INTEGER NOT NULL,"
                + MoviesListContract.COLUMN_IS_FAVORITES + "INTEGER"
                + ");";

        db.execSQL(SQL_CREATE_MOVIES_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesListContract.TABLE_NAME);
        onCreate(db);
    }
}
