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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.vinnypalumbo.vinnysmovies.MovieFragment;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int POPULARITY = 100;
    static final int POPULARITY_WITH_ID = 101;
    static final int RATING = 200;
    static final int RATING_WITH_ID = 201;
    static final int WATCHLIST = 300;
    static final int WATCHLIST_WITH_ID = 301;

    private static final SQLiteQueryBuilder sPopularityQueryBuilder;
    private static final SQLiteQueryBuilder sRatingQueryBuilder;
    private static final SQLiteQueryBuilder sWatchlistQueryBuilder;

    static{
        sPopularityQueryBuilder = new SQLiteQueryBuilder();
        sRatingQueryBuilder = new SQLiteQueryBuilder();
        sWatchlistQueryBuilder = new SQLiteQueryBuilder();

        sPopularityQueryBuilder.setTables(
                MovieContract.PopularityEntry.TABLE_NAME);
        sRatingQueryBuilder.setTables(
                MovieContract.RatingEntry.TABLE_NAME);
        sWatchlistQueryBuilder.setTables(
                MovieContract.WatchlistEntry.TABLE_NAME);
    }

    //popularity.movie_id = ?
    private static final String sPopularityIdSelection =
            MovieContract.PopularityEntry.TABLE_NAME +
                    "." + MovieContract.PopularityEntry.COLUMN_MOVIE_ID + " = ? ";

    //rating.movie_id = ?
    private static final String sRatingIdSelection =
            MovieContract.RatingEntry.TABLE_NAME +
                    "." + MovieContract.RatingEntry.COLUMN_MOVIE_ID + " = ? ";

    //watchlist.movie_id = ?
    private static final String sWatchlistIdSelection =
            MovieContract.WatchlistEntry.TABLE_NAME +
                    "." + MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        Log.d("vinny-debug", "MovieProvider - getMovieByMovieId");
        int id;

        if(MovieFragment.isWatchlistSelected){
            id = MovieContract.WatchlistEntry.getIdFromUri(uri);
            return sWatchlistQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sWatchlistIdSelection,
                    new String[]{Integer.toString(id)},
                    null,
                    null,
                    sortOrder
            );
        }else if(MovieFragment.isRatingSelected){
            id = MovieContract.RatingEntry.getIdFromUri(uri);
            return sRatingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sRatingIdSelection,
                    new String[]{Integer.toString(id)},
                    null,
                    null,
                    sortOrder
            );
        }else{
            id = MovieContract.PopularityEntry.getIdFromUri(uri);
            return sPopularityQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sPopularityIdSelection,
                    new String[]{Integer.toString(id)},
                    null,
                    null,
                    sortOrder
            );
        }
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the POPULARITY integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        Log.d("vinny-debug", "MovieProvider - buildUriMatcher");
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_POPULARITY, POPULARITY);
        matcher.addURI(authority, MovieContract.PATH_POPULARITY + "/#", POPULARITY_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_RATING, RATING);
        matcher.addURI(authority, MovieContract.PATH_RATING + "/#", RATING_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_WATCHLIST, WATCHLIST);
        matcher.addURI(authority, MovieContract.PATH_WATCHLIST + "/#", WATCHLIST_WITH_ID);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        Log.d("vinny-debug", "MovieProvider - onCreate");
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {
        Log.d("vinny-debug", "MovieProvider - getType");
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POPULARITY_WITH_ID:
                return MovieContract.PopularityEntry.CONTENT_ITEM_TYPE;
            case POPULARITY:
                return MovieContract.PopularityEntry.CONTENT_TYPE;
            case RATING_WITH_ID:
                return MovieContract.RatingEntry.CONTENT_ITEM_TYPE;
            case RATING:
                return MovieContract.RatingEntry.CONTENT_TYPE;
            case WATCHLIST_WITH_ID:
                return MovieContract.WatchlistEntry.CONTENT_ITEM_TYPE;
            case WATCHLIST:
                return MovieContract.WatchlistEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d("vinny-debug", "MovieProvider - query");
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "popularity/#"
            case POPULARITY_WITH_ID:
            {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            }
            // "popularity"
            case POPULARITY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "rating/#"
            case RATING_WITH_ID:
            {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            }
            // "rating"
            case RATING: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.RatingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "watchlist/#"
            case WATCHLIST_WITH_ID:
            {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            }
            // "watchlist"
            case WATCHLIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.WatchlistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Watchlist movies to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("vinny-debug", "MovieProvider - insert");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POPULARITY: {
                long _id = db.insert(MovieContract.PopularityEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.PopularityEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RATING: {
                long _id = db.insert(MovieContract.RatingEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.RatingEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case WATCHLIST: {
                long _id = db.insert(MovieContract.WatchlistEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.WatchlistEntry.buildWatchlistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("vinny-debug", "MovieProvider - delete");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case POPULARITY:
                rowsDeleted = db.delete(
                        MovieContract.PopularityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RATING:
                rowsDeleted = db.delete(
                        MovieContract.RatingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WATCHLIST:
                rowsDeleted = db.delete(
                        MovieContract.WatchlistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d("vinny-debug", "MovieProvider - update");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POPULARITY:
                rowsUpdated = db.update(MovieContract.PopularityEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case RATING:
                rowsUpdated = db.update(MovieContract.RatingEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case WATCHLIST:
                rowsUpdated = db.update(MovieContract.WatchlistEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.d("vinny-debug", "MovieProvider - bulkInsert");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case POPULARITY:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PopularityEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case RATING:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.RatingEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        Log.d("vinny-debug", "MovieProvider - shutdown");
        mOpenHelper.close();
        super.shutdown();
    }
}