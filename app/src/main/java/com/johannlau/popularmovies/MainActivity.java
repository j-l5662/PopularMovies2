package com.johannlau.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int COLUMNS = 2;
    private MoviesAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private ArrayList<MovieDetail> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();

        mAdapter = new MoviesAdapter(MainActivity.this,moviesList,this);
        new LoadMovieTask().execute(true);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,COLUMNS));

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.topRated_movie:
                new LoadMovieTask().execute(false);
                break;
            case R.id.popular_movie:
                new LoadMovieTask().execute(true);
                break;
            default:
                Log.v(TAG, "Error: Wrong Item");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        MovieDetail movieDetail = moviesList.get(clickedItemIndex);
        Class destinationActivity = MovieDetailActivity.class;
        Intent intent = new Intent(context,destinationActivity);
        intent.putExtra("Movie_Data",movieDetail);
        startActivity(intent);
    }

    public class LoadMovieTask extends AsyncTask<Boolean,Void,ArrayList<MovieDetail>> {

        @Override
        protected ArrayList<MovieDetail> doInBackground(Boolean... booleans) {
            Boolean choice = booleans[0];
            URL url = NetworkUtils.buildURL(choice);
            try {
                if (isOnline()){
                    String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                    ArrayList<MovieDetail> jsonMovieList = MovieDbUtils.getMovieDetails(MainActivity.this,jsonMovieResponse);
                    return jsonMovieList;
                }
                else {
                    return null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDetail> s) {
            if( s!= null){
                moviesList = s;
                mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
            else{
                Log.v(TAG,"Error: OnPostExecute Setting Adapter");
            }
        }
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
