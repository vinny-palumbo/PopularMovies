package com.vinnypalumbo.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private ImageAdapter mMovieAdapter;
    private ArrayList<Movie> listOfMovies;

    public MovieFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key", (ArrayList<? extends Parcelable>) listOfMovies);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            listOfMovies = savedInstanceState.getParcelableArrayList("key");
        }else{
            listOfMovies = new ArrayList<Movie>();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        movieTask.execute(sorting);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new ImageAdapter(
                getActivity(),
                listOfMovies
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach the Adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMovieAdapter);
        // setup on click event
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("title", movie.originalTitle)
                        .putExtra("poster", movie.posterPath)
                        .putExtra("plot", movie.overview)
                        .putExtra("rating", movie.voteAverage)
                        .putExtra("year", movie.releaseDate);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String formatVoteAverage(String voteAverage){
            return voteAverage + "/10";
        }

        private String formatReleaseDate(String releaseDate){
            return releaseDate.substring(0,4);
        }

        /**
        * Take the String representing the movie results in JSON Format and
        * pull out the data we need for each movie
        *
        * Fortunately parsing is easy:  constructor takes the JSON string and converts it
        * into an Object hierarchy for us.
        */
        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // The API gives us 20 movies
            final int numMovies = 20;

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_PLOT = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_DATE = "release_date";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            Movie[] resultStrs = new Movie[numMovies];

            for(int i = 0; i < movieArray.length(); i++) {
                String originalTitle;
                String posterPath;
                String overview;
                String voteAverage;
                String releaseDate;

                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);

                // the title is in a String associated to the key "original_title"
                originalTitle = movie.getString(TMDB_TITLE);

                // the poster path is in a String associated to the key "poster_path"
                posterPath = movie.getString(TMDB_POSTER);

                // the plot synopsis is in a String associated to the key "overview"
                overview = movie.getString(TMDB_PLOT);

                // the rating is in a Double associated to the key "vote_average"
                voteAverage = String.valueOf(movie.getDouble(TMDB_RATING));

                // the release date is in a String associated to the key "release_date"
                releaseDate = movie.getString(TMDB_DATE);

                voteAverage = formatVoteAverage(voteAverage);
                releaseDate = formatReleaseDate(releaseDate);
                resultStrs[i] = new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate);
            }

            return resultStrs;

        }

        @Override protected Movie[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                for (Movie movie : result) {
                    mMovieAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
