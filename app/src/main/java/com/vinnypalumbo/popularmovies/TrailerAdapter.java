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
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Activity context, List<Trailer> trailers){
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Trailer trailer = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);

        TextView nameView = (TextView) rootView.findViewById(R.id.list_item_trailer_title);
        nameView.setText(trailer.name);

        return rootView;
    }

}