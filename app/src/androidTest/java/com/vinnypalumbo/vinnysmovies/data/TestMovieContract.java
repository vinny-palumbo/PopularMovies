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
package com.vinnypalumbo.vinnysmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final int TEST_MOVIE_ID = 140607;

    /*
        Students: Uncomment this out to test your buildMovieId function.
     */
    public void TestBuildMovieId() {
        Uri MovieUri = MovieContract.PopularityEntry.buildMovieId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieId in " +
                        "MovieContract.",
                MovieUri);
        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
                TEST_MOVIE_ID, MovieUri.getLastPathSegment());
        assertEquals("Error: Movie ID Uri doesn't match our expected result",
                MovieUri.toString(),
                "content://com.vinnypalumbo.popularmovies/movie/140607");
    }
}
