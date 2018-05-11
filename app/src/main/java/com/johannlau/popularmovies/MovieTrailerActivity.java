package com.johannlau.popularmovies;


import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.*;
import android.util.*;
import android.widget.*;

import com.johannlau.popularmovies.adapters.TrailersAdapter;
import com.johannlau.popularmovies.utilities.*;

import java.net.*;
import java.util.*;
/*
    Example from Android Custom Array Adapter
 */
public class MovieTrailerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<TrailersInfo>> {


    private static final String TAG = MovieTrailerActivity.class.getSimpleName();
    private TrailersAdapter trailersAdapter;
    private ArrayList<TrailersInfo> trailersInfos;
    private  ListView listView;

    private static final int MOVIE_LOADER = 22;
    private static final String MOVIEDETAILS_EXTRA = "trailerdetail";

    private int movieID = 0;

    private final String intentLablel = "Movie_Data";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trailers_activity);
        setTitle(R.string.trailer_label);

        Intent startedIntent = getIntent();

        if(startedIntent.hasExtra(intentLablel)){
            movieID = startedIntent.getIntExtra(intentLablel,0);
        }
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(MOVIE_LOADER);
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIEDETAILS_EXTRA, movieID);
        if(loader == null){
            loaderManager.initLoader(MOVIE_LOADER,bundle,this);
        }
        else {
            loaderManager.restartLoader(MOVIE_LOADER, bundle, this);
        }

        listView = findViewById(R.id.trailer_lists);
    }

    @Override
    public Loader<ArrayList<TrailersInfo>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<TrailersInfo>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }
                forceLoad();
            }

            @Override
            public ArrayList<TrailersInfo> loadInBackground() {
                try{
                    if(isOnline()){
                        URL url = NetworkUtils.buildtrailerURL(movieID);
                        Log.v(TAG,url.toString());
                        String jsonTrailerResponse = NetworkUtils.getURLResponse(url);
                        ArrayList<TrailersInfo> jsonTrailerList = TrailerUtils.getTrailersDetails(jsonTrailerResponse);
                        return jsonTrailerList;
                    }
                    else{
                        Toast.makeText(MovieTrailerActivity.this,"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TrailersInfo>> loader, ArrayList<TrailersInfo> data) {
        if(data!=null){
            trailersInfos = data;
            trailersAdapter = new TrailersAdapter(MovieTrailerActivity.this,trailersInfos);
            listView.setAdapter(trailersAdapter);
        }
        else{
            Log.v(TAG,"Error: Setting OnLoadFinished");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TrailersInfo>> loader) {

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
