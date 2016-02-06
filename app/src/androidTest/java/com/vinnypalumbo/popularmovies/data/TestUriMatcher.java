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
package com.vinnypalumbo.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final int MOVIEID_QUERY = 140607;

    // content://com.vinnypalumbo.popularmovies/movie"
    private static final Uri TEST_MOVIE_DIR = MovieContract.PopularityEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID_DIR = MovieContract.PopularityEntry.buildMovieId(MOVIEID_QUERY);
    // content://com.vinnypalumbo.popularmovies/watchlist"
    private static final Uri TEST_WATCHLIST_DIR = MovieContract.WatchlistEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The POPULARITY URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.POPULARITY);
        assertEquals("Error: The POPULARITY WITH ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID_DIR), MovieProvider.POPULARITY_WITH_ID);
        assertEquals("Error: The WATCHLIST URI was matched incorrectly.",
                testMatcher.match(TEST_WATCHLIST_DIR), MovieProvider.WATCHLIST);
    }
}
