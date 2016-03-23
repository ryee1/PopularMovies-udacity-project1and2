package com.udacity.richard.movieproject.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.udacity.richard.movieproject.data.MoviesContract.MoviesListContract;

import java.util.HashSet;

/**
 * Created by richard on 3/21/16.
 */
public class TestDb extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(MoviesListContract.TABLE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateDb() throws Throwable{
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesListContract.TABLE_NAME);

        mContext.deleteDatabase(MoviesListContract.TABLE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        do{
            tableNameHashSet.remove(c.getString(0));
        }while(c.moveToNext());

        assertTrue("Error: Database was created without all the correct tables", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MoviesListContract.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query database for table information.", c.moveToFirst());

        final HashSet<String> moviesListColumnHashSet = new HashSet<>();
        moviesListColumnHashSet.add(MoviesListContract._ID);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_MOVIE_ID);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_POSTER_PATH);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_VOTE_AVERAGE);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_VOTE_COUNT);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_POPULARITY);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_RELEASE_DATE);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_OVERVIEW);
        moviesListColumnHashSet.add(MoviesListContract.COLUMN_IS_POPULAR);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            moviesListColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        assertTrue("Error: Database does not contain all the movies list table columns",
                moviesListColumnHashSet.isEmpty());
        db.close();
    }

    public void testMoviesListTable(){
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = TestUtilities.createMoviesListValues();

        long moviesRowId = db.insert(MoviesListContract.TABLE_NAME, null, cv);
        assertTrue(moviesRowId != -1);

        Cursor moviesListCursor = db.query(
                MoviesListContract.TABLE_NAME,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        assertTrue("Error: No records returned from location query", moviesListCursor.moveToFirst());
        TestUtilities.validateCurrentRecord("testMoviesListTable failed to validate",
                moviesListCursor, cv);

        assertFalse("Error: More than one record returned from movielist query",
                moviesListCursor.moveToNext());

        moviesListCursor.close();
        db.close();

    }
}
