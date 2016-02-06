package com.vinnypalumbo.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.views.ExpandedListView;
import com.squareup.picasso.Picasso;
import com.vinnypalumbo.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016-01-21.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String TRAILER_SHARE_HASHTAG = " #PopularMoviesApp";

    public static String mTrailerShareText;
    public static ShareActionProvider mShareActionProvider;

    private Uri mUri;
    private ToggleButton mToggleButton;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private int movieId;
    public static String title; // needed for action share text in FetchTrailersTask's onPostExecute()
    private String poster;
    private String plot;
    private double rating;
    public static int year; // needed for action share text in FetchTrailersTask's onPostExecute()


    private static final int DETAIL_LOADER = 0;

    private static final String[] POPULARITY_DETAIL_COLUMNS = {
            MovieContract.PopularityEntry._ID,
            MovieContract.PopularityEntry.COLUMN_MOVIE_ID,
            MovieContract.PopularityEntry.COLUMN_TITLE,
            MovieContract.PopularityEntry.COLUMN_POSTER,
            MovieContract.PopularityEntry.COLUMN_PLOT,
            MovieContract.PopularityEntry.COLUMN_RATING,
            MovieContract.PopularityEntry.COLUMN_DATE
    };

    private static final String[] RATING_DETAIL_COLUMNS = {
            MovieContract.RatingEntry._ID,
            MovieContract.RatingEntry.COLUMN_MOVIE_ID,
            MovieContract.RatingEntry.COLUMN_TITLE,
            MovieContract.RatingEntry.COLUMN_POSTER,
            MovieContract.RatingEntry.COLUMN_PLOT,
            MovieContract.RatingEntry.COLUMN_RATING,
            MovieContract.RatingEntry.COLUMN_DATE
    };

    private static final String[] WATCHLIST_DETAIL_COLUMNS = {
            MovieContract.WatchlistEntry._ID,
            MovieContract.WatchlistEntry.COLUMN_MOVIE_ID,
            MovieContract.WatchlistEntry.COLUMN_TITLE,
            MovieContract.WatchlistEntry.COLUMN_POSTER,
            MovieContract.WatchlistEntry.COLUMN_PLOT,
            MovieContract.WatchlistEntry.COLUMN_RATING,
            MovieContract.WatchlistEntry.COLUMN_DATE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER = 3;
    static final int COL_MOVIE_PLOT = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_DATE = 6;

    private TextView mTitleView;
    private ImageView mPosterView;
    private TextView mPlotView;
    private TextView mRatingView;
    private TextView mYearView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("vinny-debug", "DetailFragment - onCreateView");

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        // initialize the trailer and review adapters
        mTrailerAdapter =
                new TrailerAdapter(
                        getActivity(), // The current context (this activity)
                        new ArrayList<Trailer>());

        mReviewAdapter =
                new ReviewAdapter(
                        getActivity(), // The current context (this activity)
                        new ArrayList<Review>());

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get a reference to the Trailers ListView, and attach this adapter to it.
        ExpandedListView trailerListView = (ExpandedListView) rootView.findViewById(R.id.listview_trailer);
        trailerListView.setAdapter(mTrailerAdapter);
        // when you click on a trailer item, launch youtube with an intent and pass the key to the video
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Trailer trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/" + trailer.key));
                startActivity(intent);
            }
        });

        // Get a reference to the Reviews ListView, and attach this adapter to it.
        ExpandedListView reviewListView = (ExpandedListView) rootView.findViewById(R.id.listview_reviews);
        reviewListView.setAdapter(mReviewAdapter);

        // link the text and image views to their associated IDs
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_poster);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_plot);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_rating);
        mYearView = (TextView) rootView.findViewById(R.id.detail_year);

        // add or delete movie from watchlist when toggle button changed
        mToggleButton = (ToggleButton) rootView.findViewById(R.id.detail_favorite);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addToWatchlist(movieId, title, poster, plot, rating, year);
                } else {
                    // The toggle is disabled, delete from watchlist
                    removeFromWatchlist(movieId);
                    Toast.makeText(getContext(), R.string.button_off_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /* Without this single line of code, the app crashes when detail view
         * displays a movie that has been added to watchlist and user rotates device
         * It took me 4-5 hours to find the solution
         */
        mToggleButton.setSaveEnabled(false);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share_trailer);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mTrailerShareText != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    public static Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mTrailerShareText + TRAILER_SHARE_HASHTAG);
        return shareIntent;
    }

    boolean isInWatchlist(int movieId){
        Log.d("vinny-debug", "DetailFragment - isInWatchlist");
        boolean isInWatchlist = false;
        // check if the movie with this ID exists in the db
        Cursor watchlistCursor = getContext().getContentResolver().query(
                MovieContract.WatchlistEntry.CONTENT_URI,
                new String[]{MovieContract.WatchlistEntry._ID},
                MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        if (watchlistCursor.moveToFirst()) {
            isInWatchlist = true;
        }
        watchlistCursor.close();

        return isInWatchlist ;
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
        Log.d("vinny-debug", "DetailFragment - addToWatchlist");
        long watchlistId;

        // First, check if the movie with this ID exists in the db
        Cursor watchlistCursor = getContext().getContentResolver().query(
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
            Uri insertedUri = getContext().getContentResolver().insert(
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

    void removeFromWatchlist(int movieId){
        Log.d("vinny-debug", "DetailFragment - removeFromWatchlist");

        // Finally, insert movie data into the watchlist database.
        getContext().getContentResolver().delete(
                MovieContract.WatchlistEntry.CONTENT_URI,
                MovieContract.WatchlistEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Integer.toString(movieId)}
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("vinny-debug", "DetailFragment - onActivityCreated");
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("vinny-debug", "DetailFragment - onCreateLoader");
        if (null != mUri && MovieFragment.isWatchlistSelected) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    WATCHLIST_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }else if (null != mUri && MovieFragment.isRatingSelected) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    RATING_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }else if(null != mUri && MovieFragment.isPopularitySelected){
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    POPULARITY_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("vinny-debug", "DetailFragment - onLoadFinished");

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";

        if (data != null && data.moveToFirst()) {

            // Read movie ID from cursor to execute FetchTrailersTask and FetchReviewsTask
            movieId = data.getInt(COL_MOVIE_ID);
            // change the state of the toggle button depending if movie is in watchlist or not
            mToggleButton.setChecked(isInWatchlist(movieId));
            // Execute FetchTrailersTask with movieId as a param
            FetchTrailersTask fetchTrailersTask= new FetchTrailersTask(getActivity(), mTrailerAdapter);
            fetchTrailersTask.execute(String.valueOf(movieId));
            // Execute FetchReviewsTask with movieId as a param
            FetchReviewsTask fetchReviewsTask= new FetchReviewsTask(getActivity(), mReviewAdapter);
            fetchReviewsTask.execute(String.valueOf(movieId));

            // Read title from cursor and update view
            title = data.getString(COL_MOVIE_TITLE);
            mTitleView.setText(title);

            // Read poster path from cursor and update view using Picasso
            poster = data.getString(COL_MOVIE_POSTER);
            Picasso.with(getContext()).load(IMAGE_BASE_URL + poster).into(mPosterView);

            // Read plot synopsis from cursor and update view
            plot = data.getString(COL_MOVIE_PLOT);
            mPlotView.setText(plot);

            // Read rating from cursor and update view
            rating = data.getDouble(COL_MOVIE_RATING);
            String formattedRating = Utility.formatVoteAverage(getActivity(), rating);
            mRatingView.setText(formattedRating);

            // Read release date from cursor and update view
            year = data.getInt(COL_MOVIE_DATE);
            String formattedYear = Utility.formatReleaseDate(year);
            mYearView.setText(formattedYear);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}