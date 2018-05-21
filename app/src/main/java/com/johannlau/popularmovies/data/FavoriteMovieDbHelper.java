package com.johannlau.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    private static final String database_name = "favoriteMovie.db";

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_ALTER1 = "ALTER TABLE " +
            MovieContract.MovieEntry.TABLE_NAME + " ADD COLUMN " +
            MovieContract.MovieEntry.COLUMN_NAME_MOVIE_SYNOPSIS + " string;";



    public FavoriteMovieDbHelper(Context context){
        super(context,database_name,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_DB = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_NAME_MOVIE_SYNOPSIS + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING + " INTEGER NOT NULL," +
                MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RELEASEDATE + " TEXT NOT NULL "+ ");";
        db.execSQL(SQL_CREATE_MOVIE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            db.execSQL(DATABASE_ALTER1);
        }
    }
}
