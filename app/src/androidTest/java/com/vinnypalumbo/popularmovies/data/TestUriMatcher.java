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

import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {

    // content://com.vinnypalumbo.popularmovies/movie"
    private static final Uri TEST_WEATHER_DIR = MovieContract.MovieEntry.CONTENT_URI;
    // content://com.vinnypalumbo.popularmovies/watchlist"
    private static final Uri TEST_LOCATION_DIR = MovieContract.WatchlistEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
//    public void testUriMatcher() {
//        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
//
//        assertEquals("Error: The WEATHER URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_DIR), MovieProvider.WEATHER);
//        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), MovieProvider.WEATHER_WITH_LOCATION);
//        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), MovieProvider.WEATHER_WITH_LOCATION_AND_DATE);
//        assertEquals("Error: The LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_LOCATION_DIR), MovieProvider.LOCATION);
//    }
}
