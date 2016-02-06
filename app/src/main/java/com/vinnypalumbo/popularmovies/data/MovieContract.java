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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.vinnypalumbo.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.vinnypalumbo.popularmovies/movie/ is a valid path for
    // looking at movie data. content://com.vinnypalumbo.popularmovies/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_POPULARITY = "popularity";
    public static final String PATH_RATING = "rating";
    public static final String PATH_WATCHLIST = "watchlist";

    /*
        Inner class that defines the contents of the watchlist table
     */
    public static final class WatchlistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WATCHLIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WATCHLIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WATCHLIST;

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

        public static Uri buildWatchlistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieId(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static int getIdFromUri(Uri uri) {
            Log.d("vinny-debug", "MovieContract - getIdFromUri");
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    /* Inner class that defines the contents of the popular movie table */
    public static final class PopularityEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULARITY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULARITY;


        public static final String TABLE_NAME = "popularity";

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

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieId(int movieId) {
            Log.d("vinny-debug", "MovieContract - buildMovieId");
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static int getIdFromUri(Uri uri) {
            Log.d("vinny-debug", "MovieContract - getIdFromUri");
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    /* Inner class that defines the contents of the highest rated movies table */
    public static final class RatingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;


        public static final String TABLE_NAME = "rating";

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

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieId(int movieId) {
            Log.d("vinny-debug", "MovieContract - buildMovieId");
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static int getIdFromUri(Uri uri) {
            Log.d("vinny-debug", "MovieContract - getIdFromUri");
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }


}
