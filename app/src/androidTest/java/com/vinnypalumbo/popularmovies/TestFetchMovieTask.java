/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vinnypalumbo.popularmovies;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.vinnypalumbo.popularmovies.data.MovieContract;

public class TestFetchMovieTask extends AndroidTestCase{
    static final int ADD_MOVIE_ID = 286217;
    static final String ADD_TITLE = "The Martian";
    static final String ADD_POSTER = "\\/5aGhaIHYuQbqlHWvWYqMCnj40y2.jpg";
    static final String ADD_PLOT = "During a manned mission to Mars, Astronaut Mark Watney is presumed dead after a fierce storm and left behind by his crew. But Watney has survived and finds himself stranded and alone on the hostile planet. With only meager supplies, he must draw upon his ingenuity, wit and spirit to subsist and find a way to signal to Earth that he is alive.";
    static final double ADD_RATING = 7.65;
    static final int ADD_DATE = 2015;

    /*
        Students: uncomment testAddToWatchlist after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddToWatchlist() {
        // start from a clean state
        getContext().getContentResolver().delete(MovieContract.WatchlistEntry.CONTENT_URI,
                MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(ADD_MOVIE_ID)});

        FetchMovieTask fwt = new FetchMovieTask(getContext(), null);
        long watchlistId = fwt.addToWatchlist(ADD_MOVIE_ID, ADD_TITLE, ADD_POSTER, ADD_PLOT,
                ADD_RATING, ADD_DATE);

        // does addToWatchlist return a valid record ID?
        assertFalse("Error: addToWatchlist returned an invalid ID on insert",
                watchlistId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our movie?
            Cursor watchlistCursor = getContext().getContentResolver().query(
                    MovieContract.WatchlistEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.WatchlistEntry._ID,
                            MovieContract.WatchlistEntry.COLUMN_MOVIE_ID,
                            MovieContract.WatchlistEntry.COLUMN_TITLE,
                            MovieContract.WatchlistEntry.COLUMN_POSTER,
                            MovieContract.WatchlistEntry.COLUMN_PLOT,
                            MovieContract.WatchlistEntry.COLUMN_RATING,
                            MovieContract.WatchlistEntry.COLUMN_DATE
                    },
                    MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(ADD_MOVIE_ID)},
                    null);

            // these match the indices of the projection
            if (watchlistCursor.moveToFirst()) {
                assertEquals("Error: the queried value of watchlistId does not match the returned value" +
                        "from addToWatchlist", watchlistCursor.getLong(0), watchlistId);
                assertEquals("Error: the queried value of movie ID is incorrect",
                        watchlistCursor.getInt(1), ADD_MOVIE_ID);
                assertEquals("Error: the queried value of title is incorrect",
                        watchlistCursor.getString(2), ADD_TITLE);
                assertEquals("Error: the queried value of the poster is incorrect",
                        watchlistCursor.getString(3), ADD_POSTER);
                assertEquals("Error: the queried value of the plot is incorrect",
                        watchlistCursor.getString(4), ADD_PLOT);
                assertEquals("Error: the queried value of the rating is incorrect",
                        watchlistCursor.getDouble(5), ADD_RATING);
                assertEquals("Error: the queried value of the date is incorrect",
                        watchlistCursor.getInt(6), ADD_DATE);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a watchlist query",
                    watchlistCursor.moveToNext());

            // add the movie again
            long newWatchlistId = fwt.addToWatchlist(ADD_MOVIE_ID, ADD_TITLE, ADD_POSTER, ADD_PLOT,
                    ADD_RATING, ADD_DATE);

            assertEquals("Error: inserting a movie again should return the same ID",
                    watchlistId, newWatchlistId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.WatchlistEntry.CONTENT_URI,
                MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(ADD_MOVIE_ID)});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.WatchlistEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
