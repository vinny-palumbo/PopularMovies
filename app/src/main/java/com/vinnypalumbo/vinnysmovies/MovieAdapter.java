package com.vinnypalumbo.vinnysmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * {@link MovieAdapter} exposes a list of movies
 * from a {@link android.database.Cursor} to a {@link android.widget.GridView}.
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";

        // Read poster path from cursor
        String posterPath;
        // If "My Watchlist" sort option selected, read from watchlist table
        if(MovieFragment.isWatchlistSelected){
            posterPath= cursor.getString(MovieFragment.COL_WATCHLIST_POSTER);
        }else if(MovieFragment.isRatingSelected) {
            posterPath= cursor.getString(MovieFragment.COL_RATING_POSTER);
        }else{
            posterPath= cursor.getString(MovieFragment.COL_POPULARITY_POSTER);
        }

        ImageView posterView = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
        Picasso.with(context).load(IMAGE_BASE_URL + posterPath).into(posterView);
    }
}