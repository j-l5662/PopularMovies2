package com.johannlau.popularmovies;


import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.johannlau.popularmovies.adapters.MoviesAdapter;
import com.johannlau.popularmovies.data.FavoriteMovieDbHelper;
import com.johannlau.popularmovies.data.MovieContract;
import com.johannlau.popularmovies.utilities.*;

import java.net.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>{

    private static final String TAG = MainActivity.class.getName();
    private static final int COLUMNS = 2;
    private static final String lifecycleCallback = "callback";
    private static final String MOVIEDETAILS_EXTRA = "movieDetail";

    private static final int MOVIE_LOADER = 20;
    private static final int MOVIES_LOADER = 22;


    private SQLiteDatabase mDb;

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

        mAdapter = new MoviesAdapter(MainActivity.this,moviesList,this,moviesList.size());
//        new LoadMovieTask().execute(selection_choice);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);
        movieQuery(bundle);

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,COLUMNS));

        mRecyclerView.setAdapter(mAdapter);

        //Data base initialization
        FavoriteMovieDbHelper database = new FavoriteMovieDbHelper(this);
        mDb = database.getReadableDatabase();
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
        Bundle bundle = new Bundle();
        switch (id){
            case R.id.topRated_movie:
                selection_choice = false;
//                new LoadMovieTask().execute(selection_choice);
                bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);
                movieQuery(bundle);
                break;
            case R.id.popular_movie:
                selection_choice = true;
//                new LoadMovieTask().execute(selection_choice);
                bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);
                movieQuery(bundle);
                break;
            case R.id.favorite_movie:
                // TODO: Add favorite sorting
                Cursor cursor = getAllFavMovies();
//                moviesList = data;
                mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());
                mRecyclerView.setAdapter(mAdapter);

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
    public Loader<ArrayList<MovieDetail>> onCreateLoader(final int id, @Nullable final Bundle args) {
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
                        URL url = NetworkUtils.buildsortURL(choice);
                        String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                        ArrayList<MovieDetail> jsonMovieList = MovieDbUtils.getsortedMovieDetails(jsonMovieResponse);
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
            mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            Log.v(TAG,"Error: OnLoadFinished Setting Adapter");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {

    }

    private void movieQuery(Bundle bundle){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(MOVIES_LOADER);

        if(loader == null){
            loaderManager.initLoader(MOVIES_LOADER,bundle,this);
        }
        else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, this);
        }
    }
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private Cursor getAllFavMovies() {
        return mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry._ID
        );
    }
}
