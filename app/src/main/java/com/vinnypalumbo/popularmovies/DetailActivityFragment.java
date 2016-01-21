package com.vinnypalumbo.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vinnypalumbo.popularmovies.data.MovieContract;

/**
 * Created by Vincent on 2016-01-21.
 */

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private String mMovie;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
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

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//      final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";

//            String posterStr = intent.getStringExtra("poster");
//            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.detail_poster);
//            Picasso.with(getContext()).load(IMAGE_BASE_URL + posterStr).into(posterImageView);

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String movieId = Utility.formatMovieId(
                data.getInt(COL_MOVIE_ID));

        String title = data.getString(COL_MOVIE_TITLE);

        String poster = data.getString(COL_MOVIE_POSTER);

        String plot = data.getString(COL_MOVIE_PLOT);

        String rating = Utility.formatVoteAverage(getActivity(),
                data.getDouble(COL_MOVIE_RATING));

        String year = Utility.formatReleaseDate(
                data.getInt(COL_MOVIE_DATE));

        mMovie = String.format("%s \n %s \n %s \n %s \n %s \n %s ", movieId, title, poster, plot, rating, year);

        TextView detailTextView = (TextView)getView().findViewById(R.id.detail_plot);
        detailTextView.setText(mMovie);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}