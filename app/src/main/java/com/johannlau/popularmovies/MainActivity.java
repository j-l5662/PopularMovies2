package com.johannlau.popularmovies;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.johannlau.popularmovies.data.MovieContract;
import com.johannlau.popularmovies.utilities.MovieDbUtils;
import com.johannlau.popularmovies.utilities.MovieDetail;
import com.johannlau.popularmovies.utilities.NetworkUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener{

    private static final String TAG = MainActivity.class.getName();
    private static final int COLUMNS = 2;
    private static final String lifecycleCallback = "callback";
    private static final String scrollPosition = "scrollcallback";
    private static final String MOVIEDETAILS_EXTRA = "Movie_Data";


    private static final int FAVORITE_MOVIE_LOADER = 12;
    private static final int MOVIES_LOADER = 22;

    private int selectionSort = 1;
    private int mPosition = 0;
    private Cursor mDb;

    private MoviesAdapter mAdapter;


    private RecyclerView mRecyclerView;
    private ArrayList<MovieDetail> moviesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,COLUMNS));


        if(mAdapter == null){
            mAdapter = new MoviesAdapter(MainActivity.this,moviesList,this);
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(lifecycleCallback)){
                selectionSort = savedInstanceState.getInt(lifecycleCallback);
            }
            if(savedInstanceState.containsKey(scrollPosition)){
                mPosition = savedInstanceState.getInt(scrollPosition);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIEDETAILS_EXTRA, selectionSort);
        if( selectionSort < 2){
            movieQuery(bundle);
        }
        else{
            favoriteQuery(bundle);
        }
        //Data base initialization using Content Providers
        ContentResolver contentResolver = getContentResolver();
        mDb = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mDb.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(scrollPosition,((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        outState.putInt(lifecycleCallback,selectionSort);
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

        QueryMovieLoader queryMovieLoader = new QueryMovieLoader(this);
        if(loader == null){
            loaderManager.initLoader(MOVIES_LOADER,bundle,queryMovieLoader);
        }
        else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, queryMovieLoader);
        }
        if(favLoader != null){
            loaderManager.destroyLoader(FAVORITE_MOVIE_LOADER);
        }
    }

    private void favoriteQuery(Bundle bundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> favLoader = loaderManager.getLoader(FAVORITE_MOVIE_LOADER);
        Loader<ArrayList<MovieDetail>> queryLoader = loaderManager.getLoader(MOVIES_LOADER);

        FavoriteMovieLoader favoriteMovieLoader = new FavoriteMovieLoader(this);
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

//
    private static class QueryMovieLoader implements  LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>{

        private WeakReference<MainActivity> activityWeakReference;

        QueryMovieLoader(MainActivity context){
            activityWeakReference = new WeakReference<>(context);
        }
        @NonNull
        @Override
        public Loader<ArrayList<MovieDetail>> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<ArrayList<MovieDetail>>(activityWeakReference.get()) {
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
                    MainActivity mainActivity = activityWeakReference.get();
                    int choice = args.getInt(MOVIEDETAILS_EXTRA);
                    try {
                        if (NetworkUtils.isOnline(mainActivity)) {
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
            MainActivity mainActivity = activityWeakReference.get();
            if( data!= null){
                mainActivity.moviesList = data;
                mainActivity.mAdapter.swapData(data);
                mainActivity.getLoaderManager().destroyLoader(loader.getId());
                if(mainActivity.mPosition > 0){
                    mainActivity.mRecyclerView.getLayoutManager().scrollToPosition(mainActivity.mPosition);
                }
            }
            else{
                Toast.makeText(mainActivity.getApplicationContext(),"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {

        }
    }
    private static class FavoriteMovieLoader implements LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>{

        private WeakReference<MainActivity> activityWeakReference;

        FavoriteMovieLoader(MainActivity context){
            activityWeakReference = new WeakReference<>(context);
        }
        @NonNull
        @Override
        public Loader<ArrayList<MovieDetail>> onCreateLoader(int id, @Nullable final Bundle args) {
            MainActivity mainActivity = activityWeakReference.get();
            return new AsyncTaskLoader<ArrayList<MovieDetail>>(mainActivity) {
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
                    MainActivity mainActivity = activityWeakReference.get();
                    ArrayList<MovieDetail> favoritedMovies = new ArrayList<>();
                    ContentResolver contentResolver = mainActivity.getContentResolver();
                    mainActivity.mDb = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                    try{
                        if(NetworkUtils.isOnline(mainActivity)){
                            while( mainActivity.mDb.moveToNext()){
                                int movieIDColumn =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
                                int movieID =  mainActivity.mDb.getInt(movieIDColumn);
                                URL url = NetworkUtils.buildmovieURL(movieID);
                                String jsonMovieDetails = NetworkUtils.getURLResponse(url);
                                MovieDetail favMovie = MovieDbUtils.getMovieDetails(jsonMovieDetails);
                                favoritedMovies.add(favMovie);
                            }
                        }
                        else{
                            while( mainActivity.mDb.moveToNext()){
                                int movieIDColumn =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
                                int movieID =  mainActivity.mDb.getInt(movieIDColumn);
                                int movieTitle =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE);
                                int movieReleaseDate =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RELEASEDATE);
                                int movieRating =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING);
                                int movieSynopsis =  mainActivity.mDb.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_SYNOPSIS);

                                String title =  mainActivity.mDb.getString(movieTitle);
                                String release =  mainActivity.mDb.getString(movieReleaseDate);
                                int rating =  mainActivity.mDb.getInt(movieRating);
                                String synopsis =  mainActivity.mDb.getString(movieSynopsis);
                                String picture =  mainActivity.getFilesDir().getAbsoluteFile()+"/" + MovieContract.MovieEntry.TABLE_NAME +"/movie"+ movieID +".jpg";
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
                        mainActivity.mDb.close();
                    }
                    return favoritedMovies;
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<MovieDetail>> loader, ArrayList<MovieDetail> data) {
            MainActivity mainActivity = activityWeakReference.get();
            if( data!= null){
                if( data.size() == 0 ) {
                    Toast.makeText(mainActivity,"No Movies",Toast.LENGTH_SHORT).show();
                }
                mainActivity.moviesList = data;
                mainActivity.mAdapter.swapData(data);
                mainActivity.mRecyclerView.swapAdapter(mainActivity.mAdapter,false);
                if(mainActivity.mPosition > 0){
                    mainActivity.mRecyclerView.getLayoutManager().scrollToPosition(mainActivity.mPosition);
                }
            }
            else{
                Log.e(TAG,"Error: OnLoadFinished Setting Adapter");
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {

        }
    }
}
