package com.johannlau.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private MoviesAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private ArrayList<String> moviesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviesList = new ArrayList<>();

        mAdapter = new MoviesAdapter(MainActivity.this,moviesList);
        new LoadMovieTask().execute();

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        int numberOfColumns = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,numberOfColumns));

        mRecyclerView.setAdapter(mAdapter);


    }
    public class LoadMovieTask extends AsyncTask<Void,Void,ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            URL url = NetworkUtils.buildURL(true);

            try {
                String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                ArrayList<String> jsonMovieList = MovieDbUtils.getMovieImages(MainActivity.this,jsonMovieResponse);
                return jsonMovieList;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            if( s!= null){
//                Picasso.Builder builder = new Picasso.Builder(MainActivity.this);
//                builder.listener(new Picasso.Listener()
//                {
//                    @Override
//                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
//                    {
//                        exception.printStackTrace();
//                    }
//                });
//                builder.build().load(s).into(mImageView);
                moviesList = s;
                mAdapter = new MoviesAdapter(MainActivity.this,moviesList);
                mRecyclerView.setAdapter(mAdapter);
            }
            else{
                Log.v(TAG,"Error: OnPostExecute Text");
            }
        }
    }
}
