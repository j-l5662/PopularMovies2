package com.johannlau.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class MovieDbUtils {

    private static final String TAG = MovieDbUtils.class.getName();

    private static final String baseURL = "http://image.tmdb.org/t/p/";
    private static final String phoneSize = "w185";

//    public static final String movieImageDirectory = getFilesDir().getAbsoluteFile()+"/" + MovieContract.MovieEntry.TABLE_NAME +"/";

    public static ArrayList<MovieDetail> getsortedMovieDetails(String moviesJsonStr) throws JSONException {

        ArrayList<MovieDetail> movieList = new ArrayList<>();

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        JSONArray results = moviesJSON.getJSONArray("results");
        for(int i = 0; i < results.length(); i++){
            JSONObject result = results.getJSONObject(i);
            int id = result.getInt("id");
            String movieImagePath = result.getString("poster_path");
            String releaseDate = result.getString("release_date");
            String plotSynopsis = result.getString("overview");
            String title = result.getString("original_title");
            int rating = result.getInt("vote_average");

            String movieImageURL = baseURL + phoneSize + movieImagePath;
            MovieDetail movieDetail = new MovieDetail(id,title,movieImageURL,plotSynopsis,rating,releaseDate);
            movieList.add(movieDetail);
        }
        return movieList;
    }


    public static MovieDetail getMovieDetails(String moviesJsonStr) throws JSONException {

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);



        int id = moviesJSON.getInt("id");
        String movieImagePath = moviesJSON.getString("poster_path");
        String releaseDate = moviesJSON.getString("release_date");
        String plotSynopsis = moviesJSON.getString("overview");
        String title = moviesJSON.getString("original_title");
        int rating = moviesJSON.getInt("vote_average");

        String movieImageURL = baseURL + phoneSize + movieImagePath;
        MovieDetail movieDetail = new MovieDetail(id,title,movieImageURL,plotSynopsis,rating,releaseDate);
        return movieDetail;
    }


    public static String edit_Date(String date){
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        return month+ '/' + day+ '/' + year;
    }
}
