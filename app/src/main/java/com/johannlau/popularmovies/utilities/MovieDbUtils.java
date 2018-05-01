package com.johannlau.popularmovies.utilities;

import android.content.Context;
import android.graphics.Movie;
import android.util.Log;

import com.johannlau.popularmovies.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class MovieDbUtils {

    private static final String TAG = MovieDbUtils.class.getName();

    private static final String baseURL = "http://image.tmdb.org/t/p/";
    private static final String phoneSize = "w185";

    public static ArrayList<MovieDetail> getMovieDetails(Context context, String moviesJsonStr) throws JSONException {

        ArrayList<MovieDetail> movieList = new ArrayList<>();

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        JSONArray results = moviesJSON.getJSONArray("results");
        for(int i = 0; i < results.length(); i++){
            JSONObject result = results.getJSONObject(i);

            String movieImagePath = result.getString("poster_path");
            String releaseDate = result.getString("release_date");
            String plotSynopsis = result.getString("overview");
            String title = result.getString("original_title");
            int rating = result.getInt("vote_average");

            String movieImageURL = baseURL + phoneSize + movieImagePath;
            MovieDetail movieDetail = new MovieDetail(title,movieImageURL,plotSynopsis,rating,releaseDate);
            movieList.add(movieDetail);
        }
        return movieList;
    }

    public static String edit_Date(String date){
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        return month+ '/' + day+ '/' + year;
    }
}
