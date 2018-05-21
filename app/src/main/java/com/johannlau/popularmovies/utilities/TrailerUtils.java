package com.johannlau.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrailerUtils {

    private static final String TAG = TrailerUtils.class.getSimpleName();

    public static ArrayList<TrailersInfo> getTrailersDetails(String trailersJsonStr) throws JSONException {
        ArrayList<TrailersInfo> trailersList = new ArrayList<>();
        JSONObject trailersJSON = new JSONObject(trailersJsonStr);

        JSONArray results = trailersJSON.getJSONArray("results");
        for(int i = 0; i < results.length(); i++){
            JSONObject result = results.getJSONObject(i);
            String key = result.getString("key");
            String name = result.getString("name");

            TrailersInfo trailersInfo = new TrailersInfo(key,name);
            trailersList.add(trailersInfo);
        }
        return trailersList;

    }
}
