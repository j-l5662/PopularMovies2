package com.johannlau.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewUtils {

    private static final String TAG = TrailerUtils.class.getSimpleName();

    public static ArrayList<ReviewInfo> getReviewDetails(String trailersJsonStr) throws JSONException {
        ArrayList<ReviewInfo> reviewsList = new ArrayList<>();
        JSONObject reviewsJSON = new JSONObject(trailersJsonStr);

        JSONArray results = reviewsJSON.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String reviewerName = result.getString("author");
            String review = result.getString("content");

            ReviewInfo reviewDetails = new ReviewInfo(reviewerName, review);
            reviewsList.add(reviewDetails);
        }
        return reviewsList;

    }
}
