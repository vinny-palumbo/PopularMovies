package com.vinnypalumbo.popularmovies.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.vinnypalumbo.popularmovies.BuildConfig;
import com.vinnypalumbo.popularmovies.MovieAdapter;
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

/**
 * Created by Vincent on 2016-01-25.
 */
public class PopularMoviesService extends IntentService {
    private MovieAdapter mMovieAdapter;
    public static final String SORTING_QUERY_EXTRA = "sqe";
    private final String LOG_TAG = PopularMoviesService.class.getSimpleName();
    public PopularMoviesService() {
        super("Popular Movies");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String sortingQuery = intent.getStringExtra(SORTING_QUERY_EXTRA);

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
                    .appendQueryParameter(SORT_PARAM, sortingQuery)
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
                return ;
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
                return;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        return;
    }

    /**
     * Take the String representing the movie results in JSON Format and
     * pull out the data we need for each movie
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {

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

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                // Student: call bulkInsert to add the movie entries to the movie database here
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to handle insertion of a new movies in the watchlist database.
     * @param movie_id movie ID returned from the API
     * @param title A human-readable title, e.g "The Godfather"
     * @param poster the path of the movie's poster
     * @param plot the movie's plot synopsis
     * @param rating the movie's average vote
     * @param date the movie's release date
     * @return the row ID of the added movie.
     */
    long addToWatchlist(int movie_id, String title, String poster, String plot, double rating, int date) {
        long watchlistId;

        // First, check if the movie with this ID exists in the db
        Cursor watchlistCursor = this.getContentResolver().query(
                MovieContract.WatchlistEntry.CONTENT_URI,
                new String[]{MovieContract.WatchlistEntry._ID},
                MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movie_id)},
                null);

        if (watchlistCursor.moveToFirst()) {
            int watchlistIdIndex = watchlistCursor.getColumnIndex(MovieContract.WatchlistEntry._ID);
            watchlistId = watchlistCursor.getLong(watchlistIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues watchlistValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_MOVIE_ID, movie_id);
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_TITLE, title);
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_POSTER, poster);
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_PLOT, plot);
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_RATING, rating);
            watchlistValues.put(MovieContract.WatchlistEntry.COLUMN_DATE, date);

            // Finally, insert movie data into the watchlist database.
            Uri insertedUri = this.getContentResolver().insert(
                    MovieContract.WatchlistEntry.CONTENT_URI,
                    watchlistValues
            );

            // The resulting URI contains the ID for the row.  Extract the watchlistId from the Uri.
            watchlistId = ContentUris.parseId(insertedUri);
        }

        watchlistCursor.close();
        // Wait, that worked?  Yes!
        return watchlistId;
    }
}
