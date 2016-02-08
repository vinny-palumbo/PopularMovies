package com.vinnypalumbo.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.vinnypalumbo.popularmovies.BuildConfig;
import com.vinnypalumbo.popularmovies.MovieFragment;
import com.vinnypalumbo.popularmovies.R;
import com.vinnypalumbo.popularmovies.Utility;
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
 * Created by Vincent on 2016-01-26.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the movies, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("vinny-debug", "MovieSyncAdapter - onPerformSync");
        Log.d(LOG_TAG, "Starting sync");
        String sortingQuery = Utility.getSortSetting(getContext());
        String dateToday;
        String dateOneMonthAgo;
        String dateSixMonthsAgo;

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.
        Time dayTime = new Time();
        dayTime.setToNow();
        // get the julian date of today, 1 month ago and 6 months ago
        int julianDateToday = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        int julianDateOneMonthAgo = julianDateToday - 31;
        int julianDateSixMonthAgo = julianDateToday - (31 * 6);
        // convert Julian dates to the API date format: YYYY-MM-DD
        dateToday = Utility.julianDateToAPIFormatConversion(julianDateToday);
        dateOneMonthAgo = Utility.julianDateToAPIFormatConversion(julianDateOneMonthAgo);
        dateSixMonthsAgo = Utility.julianDateToAPIFormatConversion(julianDateSixMonthAgo);

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
            final String LANGUAGE_PARAM = "language";
            final String LANGUAGE_VALUE = "en";
            final String COUNT_MIN_PARAM = "vote_count.gte";
            final String COUNT_MIN_VALUE = "250";
            final String RATING_MIN_PARAM = "vote_average.gte";
            final String RATING_MIN_VALUE = "6.0";
            final String DATE_MIN_PARAM = "primary_release_date.gte";
            final String DATE_MAX_PARAM = "primary_release_date.lte";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri;
            if(sortingQuery == getContext().getResources().getString(R.string.pref_sort_rating)){
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortingQuery)
                        .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)        // only english language
                        .appendQueryParameter(COUNT_MIN_PARAM, COUNT_MIN_VALUE)      // minimum vote count of 250
                        .appendQueryParameter(RATING_MIN_PARAM, RATING_MIN_VALUE)    // minimum rating of 6/10
                        .appendQueryParameter(DATE_MIN_PARAM, dateSixMonthsAgo)      // released in the last 6 months
                        .appendQueryParameter(DATE_MAX_PARAM, dateToday)             // make sure the movie is released
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            }else{
                // if (sortingQuery == getContext().getResources().getString(R.string.pref_sort_popularity))
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortingQuery)
                        .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)        // only english language
                        .appendQueryParameter(DATE_MIN_PARAM, dateOneMonthAgo)       // released in the last month
                        .appendQueryParameter(DATE_MAX_PARAM, dateToday)             // make sure the movie is released
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            }

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
        Log.d("vinny-debug", "MovieSyncAdapter - getMovieDataFromJson");

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
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

                if(MovieFragment.isRatingSelected){
                    movieValues.put(MovieContract.RatingEntry.COLUMN_MOVIE_ID, movieId);
                    movieValues.put(MovieContract.RatingEntry.COLUMN_TITLE, originalTitle);
                    movieValues.put(MovieContract.RatingEntry.COLUMN_POSTER, posterPath);
                    movieValues.put(MovieContract.RatingEntry.COLUMN_PLOT, overview);
                    movieValues.put(MovieContract.RatingEntry.COLUMN_RATING, voteAverage);
                    movieValues.put(MovieContract.RatingEntry.COLUMN_DATE, releaseDate);
                }else{
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_MOVIE_ID, movieId);
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_TITLE, originalTitle);
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_POSTER, posterPath);
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_PLOT, overview);
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_RATING, voteAverage);
                    movieValues.put(MovieContract.PopularityEntry.COLUMN_DATE, releaseDate);
                }

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                if(MovieFragment.isRatingSelected){
                    // delete old data so we don't build up an endless history
                    getContext().getContentResolver().delete(MovieContract.RatingEntry.CONTENT_URI, null, null);
                    // Student: call bulkInsert to add the movie entries to the rating database here
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MovieContract.RatingEntry.CONTENT_URI, cvArray);
                }else{
                    // delete old data so we don't build up an endless history
                    getContext().getContentResolver().delete(MovieContract.PopularityEntry.CONTENT_URI, null, null);
                    // Student: call bulkInsert to add the movie entries to the popularity database here
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MovieContract.PopularityEntry.CONTENT_URI, cvArray);
                }
            }

            Log.d(LOG_TAG, "Sync Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.d("vinny-debug", "MovieSyncAdapter - configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d("vinny-debug", "MovieSyncAdapter - syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        Log.d("vinny-debug", "MovieSyncAdapter - getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d("vinny-debug", "MovieSyncAdapter - onAccountCreated");
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d("vinny-debug", "MovieSyncAdapter - initializeSyncAdapter");
        getSyncAccount(context);
    }
}
