package com.johannlau.popularmovies;

import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.view.*;

import com.johannlau.popularmovies.data.FavoriteMovieDbHelper;
import com.johannlau.popularmovies.databinding.MoviedetailActivityBinding;
import com.johannlau.popularmovies.utilities.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    MoviedetailActivityBinding binding;

    private SQLiteDatabase mDb;

    private final int ADJUSTHEIGHT = 64;
    private final int ADJUSTWIDTH = 8;
    private final int HALF = 2;

    private MovieDetail movieDetail;

    private final String trailerLabel = "Movie_Data";
    private final String reviewLabel ="Review_Data";


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
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
