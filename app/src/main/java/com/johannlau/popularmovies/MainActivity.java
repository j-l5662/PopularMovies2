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

import com.facebook.stetho.Stetho;
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

    private static final int FAVORITE_MOVIE_LOADER = 20;
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

        Stetho.initialize(Stetho.newInitializerBuilder(this).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
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
                bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);
                movieQuery(bundle);
                break;
            case R.id.popular_movie:
                selection_choice = true;
                bundle.putBoolean(MOVIEDETAILS_EXTRA, selection_choice);
                movieQuery(bundle);
                break;
            case R.id.favorite_movie:
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(FAVORITE_MOVIE_LOADER);

                if(loader == null){
                    loaderManager.initLoader(FAVORITE_MOVIE_LOADER,bundle,favoriteMovieLoader);
                }
                else {
                    loaderManager.restartLoader(FAVORITE_MOVIE_LOADER, bundle, favoriteMovieLoader);
                }
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
    public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {    }

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

    private LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>> favoriteMovieLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>() {
        @NonNull
        @Override
        public Loader<ArrayList<MovieDetail>> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieDetail>>(MainActivity.this) {
                /**
                 * Subclasses must implement this to take care of loading their data,
                 * as per {@link #startLoading()}.  This is not called by clients directly,
                 * but as a result of a call to {@link #startLoading()}.
                 * This will always be called from the process's main thread.
                 */
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                    forceLoad();
                }

                @Nullable
                @Override
                public ArrayList<MovieDetail> loadInBackground() {
                    Cursor cursor = getAllFavMovies();
                    ArrayList<MovieDetail> favoritedMovies = new ArrayList<>();
                    try{
                        while(cursor.moveToNext()){
                            int movieIDColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
//                            int movieTitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE);
                            int movieID = cursor.getInt(movieIDColumn);
                            URL url = NetworkUtils.buildmovieURL(movieID);
                            Log.v(TAG,"Favorite Movie " +url.toString());
                            String jsonMovieDetails = NetworkUtils.getURLResponse(url);
                            MovieDetail favMovie = MovieDbUtils.getMovieDetails(jsonMovieDetails);
                            favoritedMovies.add(favMovie);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.v(TAG,"Error Querying Data");
                    }
                    finally {
                        cursor.close();
                    }
                    return favoritedMovies;
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
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {        }
    };
}
