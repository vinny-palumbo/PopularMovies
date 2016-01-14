package com.vinnypalumbo.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.vinnypalumbo.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your MovieContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default movie values for your database tests.
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 140607);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Star Wars: The Force Awakens");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "\\/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 7.93);
        movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, "2015-09-11");

        return movieValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        WatchlistEntry part of the MovieContract.
     */
    static ContentValues createWatchlistValues() {
        // Create a new map of values, where column names are the keys
        ContentValues watchlistValues = new ContentValues();
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_MOVIE_ID, 140607);
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_TITLE, "Star Wars: The Force Awakens");
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_POSTER, "\\/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_PLOT, "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.");
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_RATING, 7.93);
        watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_DATE, "2015-09-11");

        return watchlistValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        WatchlistEntry part of the MovieContract as well as the MovieDbHelper.
     */
    static long insertWatchlistValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createWatchlistValues();

        long watchlistRowId;
        watchlistRowId = db.insert(MovieContract.WatchlistEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", watchlistRowId != -1);

        return watchlistRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
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
