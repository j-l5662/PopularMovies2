package com.johannlau.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.johannlau.popularmovies.databinding.MoviedetailActivityBinding;
import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    MoviedetailActivityBinding binding;

    private final int ADJUSTHEIGHT = 64;
    private final int ADJUSTWIDTH = 8;
    private final int HALF = 2;

    private final String intentLablel = "Movie_Data";


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

        if(startedIntent.hasExtra(intentLablel)){
            MovieDetail movieDetail = startedIntent.getParcelableExtra(intentLablel);
            binding.movieTitleTv.setText(movieDetail.returnMovieTitle());
            Picasso.with(this).load(movieDetail.returnMoviePoster()).into(binding.movieDetailIv);
            binding.moviePlotTv.setText(movieDetail.returnPlotSynopsis());
            binding.movieRatingTv.setText(Integer.toString(movieDetail.returnVoteAverage()) + "/10");
            binding.movieReleaseTv.setText(MovieDbUtils.edit_Date(movieDetail.returnReleaseDate()));
        }



    }
}
