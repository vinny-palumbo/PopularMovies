package com.vinnypalumbo.vinnysmovies;

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
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Activity context, List<Review> reviews){
        super(context, 0, reviews);
    }

    // disable click events on the items of the Review ListView
    @Override
    public boolean isEnabled(int position) {
        return false;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = null;

        if(convertView == null) {
            convertView  = inflater.inflate(R.layout.list_item_review, parent, false);
            Review review = getItem(position);

            TextView authorView = (TextView) convertView.findViewById(R.id.list_item_review_author);
            TextView contentView = (TextView) convertView.findViewById(R.id.list_item_review_content);

            String formattedAuthor = String.format(getContext().getString(R.string.format_author),review.author);

            authorView.setText(formattedAuthor);
            contentView.setText(review.content);
        }

        return convertView;
    }

}