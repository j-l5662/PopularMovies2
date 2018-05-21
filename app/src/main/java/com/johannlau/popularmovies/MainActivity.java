package com.johannlau.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.johannlau.popularmovies.adapters.MoviesAdapter;
import com.johannlau.popularmovies.data.FavoriteMovieDbHelper;
import com.johannlau.popularmovies.data.MovieContract;
import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.MovieDetail;
import com.johannlau.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener{

    private static final String TAG = MainActivity.class.getName();
    private static final int COLUMNS = 2;
    private static final String lifecycleCallback = "callback";
    private static final String MOVIEDETAILS_EXTRA = "Movie_Data";


    private static final int FAVORITE_MOVIE_LOADER = 12;
    private static final int MOVIES_LOADER = 22;

    private int selectionSort = 1;

    private SQLiteDatabase mDb;

    private MoviesAdapter mAdapter;


    private RecyclerView mRecyclerView;
    private ArrayList<MovieDetail> moviesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(lifecycleCallback)){
                selectionSort = savedInstanceState.getInt(lifecycleCallback);
            }
        }

        mAdapter = new MoviesAdapter(MainActivity.this,moviesList,this,moviesList.size());
        mAdapter.setHasStableIds(true);
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIEDETAILS_EXTRA, selectionSort);

        if( selectionSort < 2){
            movieQuery(bundle);
        }
        else{
            favoriteQuery(bundle);
        }


        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,COLUMNS));

        mRecyclerView.setAdapter(mAdapter);

        //Data base initialization
        FavoriteMovieDbHelper database = new FavoriteMovieDbHelper(this);
        mDb = database.getReadableDatabase();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(lifecycleCallback,selectionSort);
        super.onSaveInstanceState(outState);

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
        Bundle bundle;
        switch (id){
            case R.id.topRated_movie:
                selectionSort = 0;
                bundle = new Bundle();
                bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
                movieQuery(bundle);
                break;
            case R.id.popular_movie:
                selectionSort = 1;
                bundle = new Bundle();
                bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
                movieQuery(bundle);

                break;
            case R.id.favorite_movie:
                selectionSort = 2;
                bundle = new Bundle();
                bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
                favoriteQuery(bundle);
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
        intent.putExtra(MOVIEDETAILS_EXTRA,movieDetail);
        startActivity(intent);
    }

    private void movieQuery(Bundle bundle){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(MOVIES_LOADER);
        Loader<ArrayList<MovieDetail>> favLoader = loaderManager.getLoader(FAVORITE_MOVIE_LOADER);
        if(loader == null){
            loaderManager.initLoader(MOVIES_LOADER,bundle,querySortedMovieLoader);
        }
        else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, querySortedMovieLoader);
        }
        if(favLoader != null){
            loaderManager.destroyLoader(FAVORITE_MOVIE_LOADER);
        }
    }

    private void favoriteQuery(Bundle bundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> favLoader = loaderManager.getLoader(FAVORITE_MOVIE_LOADER);
        Loader<ArrayList<MovieDetail>> queryLoader = loaderManager.getLoader(MOVIES_LOADER);

        if(favLoader == null){
            loaderManager.initLoader(FAVORITE_MOVIE_LOADER,bundle,favoriteMovieLoader);
        }
        else {
            loaderManager.restartLoader(FAVORITE_MOVIE_LOADER, bundle, favoriteMovieLoader);
        }
        if(queryLoader != null){
            loaderManager.destroyLoader(MOVIES_LOADER);
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
    private LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>> querySortedMovieLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>() {
        @NonNull
        @Override
        public Loader<ArrayList<MovieDetail>> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieDetail>>(MainActivity.this) {
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
                    int choice = args.getInt(MOVIEDETAILS_EXTRA);
                    try {
                        if (isOnline()) {
                            URL url = NetworkUtils.buildsortURL(choice);
                            String jsonMovieResponse = NetworkUtils.getURLResponse(url);
                            ArrayList<MovieDetail> jsonMovieList = MovieDbUtils.getsortedMovieDetails(jsonMovieResponse);
                            return jsonMovieList;
                        } else {
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
                mAdapter.clearAdapter();
                mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());

                mRecyclerView.setAdapter(mAdapter);
                getLoaderManager().destroyLoader(loader.getId());
            }
            else{
                Toast.makeText(getApplicationContext(),"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {
        }
    };
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
                        if(isOnline()){
                            while(cursor.moveToNext()){
                                int movieIDColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
                                int movieID = cursor.getInt(movieIDColumn);
                                URL url = NetworkUtils.buildmovieURL(movieID);
                                String jsonMovieDetails = NetworkUtils.getURLResponse(url);
                                MovieDetail favMovie = MovieDbUtils.getMovieDetails(jsonMovieDetails);
                                favoritedMovies.add(favMovie);
                            }
                        }
                        else{
                            while(cursor.moveToNext()){
                                int movieIDColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
                                int movieID = cursor.getInt(movieIDColumn);
                                int movieTitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE);
                                int movieReleaseDate = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RELEASEDATE);
                                int movieRating = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING);
                                int movieSynopsis = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_SYNOPSIS);

                                String title = cursor.getString(movieTitle);
                                String release = cursor.getString(movieReleaseDate);
                                int rating = cursor.getInt(movieRating);
                                String synopsis = cursor.getString(movieSynopsis);
                                String picture =  getFilesDir().getAbsoluteFile()+"/" + MovieContract.MovieEntry.TABLE_NAME +"/movie"+ movieID +".jpg";
                                Log.v(TAG,picture);
                                MovieDetail favMovie = new MovieDetail(movieID,title,picture,synopsis,rating,release);

                                favoritedMovies.add(favMovie);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG,"Error Querying Data");
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
                if( data.size() == 0 ) {
                    Toast.makeText(MainActivity.this,"No Movies",Toast.LENGTH_SHORT).show();
                }
                moviesList = data;

                mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());

                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            else{
                Log.e(TAG,"Error: OnLoadFinished Setting Adapter");
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {
        }
    };
}
