package com.johannlau.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class MovieDbUtils {

    private static final String TAG = MovieDbUtils.class.getName();

    public static String getMovieImages(Context context, String moviesJsonStr) throws JSONException {


        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        //Error handling here...

        JSONArray results = moviesJSON.getJSONArray("results");
        JSONObject firstResults = results.getJSONObject(0);
        String firstMovie = firstResults.getString("title");

        Log.v(TAG,"String = " + firstMovie);

        return firstMovie;
    }
}
