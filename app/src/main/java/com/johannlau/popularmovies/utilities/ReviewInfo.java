package com.johannlau.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewInfo implements Parcelable {

    private String reviewerName;
    private String review;

    public ReviewInfo(String reviewerName, String review){
        this.reviewerName = reviewerName;
        this.review = review;
    }

    public ReviewInfo(Parcel in) {
        this.reviewerName = in.readString();
        this.review = in.readString();
    }

    public String getReviewerName(){ return reviewerName; }
    public String getReview() { return review; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(review);
        dest.writeString(reviewerName);
    }

    public static final Parcelable.Creator<ReviewInfo> CREATOR = new Parcelable.Creator<ReviewInfo>() {

        @Override
        public ReviewInfo createFromParcel(Parcel source) {
            return new ReviewInfo(source);
        }

        @Override
        public ReviewInfo[] newArray(int size) {
            return new ReviewInfo[size];
        }
    };

}
