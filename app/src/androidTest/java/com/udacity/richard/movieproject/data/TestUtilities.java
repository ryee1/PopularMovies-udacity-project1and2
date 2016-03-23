package com.udacity.richard.movieproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;
import com.udacity.richard.movieproject.utils.PollingCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by richard on 3/21/16.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createMoviesListValues() {

        ContentValues cv = new ContentValues();
        cv.put(MoviesListContract.COLUMN_MOVIE_ID, "movieId1234");
        cv.put(MoviesListContract.COLUMN_TITLE, "movieTitle1234");
        cv.put(MoviesListContract.COLUMN_POSTER_PATH, "poster_path1234");
        cv.put(MoviesListContract.COLUMN_VOTE_AVERAGE, 5.5);
        cv.put(MoviesListContract.COLUMN_VOTE_COUNT, 10);
        cv.put(MoviesListContract.COLUMN_POPULARITY, 10.5);
        cv.put(MoviesListContract.COLUMN_RELEASE_DATE, "releasedate1234");
        cv.put(MoviesListContract.COLUMN_OVERVIEW, "overview here");
        cv.put(MoviesListContract.COLUMN_IS_POPULAR, 1);
        return cv;
    }

    static long insertMoviesListValues(Context context){
        MoviesDbHelper dbHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = createMoviesListValues();

        long moviesListRowId;
        moviesListRowId = db.insert(MoviesListContract.TABLE_NAME, null, cv);
        assertTrue("Error: Failure to insert movies list values", moviesListRowId != -1);
        return moviesListRowId;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues){
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertTrue("Column '" + columnName + "' not found." + error, idx != -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" +  valueCursor.getString(idx) + "' did not match the expected value '"
            + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues){
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
