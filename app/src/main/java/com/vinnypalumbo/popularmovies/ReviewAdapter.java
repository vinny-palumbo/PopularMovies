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
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Activity context, List<Review> reviews){
        super(context, 0, reviews);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Review review = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);

        TextView authorView = (TextView) rootView.findViewById(R.id.list_item_review_author);
        String formattedAuthor = String.format(getContext().getString(R.string.format_author),review.author);
        authorView.setText(formattedAuthor);

        TextView contentView = (TextView) rootView.findViewById(R.id.list_item_review_content);
        contentView.setText(review.content);

        return rootView;
    }

}