package com.johannlau.popularmovies;


import android.content.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.johannlau.popularmovies.utilities.*;

import java.net.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>{

    private static final String TAG = MainActivity.class.getName();
    private static final int COLUMNS = 2;
    private static final String lifecycleCallback = "callback";
    private static final String MOVIEDETAILS_EXTRA = "moviedetail";
    private static final int MOVIE_LOADER = 22;

    private MoviesAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private ArrayList<MovieDetail> moviesList;
    private boolean selection_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();
        selection_choice = true;
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(lifecycleCallback)){
                selection_choice = savedInstanceState.getBoolean(lifecycleCallback);
            }
        }

        mAdapter = new MoviesAdapter(MainActivity.this,moviesList,this);
//        new LoadMovieTask().execute(selection_choice);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(MOVIE_LOADER);

        if(loader == null){
            loaderManager.initLoader(MOVIE_LOADER,bundle,this);
        }
        else {
            loaderManager.restartLoader(MOVIE_LOADER, bundle, this);
        }
        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,COLUMNS));

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(lifecycleCallback,selection_choice);
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
                selection_choice = false;
//                new LoadMovieTask().execute(selection_choice);
                break;
            case R.id.popular_movie:
                selection_choice = true;
//                new LoadMovieTask().execute(selection_choice);
                break;
            case R.id.favorite_movie:
                // TODO: Add favorite sorting
//                selection_choice = true;
//                new LoadMovieTask().execute(selection_choice);
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

    @NonNull
    @Override
    public Loader<ArrayList<MovieDetail>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<ArrayList<MovieDetail>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                forceLoad();
            }

            @Override
            public ArrayList<MovieDetail> loadInBackground() {
                boolean choice = args.getBoolean(MOVIEDETAILS_EXTRA);
                try {

                    if (isOnline()) {
                        URL url = NetworkUtils.buildURL(choice);
                        String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                        ArrayList<MovieDetail> jsonMovieList = MovieDbUtils.getMovieDetails(MainActivity.this, jsonMovieResponse);
                        return jsonMovieList;
                    } else {
                        Toast.makeText(MainActivity.this,"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }


        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MovieDetail>> loader, ArrayList<MovieDetail> data) {
        if( data!= null){
            moviesList = data;
            mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            Log.v(TAG,"Error: OnLoadFinished Setting Adapter");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
