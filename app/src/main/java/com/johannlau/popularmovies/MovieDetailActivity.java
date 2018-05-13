package com.johannlau.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.johannlau.popularmovies.data.FavoriteMovieDbHelper;
import com.johannlau.popularmovies.data.MovieContract;
import com.johannlau.popularmovies.databinding.MoviedetailActivityBinding;
import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.MovieDetail;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    MoviedetailActivityBinding binding;

    private SQLiteDatabase mDb;

    SharedPreferences mPreferences;
    private static final String favorite_choice = "favorite";
    private final int ADJUSTHEIGHT = 64;
    private final int ADJUSTWIDTH = 8;
    private final int HALF = 2;

    private MovieDetail movieDetail;

    private final String trailerLabel = "Movie_Data";
    private final String reviewLabel ="Review_Data";

    private boolean favorited = false;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.movie_detail);
        binding = DataBindingUtil.setContentView(this,R.layout.moviedetail_activity);

        //Expand the size of the movie poster image
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (metrics.heightPixels- ADJUSTHEIGHT)/ HALF;
        int width = (metrics.widthPixels- ADJUSTWIDTH)/ HALF;

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.movieDetailIv.getLayoutParams();
        params.height = height;
        params.width = width;
        binding.movieDetailIv.setLayoutParams(params);

        Intent startedIntent = getIntent();

        if(startedIntent.hasExtra(trailerLabel)){
            movieDetail = startedIntent.getParcelableExtra(trailerLabel);
            binding.movieTitleTv.setText(movieDetail.returnMovieTitle());
            Picasso.with(this).load(movieDetail.returnMoviePoster()).into(binding.movieDetailIv);
            binding.moviePlotTv.setText(movieDetail.returnPlotSynopsis());
            binding.movieRatingTv.setText(Integer.toString(movieDetail.returnVoteAverage()) + "/10");
            binding.movieReleaseTv.setText(MovieDbUtils.edit_Date(movieDetail.returnReleaseDate()));
        }
        binding.watchTrailerBt.setOnClickListener( new TrailerHandler());
        binding.readReviewBt.setOnClickListener(new ReviewHandler());

        //Data base initialization
        FavoriteMovieDbHelper database = new FavoriteMovieDbHelper(this);
        mDb = database.getWritableDatabase();

        //Shared Preference for Favorite Movie
        mPreferences = this.getPreferences(Context.MODE_PRIVATE);
        favorited = mPreferences.getBoolean(movieDetail.returnMovieTitle()+favorite_choice,favorited);
        if(favorited){
            binding.favoriteBtn.setColorFilter(Color.YELLOW);
        }
        else{
            binding.favoriteBtn.setColorFilter(Color.WHITE);
        }
    }

    public class TrailerHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = MovieDetailActivity.this;
            int movieID = movieDetail.returnMovieID();
            Class destinationActivity = MovieTrailerActivity.class;
            Intent intent = new Intent(context,destinationActivity);
            intent.putExtra(trailerLabel,movieID);
            startActivity(intent);
        }
    }

    public class ReviewHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = MovieDetailActivity.this;
            int movieID = movieDetail.returnMovieID();
            Class destinationActivity = MovieReviewActivity.class;
            Intent intent = new Intent(context,destinationActivity);
            intent.putExtra(reviewLabel,movieID);
            startActivity(intent);
        }
    }

    public void onClickFavoriteReview(View view) {

        if(favorited){
            Uri deletedUri = MovieContract.MovieEntry.CONTENT_URI;
            deletedUri = deletedUri.buildUpon().appendPath(Integer.toString(movieDetail.returnMovieID())).build();
            int deletedItem = getContentResolver().delete(deletedUri,null,null);
            Log.v(TAG,"Deleted: " + Integer.toString(deletedItem));
            changeButtonColor(favorited);
        }
        else{
            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID, movieDetail.returnMovieID());
            contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE,movieDetail.returnMovieTitle());

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues);
            if(uri != null ){
                Log.v(TAG,"Inserted: " + uri.toString());
            }
            changeButtonColor(favorited);
        }
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void changeButtonColor(boolean selection){
        SharedPreferences.Editor editor = mPreferences.edit();
        if(!selection){
            favorited = true;
            editor.putBoolean(movieDetail.returnMovieTitle()+favorite_choice,favorited);
            editor.apply();
            binding.favoriteBtn.setColorFilter(Color.YELLOW);
        }
        else{
            favorited = false;
            editor.putBoolean(movieDetail.returnMovieTitle()+favorite_choice,favorited);
            editor.apply();
            binding.favoriteBtn.setColorFilter(Color.WHITE);
        }
    }
}
