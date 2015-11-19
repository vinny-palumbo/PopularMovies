package com.vinnypalumbo.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] movieArray = {
                "Batman",
                "Spiderman",
                "The Godfather",
                "Forrest Gump",
                "Pulp Fiction",
                "The Goodfellas",
                "Toy Story"
        };

        List<String> topMovies = new ArrayList<String>(
                Arrays.asList(movieArray)
        );

        ArrayAdapter<String> mMovieAdapter = new ArrayAdapter<String>(
                // Context: global info about app environment.
                // Allows access the system services and resources
                // We use the fragments containing the activity as the context
                getActivity(),
                // ID of grid item layout:
                // The Adapter needs to know the layout for each grid item
                R.layout.grid_item_movie,
                // ID of text view within that layout that we want to populate
                R.id.grid_item_movie_textview,
                // List of data that we want to populate the text view with
                topMovies
        );

        // Get a reference to the ListView, and attach the Adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }
}
