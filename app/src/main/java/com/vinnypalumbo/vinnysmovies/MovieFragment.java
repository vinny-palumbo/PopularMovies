package com.vinnypalumbo.vinnysmovies;

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
import android.widget.AdapterView;
import android.widget.GridView;

import com.vinnypalumbo.vinnysmovies.data.MovieContract;
import com.vinnypalumbo.vinnysmovies.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static boolean isPopularitySelected = false;
    public static boolean isRatingSelected = false;
    public static boolean isWatchlistSelected = false;

    private MovieAdapter mMovieAdapter;

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIE_LOADER = 0;


    // For the popularity gridview we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] POPULARITY_COLUMNS = {
            MovieContract.PopularityEntry._ID,
            MovieContract.PopularityEntry.COLUMN_MOVIE_ID,
            MovieContract.PopularityEntry.COLUMN_TITLE,
            MovieContract.PopularityEntry.COLUMN_POSTER,
    };

    // For the rating gridview we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] RATING_COLUMNS = {
            MovieContract.RatingEntry._ID,
            MovieContract.RatingEntry.COLUMN_MOVIE_ID,
            MovieContract.RatingEntry.COLUMN_TITLE,
            MovieContract.RatingEntry.COLUMN_POSTER,
    };

    // For the watchlist gridview we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] WATCHLIST_COLUMNS = {
            MovieContract.WatchlistEntry._ID,
            MovieContract.WatchlistEntry.COLUMN_MOVIE_ID,
            MovieContract.WatchlistEntry.COLUMN_TITLE,
            MovieContract.WatchlistEntry.COLUMN_POSTER,
    };

    // These indices are tied to POPULARITY_COLUMNS.  If POPULARITY_COLUMNS changes, these
    // must change.
    static final int COL_POPULARITY_ID = 1;
//    static final int COL_POPULARITY_TITLE = 2;
    static final int COL_POPULARITY_POSTER = 3;

    // These indices are tied to RATING_COLUMNS.  If RATING_COLUMNS changes, these
    // must change.
    static final int COL_RATING_ID = 1;
//    static final int COL_RATING_TITLE = 2;
    static final int COL_RATING_POSTER = 3;

    // These indices are tied to WATCHLIST_COLUMNS.  If WATCHLIST_COLUMNS changes, these
    // must change.
    static final int COL_WATCHLIST_ID = 1;
//    static final int COL_WATCHLIST_TITLE = 2;
    static final int COL_WATCHLIST_POSTER = 3;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieIdUri);
    }

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("vinny-debug", "MovieFragment - onCreateView");

        // The COL_POPULARITY_POSTER or COL_RATING_POSTER will take data from a source and
        // use it to populate the GridView it's attached to.
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach the Adapter to it
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mMovieAdapter);

        // We'll call our MainActivity
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Uri movieIdUri;
                if(isWatchlistSelected){
                    movieIdUri = MovieContract.WatchlistEntry.buildMovieId(cursor.getInt(COL_WATCHLIST_ID));
                }else if(isRatingSelected) {
                    movieIdUri = MovieContract.RatingEntry.buildMovieId(cursor.getInt(COL_RATING_ID));
                }else{
                    movieIdUri = MovieContract.PopularityEntry.buildMovieId(cursor.getInt(COL_POPULARITY_ID));
                }

                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(movieIdUri);
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The gridview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("vinny-debug", "MovieFragment - onActivityCreated");
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the sort when we create the loader, all we need to do is restart things
    void onSortChanged( ) {
        Log.d("vinny-debug", "MovieFragment - onSortChanged:" + isWatchlistSelected);
        if(!isWatchlistSelected){
            updateMovies();
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
        Log.d("vinny-debug", "MovieFragment - updateMovies");
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("vinny-debug", "MovieFragment - onSaveInstanceState");
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("vinny-debug", "MovieFragment - onCreateLoader");
        if(isWatchlistSelected){
            Uri watchlistUri = MovieContract.WatchlistEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    watchlistUri,
                    WATCHLIST_COLUMNS,
                    null,
                    null,
                    null);
        }else if(isRatingSelected){
            Uri ratingUri = MovieContract.RatingEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    ratingUri,
                    RATING_COLUMNS,
                    null,
                    null,
                    null);
        }else{
            Uri popularityUri = MovieContract.PopularityEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    popularityUri,
                    POPULARITY_COLUMNS,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("vinny-debug", "MovieFragment - onLoadFinished");
        mMovieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d("vinny-debug", "MovieFragment - onLoaderReset");
        mMovieAdapter.swapCursor(null);
    }
}
