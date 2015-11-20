package com.vinnypalumbo.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vincent on 2015-11-19.
 */
public class ImageAdapter extends ArrayAdapter<MoviePoster> {

    public ImageAdapter(Activity context, List<MoviePoster> moviePosters){
        super(context, 0, moviePosters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MoviePoster moviePoster = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);

        TextView posterImage = (TextView) rootView.findViewById(R.id.grid_item_movie_textview);
        posterImage.setText(moviePoster.image);

        return rootView;
    }

}
