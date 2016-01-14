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

import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {


    /*
        Inner class that defines the contents of the watchlist table
     */
    public static final class WatchlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "watchlist";

        // Movie id as returned by API. Stored as int.
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Title of the movie.
        public static final String COLUMN_TITLE = "title";

        // Movie Poster. Stored as string.
        public static final String COLUMN_POSTER = "poster";

        // Movie synopsis.
        public static final String COLUMN_PLOT = "plot";

        // Movie vote average. Stored as floats
        public static final String COLUMN_RATING = "rating";

        // Movie Release Date
        public static final String COLUMN_DATE = "date";

    }

    /* Inner class that defines the contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        // Movie id as returned by API. Stored as int.
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Title of the movie. Stored as a string.
        public static final String COLUMN_TITLE = "title";

        // Movie Poster. Stored as string.
        public static final String COLUMN_POSTER = "poster";

        // Movie synopsis. Stored as a string.
        public static final String COLUMN_PLOT = "plot";

        // Movie vote average. Stored a float.
        public static final String COLUMN_RATING = "rating";

        // Movie Release Date. Stored as a string.
        public static final String COLUMN_DATE = "date";

    }
}
