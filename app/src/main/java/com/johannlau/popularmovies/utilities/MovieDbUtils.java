package com.johannlau.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class MovieDbUtils {

    private static final String TAG = MovieDbUtils.class.getName();

    private static final String baseURL = "http://image.tmdb.org/t/p/";
    private static final String phoneSize = "w185";

    public static ArrayList<String> getMovieImages(Context context, String moviesJsonStr) throws JSONException {

        ArrayList<String> imageList = new ArrayList<>();

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        //Error handling here...

        JSONArray results = moviesJSON.getJSONArray("results");
        for(int i = 0; i < results.length(); i++){
            JSONObject result = results.getJSONObject(i);
            String movieImagePath = result.getString("poster_path");
            String movieImageURL = baseURL + phoneSize + movieImagePath;
            imageList.add(movieImageURL);
        }
        return imageList;
    }
}
