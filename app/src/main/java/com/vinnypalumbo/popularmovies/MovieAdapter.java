package com.vinnypalumbo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link MovieAdapter} exposes a list of movies
 * from a {@link android.database.Cursor} to a {@link android.widget.GridView}.
 */
public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is ported from FetchMovieTask --- but now we go straight from the cursor to the
        Movie.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        return cursor.getString(MovieFragment.COL_MOVIE_POSTER);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}