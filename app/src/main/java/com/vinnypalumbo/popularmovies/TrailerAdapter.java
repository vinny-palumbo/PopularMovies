package com.vinnypalumbo.popularmovies;

import android.app.Activity;
import android.content.Context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = null;

        if(convertView == null) {
            convertView  = inflater.inflate(R.layout.list_item_trailer, parent, false);
            TextView nameView = (TextView) convertView.findViewById(R.id.list_item_trailer_title);
            Trailer trailer = getItem(position);
            nameView.setText(trailer.name);
        }

        return convertView;
    }

}