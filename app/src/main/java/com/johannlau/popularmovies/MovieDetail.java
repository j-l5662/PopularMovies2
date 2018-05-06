package com.johannlau.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
/*
    Movie Detail class that holds details for the movie.
    Movie Title
    Movie Poster image path
    Movie Plot Synopsis
    Movie Average Vote
    Movie Release Date
 */
public class MovieDetail implements Parcelable{
    private int movieID;
    private String movieTitle;
    private String moviePoster;
    private String plotSynopsis;
    private int voteAverage;
    private String releaseDate;


    public MovieDetail(int movieID, String movieTitle, String moviePoster, String plotSynopsis, int voteAverage, String releaseDate){
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.plotSynopsis = plotSynopsis;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
    private MovieDetail(Parcel in){
        this.movieID = in.readInt();
        this.movieTitle = in.readString();
        this.moviePoster = in.readString();
        this.plotSynopsis = in.readString();
        this.voteAverage = in.readInt();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String returnMovieTitle(){
        return this.movieTitle;
    }
    public String returnMoviePoster(){
        return this.moviePoster;
    }
    public String returnPlotSynopsis(){
        return this.plotSynopsis;
    }
    public int returnVoteAverage(){
        return this.voteAverage;
    }
    public String returnReleaseDate(){
        return this.releaseDate;
    }

    public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>(){
        @Override
        public MovieDetail createFromParcel(Parcel source) {
            return new MovieDetail(source);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieID);
        dest.writeString(movieTitle);
        dest.writeString(moviePoster);
        dest.writeString(plotSynopsis);
        dest.writeInt(voteAverage);
        dest.writeString(releaseDate);
    }
}
