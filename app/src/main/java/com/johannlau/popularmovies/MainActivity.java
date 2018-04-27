package com.johannlau.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private MoviesAdapter mAdapter;

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        RecyclerView recyclerView = findViewById(R.id.rv_movies);
//        int numberOfColumns = 2;
//        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));
        mTextView = findViewById(R.id.rv_movies);
        new LoadMovieTask().execute();
    }
    public class LoadMovieTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url = NetworkUtils.buildURL(true);

            try {
                String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                String jsonMovieTitle = MovieDbUtils.getMovieImages(MainActivity.this,jsonMovieResponse);
                Log.v(TAG,"Title:" + jsonMovieTitle);
                return jsonMovieTitle;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if( s!= null) mTextView.setText(s);
            else{
                Log.v(TAG,"Error: Setting Text");
            }
        }
    }
}
