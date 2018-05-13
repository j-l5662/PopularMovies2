package com.johannlau.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.johannlau.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "favoriteMovies";



    private MovieContract(){

    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME ="favoriteMovies";
        public static final String COLUMN_NAME_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_NAME_MOVIE_ID ="movieID";

    }
}
