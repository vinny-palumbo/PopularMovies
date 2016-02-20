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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vinnypalumbo.vinnysmovies.data.MovieContract.PopularityEntry;
import com.vinnypalumbo.vinnysmovies.data.MovieContract.WatchlistEntry;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WATCHLIST_TABLE = "CREATE TABLE " + WatchlistEntry.TABLE_NAME + " (" +
                WatchlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WatchlistEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                WatchlistEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                WatchlistEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                WatchlistEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                WatchlistEntry.COLUMN_RATING + " REAL NOT NULL, " +
                WatchlistEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                ");";

        final String SQL_CREATE_POPULARITY_TABLE = "CREATE TABLE " + PopularityEntry.TABLE_NAME + " (" +
                PopularityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularityEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                PopularityEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopularityEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                PopularityEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                PopularityEntry.COLUMN_RATING + " REAL NOT NULL, " +
                PopularityEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                ");";

        final String SQL_CREATE_RATING_TABLE = "CREATE TABLE " + MovieContract.RatingEntry.TABLE_NAME + " (" +
                MovieContract.RatingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.RatingEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.RatingEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.RatingEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieContract.RatingEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MovieContract.RatingEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.RatingEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WATCHLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULARITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RATING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WatchlistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.RatingEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
