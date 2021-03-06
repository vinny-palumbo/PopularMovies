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
package com.vinnypalumbo.vinnysmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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

public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    private ArrayAdapter<Trailer> mTrailerAdapter;
    public String firstTrailerKey;
    public String firstTrailerName;  // for debugging purposes
    private final Context mContext;

    public FetchTrailersTask(Context context, ArrayAdapter<Trailer> trailerAdapter) {
        mContext = context;
        mTrailerAdapter = trailerAdapter;
    }

    private boolean DEBUG = true;

    @Override
    protected List<Trailer> doInBackground(String... params) {
        Log.d("vinny-debug", "FetchTrailersTask - doInBackground");

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
        String trailerJsonStr = null;

        try {
            // Construct the URL for the TheMovieDB query
            // Possible parameters are available at TMDB's movie API page

            //URL url = new URL("http://api.themoviedb.org/3/movie/281957/videos?api_key=XXXX");

            final String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie";
            final String VIDEO_SEGMENT = "videos";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(VIDEO_SEGMENT)
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
            trailerJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the trailer data, there's no point in attempting
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
            return getTrailerDataFromJson(trailerJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    /**
     * Take the String representing the trailer results in JSON Format and
     * pull out the data we need for each trailer
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private List<Trailer> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
        Log.d("vinny-debug", "FetchTrailersTask - getTrailerDataFromJson");

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_NAME = "name";
        final String TMDB_KEY = "key";

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TMDB_RESULTS);

            // Insert the trailer infos into an ArrayList
            List<Trailer> trailers = new ArrayList<Trailer>();

            for (int i = 0; i < trailerArray.length(); i++) {
                String trailerName;
                String trailerKey;

                // Get the JSON object representing the trailer
                JSONObject trailer = trailerArray.getJSONObject(i);
                // the trailer name is in a String associated to the key "name"
                trailerName = trailer.getString(TMDB_NAME);
                // the trailer key is in a String associated to the key "key"
                trailerKey = trailer.getString(TMDB_KEY);

                trailers.add(new Trailer(trailerName, trailerKey));
            }
            Log.d(LOG_TAG, "Fetch Trailers Completed");

            return trailers;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        Log.d("vinny-debug", "FetchTrailersTask - onPostExecute");

        final String TRAILER_STRING = "Trailer";
        final String YOUTUBE_BASE_URL = "https://youtu.be/";

        if (trailers != null && mTrailerAdapter != null) {
            mTrailerAdapter.clear();
            for(int i = 0; i < trailers.size(); i++) {
                Trailer trailer = trailers.get(i);

                // add trailer to the Trailer Adapter
                mTrailerAdapter.add(trailer);

                // capture first trailer key for share intent text
                if(i == 0){
                    firstTrailerKey = trailer.key;
                    firstTrailerName = trailer.name;  // for debugging purposes
                    // We still need this for the trailer share intent text
                    Log.d("vinny-debug", "FetchTrailersTask - onPostExecute: firstTrailerKey: " + firstTrailerKey + "-" + firstTrailerName);
                    DetailFragment.mTrailerShareText = String.format("%s - %s %s: %s%s ", DetailFragment.title, DetailFragment.year, TRAILER_STRING, YOUTUBE_BASE_URL, firstTrailerKey);

                    // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                    if (DetailFragment.mShareActionProvider != null) {
                        DetailFragment.mShareActionProvider.setShareIntent(DetailFragment.createShareTrailerIntent());
                    }
                }
            }
        }
    }
}