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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vinnypalumbo.popularmovies.data.MovieContract;

/**
 * Created by Vincent on 2016-01-21.
 */

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

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

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_poster);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_plot);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_rating);
        mYearView = (TextView) rootView.findViewById(R.id.detail_year);

        return rootView;
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
        if (intent == null || intent.getData() == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";

        if (data != null && data.moveToFirst()) {
            // Read movie ID from cursor
            int movieId = data.getInt(COL_MOVIE_ID);

            // Read title from cursor and update view
            String title = data.getString(COL_MOVIE_TITLE);
            mTitleView.setText(title);

            // Read poster path from cursor and update view using Picasso
            String poster = data.getString(COL_MOVIE_POSTER);
            Picasso.with(getContext()).load(IMAGE_BASE_URL + poster).into(mPosterView);

            // Read plot synopsis from cursor and update view
            String plot = data.getString(COL_MOVIE_PLOT);
            mPlotView.setText(plot);

            // Read rating from cursor and update view
            String rating = Utility.formatVoteAverage(getActivity(),
                    data.getDouble(COL_MOVIE_RATING));
            mRatingView.setText(rating);

            // Read release date from cursor and update view
            String year = Utility.formatReleaseDate(
                    data.getInt(COL_MOVIE_DATE));
            mYearView.setText(year);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}