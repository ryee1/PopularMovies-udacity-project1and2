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
    static final int MOVIES_LIST_CATEGORY = 101;
    static final int MOVIES_SELECTION = 200;
    static final int MOVIES_FAVORITES = 300;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST, MOVIES_LIST);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST + "/" +
                MoviesContract.MoviesListContract.CATEGORY_FAVORITES, MOVIES_FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST + "/#", MOVIES_SELECTION);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_LIST + "/*", MOVIES_LIST_CATEGORY);
        return matcher;
    }

    private static final String sMoviesListPopularSelection =
            MoviesContract.MoviesListContract.COLUMN_IS_POPULAR + "= 1";

    private static final String sMoviesListTopRatedSelection =
            MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED + "= 1";

    private void resetCategoryColumns(){
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MoviesListContract.COLUMN_IS_POPULAR, 0);
        cv.put(MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED, 0);
        db.update(
                MoviesContract.MoviesListContract.TABLE_NAME,
                cv,
                null,
                null
        );
    }

    //Check for and remove row from table if all category columns and favorites column are zero
    private void cleanDatabase(){
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.delete(
                MoviesContract.MoviesListContract.TABLE_NAME,
                " ( " +
                MoviesContract.MoviesListContract.COLUMN_IS_FAVORITES + " = 0 or " +
                        MoviesContract.MoviesListContract.COLUMN_IS_FAVORITES + " is null"
                + " ) and " +
                        MoviesContract.MoviesListContract.COLUMN_IS_POPULAR + " = 0 and " +
                        MoviesContract.MoviesListContract.COLUMN_IS_TOP_RATED + " = 0",
                null
        );
    }

    private Cursor getMoviesFavorites(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MoviesListContract.TABLE_NAME,
                projection,
                MoviesContract.MoviesListContract.COLUMN_IS_FAVORITES+ " = 1",
                null,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMovieSelection(Uri uri){
        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MoviesListContract.TABLE_NAME,
                null,
                MoviesContract.MoviesListContract.COLUMN_MOVIE_ID + " = ?",
                new String[]{Long.toString(MoviesContract.MoviesListContract.getMovieIdFromUri(uri))},
                null,
                null,
                null
        );
    }
    private Cursor getMoviesListAll(Uri uri, String[] projection, String selection,String sortOrder) {
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

    private Cursor getMoviesListByCategory(Uri uri, String[] projection, String sortOrder) {
        String category = MoviesContract.MoviesListContract.getCategoryFromUri(uri);

        String selection;

        switch (category) {
            case MoviesContract.MoviesListContract.CATEGORY_POPULAR:
                selection = sMoviesListPopularSelection;
                break;
            case MoviesContract.MoviesListContract.CATEGORY_TOP_RATED:
                selection = sMoviesListTopRatedSelection;
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
        switch (sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                retCursor = getMoviesListAll(uri, projection, selection, sortOrder);
                Log.e(LOG_TAG, "Cursor count" + retCursor.getCount());
                break;
            case MOVIES_LIST_CATEGORY:
                retCursor = getMoviesListByCategory(uri, projection, sortOrder);
                break;
            case MOVIES_SELECTION:
                retCursor = getMovieSelection(uri);
                break;
            case MOVIES_FAVORITES:
                retCursor = getMoviesFavorites(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
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

        switch (sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                long _id = db.insert(MoviesContract.MoviesListContract.TABLE_NAME, null, values);
                if (_id != -1)
                    returnUri = MoviesContract.MoviesListContract.buildMoviesListRowUri(_id);
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
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES_LIST:
                rowsDeleted = db.delete(MoviesContract.MoviesListContract.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int _id;
//        switch (sUriMatcher.match(uri)) {
//            case MOVIES_LIST:
                _id = db.update(MoviesContract.MoviesListContract.TABLE_NAME, values, selection,
                        selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
        if (_id != 0)
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
                int returnCount = 1;

                resetCategoryColumns();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MoviesContract.MoviesListContract.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                        else{
                            db.update(MoviesContract.MoviesListContract.TABLE_NAME, value,
                                    MoviesContract.MoviesListContract.COLUMN_MOVIE_ID + " = ?",
                                    new String[] {Integer.toString(value.getAsInteger
                                            (MoviesContract.MoviesListContract.COLUMN_MOVIE_ID))});
                        }
                    }
                    cleanDatabase();
                    Log.e(LOG_TAG, "transaction successful");
                    db.setTransactionSuccessful();
                } finally {
                    Log.e(LOG_TAG, "transaction ended");
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
