package com.johannlau.popularmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johannlau.popularmovies.R;
import com.johannlau.popularmovies.utilities.ReviewInfo;

import java.util.List;

public class ReviewsAdapter extends ArrayAdapter<ReviewInfo>{

    private String TAG = ReviewsAdapter.class.getSimpleName();

    private String HYPEN = "-";

    public ReviewsAdapter(Activity context, List<ReviewInfo> reviews){
        super(context,0,reviews);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ReviewInfo reviewInfo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reviews_activity,parent,false);
        }

        //Load Reviews into the activity and set up activity/layout
        TextView mReviewerName = convertView.findViewById(R.id.reviewer_name);
        mReviewerName.setText(HYPEN+reviewInfo.getReviewerName());
        TextView mReview = convertView.findViewById(R.id.movie_review);
        mReview.setText(reviewInfo.getReview());


        return convertView;

    }
}
