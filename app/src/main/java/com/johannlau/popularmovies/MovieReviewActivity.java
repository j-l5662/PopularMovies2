package com.johannlau.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.johannlau.popularmovies.adapters.ReviewsAdapter;
import com.johannlau.popularmovies.utilities.NetworkUtils;
import com.johannlau.popularmovies.utilities.ReviewInfo;
import com.johannlau.popularmovies.utilities.ReviewUtils;

import java.net.URL;
import java.util.ArrayList;

public class MovieReviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ReviewInfo>>  {

    private static final String TAG = MovieReviewActivity.class.getSimpleName();


    private static final int REVIEW_LOADER = 23;
    private static final String REVIEW_EXTRA = "reviewDetail";
    private final String reviewLabel ="Review_Data";
    private int movieID;
    private ListView mListView;
    private ArrayList<ReviewInfo> reviewInfos;
    private ReviewsAdapter mReviewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.review_label);
        setContentView(R.layout.reviewlist_activity);
        Intent startedIntent = getIntent();
        if(startedIntent.hasExtra(reviewLabel)) {
            movieID = startedIntent.getIntExtra(reviewLabel,0);
        }

        mListView = findViewById(R.id.reviews_list);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<ReviewInfo>> loader = loaderManager.getLoader(REVIEW_LOADER);
        Bundle bundle = new Bundle();
        bundle.putInt(REVIEW_EXTRA, movieID);

        if(loader == null) {
            loaderManager.initLoader(REVIEW_LOADER,bundle,this);
        }
        else {
            loaderManager.restartLoader(REVIEW_LOADER,bundle,this);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<ReviewInfo>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<ArrayList<ReviewInfo>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }
                forceLoad();
            }
            @Nullable
            @Override
            public ArrayList<ReviewInfo> loadInBackground() {
                try{
                    if(isOnline()){
                        URL url = NetworkUtils.buildReviewURL(movieID);
                        String jsonReviewResponse = NetworkUtils.getURLResponse(url);
                        ArrayList<ReviewInfo> reviewTrailersList = ReviewUtils.getReviewDetails(jsonReviewResponse);
                        return reviewTrailersList;

                    }
                    else{
                        Toast.makeText(MovieReviewActivity.this,"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ReviewInfo>> loader, ArrayList<ReviewInfo> data) {
        if(data!=null) {
            reviewInfos = data;
            mReviewAdapter = new ReviewsAdapter(this, reviewInfos);
            mListView.setAdapter(mReviewAdapter);
        }
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<ReviewInfo>> loader) {

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
