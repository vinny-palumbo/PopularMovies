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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.vinnypalumbo.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private ImageAdapter mMovieAdapter;
    private final Context mContext;

    public FetchMovieTask(Context context, ImageAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

    private boolean DEBUG = true;

    /* The date/rating conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String formatVoteAverage(double voteAverage){
        return String.valueOf(voteAverage) + "/10";
    }

    private String formatReleaseDate(String releaseDate){
        return releaseDate.substring(0,4);
    }

    /**
     * Helper method to handle insertion of a new movies in the watchlist database.
     *
     * @param title A human-readable title, e.g "The Godfather"
     * @param poster the path of the movie's poster
     * @param plot the movie's plot synopsis
     * @param rating the movie's average vote
     * @param date the movie's release date
     * @return the row ID of the added movie.
     */
    long addToWatchlist(String title, String poster, String plot, String rating, int date) {
        // Students: First, check if the movie with this title exists in the db
        // If it exists, return the current ID
        // Otherwise, insert it using the content resolver and the base URI
        return -1;
    }

    /*
        Students: This code will allow the FetchMovieTask to continue to return the strings that
        the UX expects so that we can continue to test the application even once we begin using
        the database.
    */
    Movie[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return Movies to keep UI functional for now
        Movie[] resultStrs = new Movie[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues movieValues = cvv.elementAt(i);
            String voteAverage = formatVoteAverage(movieValues.getAsDouble(MovieContract.MovieEntry.COLUMN_RATING));
            String releaseDate = formatReleaseDate(movieValues.getAsString(MovieContract.MovieEntry.COLUMN_DATE));
            resultStrs[i] = new Movie(
                    movieValues.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID),
                    movieValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE),
                    movieValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER),
                    movieValues.getAsString(MovieContract.MovieEntry.COLUMN_PLOT),
                    voteAverage,
                    releaseDate
            );
        }
        return resultStrs;
    }

    /**
     * Take the String representing the movie results in JSON Format and
     * pull out the data we need for each movie
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private Movie[] getMovieDataFromJson(String movieJsonStr, String sortBySetting) throws JSONException {

        // The API gives us 20 movies
        final int numMovies = 20;

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_PLOT = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_DATE = "release_date";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            // Insert the new movie information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                int movieId;
                String originalTitle;
                String posterPath;
                String overview;
                double voteAverage;
                String releaseDate;

                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);

                // the movie ID is in a Int associated to the key "id"
                movieId = movie.getInt(TMDB_ID);

                // the title is in a String associated to the key "original_title"
                originalTitle = movie.getString(TMDB_TITLE);

                // the poster path is in a String associated to the key "poster_path"
                posterPath = movie.getString(TMDB_POSTER);

                // the plot synopsis is in a String associated to the key "overview"
                overview = movie.getString(TMDB_PLOT);

                // the rating is in a Double associated to the key "vote_average"
                voteAverage = movie.getDouble(TMDB_RATING);

                // the release date is in a String associated to the key "release_date"
                releaseDate = movie.getString(TMDB_DATE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, originalTitle);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, voteAverage);
                movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, releaseDate);

                cVVector.add(movieValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                // Student: call bulkInsert to add the movieEntries to the database here
            }

            // Students: Uncomment the next lines to display what what you stored in the bulkInsert

//            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri,
//                    null, null, null, sortOrder);
//
//            cVVector = new Vector<ContentValues>(cur.getCount());
//            if ( cur.moveToFirst() ) {
//                do {
//                    ContentValues cv = new ContentValues();
//                    DatabaseUtils.cursorRowToContentValues(cur, cv);
//                    cVVector.add(cv);
//                } while (cur.moveToNext());
//            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + cVVector.size() + " Inserted");

            Movie[] resultStrs = convertContentValuesToUXFormat(cVVector);
            return resultStrs;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override protected Movie[] doInBackground(String... params) {
        // If there's no sort_by value, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        String sortingQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            // Construct the URL for the TheMovieDB query
            // Possible parameters are avaiable at TMDB's movie API page, at
            // http://docs.themoviedb.apiary.io/#reference/discover/discovermovie

            //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc");

            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String COUNT_PARAM = "vote_count.gte";
            final String COUNT_VALUE = "500";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(COUNT_PARAM, COUNT_VALUE)
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(movieJsonStr, sortingQuery);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movie.
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        if (result != null && mMovieAdapter != null) {
            mMovieAdapter.clear();
            for (Movie movie : result) {
                mMovieAdapter.add(movie);
            }
            // New data is back from the server.  Hooray!
        }
    }
}