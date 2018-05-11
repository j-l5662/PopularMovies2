package com.johannlau.popularmovies.data;

import android.provider.BaseColumns;

public class MovieContract {

    private MovieContract(){

    }

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME ="favoriteMovies";
        public static final String COLUMN_NAME_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_NAME_MOVIE_ID ="movieID";

    }
}
