package com.johannlau.popularmovies.utilities;

import android.net.Uri;
import android.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getName();

    //Include API Key
    private final static String apiKey = "";


    private final static String movieURL = "http://api.themoviedb.org/3/movie/?api_key=";

    private final static String popularURL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private final static String topRatedURL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";

    private final static String trailerURL = "http://api.themoviedb.org/3/movie//videos?api_key=";
    private final static String reviewURL = "http://api.themoviedb.org/3/movie//reviews?api_key=";
    //Sample URL https://api.themoviedb.org/3/movie/76341?api_key={api_key}

    public static URL buildsortURL(boolean choice) {
        String baseURL;
        if(choice)  baseURL = popularURL;
        else{ baseURL = topRatedURL; }

        Uri builtUri = Uri.parse(baseURL + apiKey).buildUpon()
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildmovieURL(int id){
        if (id == 0) {
            return null;
        }
        String videoID = Integer.toString(id);
        String baseURL = new StringBuilder(movieURL).insert(movieURL.length() - 10, videoID).toString();
        Log.v(TAG, baseURL);
        Uri builtUri = Uri.parse(baseURL + apiKey).buildUpon()
                .build();

        URL url = null;

        try {

            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildtrailerURL(int id){
        if(id == 0){
            return null;
        }
        String videoID = Integer.toString(id);
        String baseURL = new StringBuilder(trailerURL).insert(trailerURL.length()-16,videoID).toString();
        Log.v(TAG,baseURL);
        Uri builtUri = Uri.parse(baseURL + apiKey).buildUpon()
                .build();

        URL url = null;

        try {

            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewURL(int id){
        if(id == 0){
            return null;
        }
        String videoID = Integer.toString(id);
        String baseURL = new StringBuilder(reviewURL).insert(reviewURL.length()-17,videoID).toString();
        Uri builtUri = Uri.parse(baseURL + apiKey).buildUpon()
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
            Log.v(TAG,"Review: "+url.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
    public static String getURLResponse(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if(hasInput) {
                return scanner.next();
            }
            else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }
}
