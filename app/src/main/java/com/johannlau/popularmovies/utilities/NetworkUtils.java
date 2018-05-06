package com.johannlau.popularmovies.utilities;

import android.net.Uri;

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

    private final static String popularURL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private final static String topRatedURL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";

    private final static String trailerURL = "http://api.themoviedb.org/3/movie//videos?api_key=";
    //Sample URL https://api.themoviedb.org/3/movie/76341?api_key={api_key}

    public static URL buildURL(boolean choice){
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

    public static URL buildtrailerURL(int id){
        if(id == 0){
            return null;
        }
        String videoID = Integer.toString(id);
        String baseURL = new StringBuilder(trailerURL).insert(trailerURL.length()-6,videoID).toString();

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
