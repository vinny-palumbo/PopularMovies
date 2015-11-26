package com.vinnypalumbo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("title") && intent.hasExtra("poster") && intent.hasExtra("plot")
                && intent.hasExtra("rating") && intent.hasExtra("year")) {

            String titleStr = intent.getStringExtra("title");
            TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title);
            titleTextView.setText(titleStr);

            String posterStr = intent.getStringExtra("poster");
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.detail_poster);
            Picasso.with(getContext()).load(IMAGE_BASE_URL + posterStr).into(posterImageView);

            String plotStr = intent.getStringExtra("plot");
            TextView plotTextView = (TextView) rootView.findViewById(R.id.detail_plot);
            plotTextView.setText(plotStr);

            String ratingStr = intent.getStringExtra("rating");
            TextView ratingTextView = (TextView) rootView.findViewById(R.id.detail_rating);
            ratingTextView.setText(ratingStr);

            String yearStr = intent.getStringExtra("year");
            TextView yearTextView = (TextView) rootView.findViewById(R.id.detail_year);
            yearTextView.setText(yearStr);
        }

        return rootView;
    }
}
