package com.vinnypalumbo.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.vinnypalumbo.popularmovies.data.MovieContract;

/**
 * Created by Vincent on 2016-01-21.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private ToggleButton mToggleButton;

    private int movieId;
    private String title;
    private String poster;
    private String plot;
    private double rating;
    private int year;


    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_DATE
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("vinny-debug", "DetailFragment - onCreateView");

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

        return rootView;
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
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
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
            // Read movie ID from cursor
            movieId = data.getInt(COL_MOVIE_ID);
            // change the state of the toggle button depending if movie is in watchlist or not
            mToggleButton.setChecked(isInWatchlist(movieId));

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
            String formattedRating = Utility.formatVoteAverage(getActivity(),rating);
            mRatingView.setText(formattedRating);

            // Read release date from cursor and update view
            year = data.getInt(COL_MOVIE_DATE);
            String formattedYear = Utility.formatReleaseDate(year);
            mYearView.setText(formattedYear);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}