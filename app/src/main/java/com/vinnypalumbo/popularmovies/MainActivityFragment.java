package com.vinnypalumbo.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    MoviePoster[] moviePosters = {
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar),
            new MoviePoster(R.drawable.interstellar)
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new ImageAdapter(
                getActivity(),
                Arrays.asList(moviePosters)
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach the Adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }
}
