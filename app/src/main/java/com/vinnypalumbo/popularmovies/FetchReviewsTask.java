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

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private ReviewAdapter mReviewAdapter;
    private final Context mContext;

    public FetchReviewsTask(Context context, ReviewAdapter reviewAdapter) {
        mContext = context;
        mReviewAdapter = reviewAdapter;
    }

    private boolean DEBUG = true;

    @Override
    protected List<Review> doInBackground(String... params) {
        Log.d("vinny-debug", "FetchReviewsTask - doInBackground");
        // Add useless code to test what happens when adding commit to a branch already merged with master

        // If there's no movieId, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String movieId = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        try {
            // Construct the URL for the TheMovieDB query
            // Possible parameters are available at TMDB's movie API page

            //URL url = new URL("http://api.themoviedb.org/3/movie/281957/reviews?api_key=XXXX");

            final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie";
            final String REVIEW_SEGMENT = "reviews";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(REVIEW_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(REVIEW_SEGMENT)
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
            reviewJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the review data, there's no point in attempting
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
            return getReviewDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    /**
     * Take the String representing the review results in JSON Format and
     * pull out the data we need for each review
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private List<Review> getReviewDataFromJson(String reviewJsonStr) throws JSONException {
        Log.d("vinny-debug", "FetchReviewsTask - getReviewDataFromJson");

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(TMDB_RESULTS);

            // Insert the review into an ArrayList
            List<Review> reviews = new ArrayList<Review>();

            for (int i = 0; i < reviewArray.length(); i++) {
                String reviewAuthor;
                String reviewContent;

                // Get the JSON object representing the review
                JSONObject review = reviewArray.getJSONObject(i);

                // the review author is in a String associated to the key "author"
                reviewAuthor = review.getString(TMDB_AUTHOR);

                // the review title is in a String associated to the key "content"
                reviewContent = review.getString(TMDB_CONTENT);

                reviews.add(new Review(reviewAuthor, reviewContent));
            }
            Log.d(LOG_TAG, "Fetch Reviews Completed");

            return reviews;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Review> result) {
        if (result != null && mReviewAdapter != null) {
            mReviewAdapter.clear();
            for(Review review : result) {
                mReviewAdapter.add(review);
            }
            // New data is back from the server.  Hooray!
        }
    }
}