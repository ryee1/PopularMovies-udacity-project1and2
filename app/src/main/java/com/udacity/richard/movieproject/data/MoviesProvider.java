package com.udacity.richard.movieproject.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


/**
 * Created by richard on 3/21/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES_LIST = 100;
    static final int MOVIES_LIST_POPULAR = 101;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST, MOVIES_LIST);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST + "/" +
                MoviesContract.MoviesListContract.CATEGORY_POPULAR, MOVIES_LIST_POPULAR);
        // TODO add more URIs here

        return matcher;
    }

    private static final String sMoviesListPopularSelection =
            MoviesContract.MoviesListContract.COLUMN_IS_POPULAR + "= 1";

    private Cursor getMoviesListAll(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MoviesListContract.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMoviesListByCategory(Uri uri, String[] projection, String sortOrder){
        String category = MoviesContract.MoviesListContract.getCategoryFromUri(uri);

        String selection;

        switch(category){
            case MoviesContract.MoviesListContract.CATEGORY_POPULAR:
                selection = sMoviesListPopularSelection;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MoviesListContract.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIES_LIST:
                retCursor = getMoviesListAll(uri, projection, sortOrder);
                Log.e(LOG_TAG, "Cursor count" + retCursor.getCount());
                break;
            case MOVIES_LIST_POPULAR:
                retCursor = getMoviesListByCategory(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch(sUriMatcher.match(uri)){
            case MOVIES_LIST:
                long _id = db.insert(MoviesContract.MoviesListContract.TABLE_NAME, null, values);
                if (_id != -1)
                    returnUri =  MoviesContract.MoviesListContract.buildMoviesListRowUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(null == selection) selection = "1";
        switch(match){
            case MOVIES_LIST:
                rowsDeleted = db.delete(MoviesContract.MoviesListContract.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        int _id;
        switch(sUriMatcher.match(uri)){
            case MOVIES_LIST:
                _id = db.update(MoviesContract.MoviesListContract.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(_id != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return _id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_LIST:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesListContract.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
