package com.udacity.richard.movieproject.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by richard on 3/22/16.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MoviesContract.MoviesListContract.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MoviesListContract.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies List table", 0, cursor.getCount());

        cursor.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MoviesProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: MoviesProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testBasicMoviesListQuery(){
        MoviesDbHelper dbHelper= new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues expectedValues = TestUtilities.createMoviesListValues();
        long MoviesListRowId = TestUtilities.insertMoviesListValues(mContext);
        db.close();

        Cursor moviesCursor = mContext.getContentResolver().query(
                MoviesContract.MoviesListContract.CONTENT_URI,
                null, null, null, null
        );
        TestUtilities.validateCursor("testBasicMoviesListQuery", moviesCursor, expectedValues);
    }

    public void testInsertReadProvider(){
        ContentValues testValues = TestUtilities.createMoviesListValues();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MoviesListContract.CONTENT_URI,
                true, tco);
        Uri moviesListUri = mContext.getContentResolver().insert(MoviesContract.MoviesListContract.CONTENT_URI,
                testValues);
        assertTrue(moviesListUri != null);
        tco.waitForNotificationOrFail();
        Cursor moviesCursor = getContext().getContentResolver().query(MoviesContract.MoviesListContract.CONTENT_URI,
                null, null, null, null);

        TestUtilities.validateCursor("testInsertReadProvider", moviesCursor, testValues);
        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    public void testDeleteRecords(){
        testInsertReadProvider();
        TestUtilities.TestContentObserver moviesListObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MoviesListContract.CONTENT_URI,
                true, moviesListObserver);
        deleteAllRecordsFromProvider();
        moviesListObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesListObserver);
    }
}
