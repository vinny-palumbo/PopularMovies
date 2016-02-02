package com.vinnypalumbo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link TrailerAdapter} exposes a list of trailers
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d("vinny-debug", "TrailerAdapter - newView");
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("vinny-debug", "TrailerAdapter - bindView");

        String trailerTitle;
        // Read trailer title from cursor
//        trailerTitle= cursor.getString(MovieFragment.COL_WATCHLIST_POSTER);
        trailerTitle = "The Revenant Official Trailer 1 2015 HD";

        TextView titleView = (TextView) view.findViewById(R.id.list_item_trailer_title);
        titleView.setText(trailerTitle);
    }
}