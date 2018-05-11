package com.johannlau.popularmovies.utilities;

import android.os.*;

/*
    Class to hold Trailer Info:
    Trailer Youtube Video ID
    Trailer Name
    Trailer ThumbNail
 */
public class TrailersInfo implements Parcelable{

    private String video_id;
    private String video_title;
    private String video_thumbnailURL;

    public TrailersInfo(String video_id,String video_title){
        this.video_id = video_id;
        this.video_title = video_title;
        this.video_thumbnailURL = formVideo_thumbnailURL();
    }

    public TrailersInfo(Parcel in){
        this.video_id = in.readString();
        this.video_title = in.readString();
        this.video_thumbnailURL = in.readString();
    }

    public String getVideo_id(){
        return video_id;
    }

    public String getVideo_title(){
        return video_title;
    }

    public String getVideo_thumbnailURL(){
        return video_thumbnailURL;
    }

    public String formVideo_thumbnailURL(){
        String youtubeImg = "https://img.youtube.com/vi/";
        String file_ext = "/0.jpg";
        return youtubeImg + this.video_id + file_ext;
    }
    public String formVideo_URL(){
        String youtubeImg = "https://www.youtube.com/watch?v=";
        return youtubeImg + this.video_id;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(video_id);
        dest.writeString(video_title);
        dest.writeString(video_thumbnailURL);
    }

    public static final Parcelable.Creator<TrailersInfo> CREATOR = new Parcelable.Creator<TrailersInfo>() {
        @Override
        public TrailersInfo createFromParcel(Parcel source) {
            return new TrailersInfo(source);
        }

        @Override
        public TrailersInfo[] newArray(int size) {
            return new TrailersInfo[size];
        }
    };
}
