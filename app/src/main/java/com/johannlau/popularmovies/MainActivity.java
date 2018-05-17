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

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ImageItemClickListener{

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

    private int selectionSort = 1;
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

      //  Stetho.initialize(Stetho.newInitializerBuilder(this).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle;
        if(selectionSort == 2){
            bundle = new Bundle();
            bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
            favoriteQuery(bundle);
        }
        else{
            bundle = new Bundle();
            bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
            movieQuery(bundle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(lifecycleCallback,selectionSort);
        //outState.putBoolean(lifecycleCallback,selection_choice);
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
                Log.v(TAG,"TopRated Selected " + Integer.toString(selectionSort));
                movieQuery(bundle);


                break;
            case R.id.popular_movie:
                selectionSort = 1;
                bundle = new Bundle();
                bundle.putInt(MOVIEDETAILS_EXTRA,selectionSort);
                Log.v(TAG,"Popular Selected " + Integer.toString(selectionSort));
                movieQuery(bundle);

                break;
            case R.id.favorite_movie:
                selectionSort = 2;
                bundle = new Bundle();
                Log.v(TAG,"Favorite Selected " + Integer.toString(selectionSort));
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
        intent.putExtra("Movie_Data",movieDetail);
        startActivity(intent);
    }

//    @NonNull
//    @Override
//    public Loader<ArrayList<MovieDetail>> onCreateLoader(final int id, @Nullable final Bundle args) {
//        return new AsyncTaskLoader<ArrayList<MovieDetail>>(this) {
//            @Override
//            protected void onStartLoading() {
//                super.onStartLoading();
//                if (args == null) {
//                    return;
//                }
//                forceLoad();
//            }
//
//            @Override
//            public ArrayList<MovieDetail> loadInBackground() {
//
//                int choice = args.getInt(MOVIEDETAILS_EXTRA);
//                try {
//                    if (isOnline()) {
//                        URL url = NetworkUtils.buildsortURL(choice);
//                        String jsonMovieResponse = NetworkUtils.getURLResponse(url);
//                        ArrayList<MovieDetail> jsonMovieList = MovieDbUtils.getsortedMovieDetails(jsonMovieResponse);
//                        return jsonMovieList;
//                    } else {
//                        Toast.makeText(MainActivity.this,"Error Connecting to Internet",Toast.LENGTH_SHORT).show();
//                        return null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        };
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<ArrayList<MovieDetail>> loader, ArrayList<MovieDetail> data) {
//        if( data!= null){
//            moviesList = data;
//            mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());
//            mAdapter.notifyDataSetChanged();
//            mRecyclerView.setAdapter(mAdapter);
//            Log.v(TAG,"!@#@!#@onloadfinish" + Integer.toString(selectionSort));
//        }
//        else{
//            Log.v(TAG,"Error: OnLoadFinished Setting Adapter");
//        }
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {    }

    private void movieQuery(Bundle bundle){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(MOVIES_LOADER);

        if(loader == null){
            loaderManager.initLoader(MOVIES_LOADER,bundle,querySortedMovieLoader);
        }
        else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, querySortedMovieLoader);
        }
    }

    private void favoriteQuery(Bundle bundle) {

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieDetail>> loader = loaderManager.getLoader(FAVORITE_MOVIE_LOADER);

        if(loader == null){
            loaderManager.initLoader(FAVORITE_MOVIE_LOADER,bundle,favoriteMovieLoader);
        }
        else {
            loaderManager.restartLoader(FAVORITE_MOVIE_LOADER, bundle, favoriteMovieLoader);
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
                    Log.v(TAG,"QUERY!! " + Integer.toString(choice));
                    try {
                        if (isOnline()) {
                            URL url = NetworkUtils.buildsortURL(choice);
                            Log.v(TAG,"Loaded: "+url.toString());
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
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                getLoaderManager().destroyLoader(loader.getId());
            }
            else{
                Log.v(TAG,"Error: OnLoadFinished Setting Adapter");
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {
            Log.v(TAG,"QUERY RESET");
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
                        while(cursor.moveToNext()){
                            int movieIDColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID);
//                            int movieTitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE);
                            int movieID = cursor.getInt(movieIDColumn);
                            URL url = NetworkUtils.buildmovieURL(movieID);
                            //Log.v(TAG,"Favorite Movie " +url.toString());
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
                if( data.size() == 0 ) {
                    Toast.makeText(MainActivity.this,"No Favorites",Toast.LENGTH_SHORT).show();
                }
                moviesList = data;

                mAdapter = new MoviesAdapter(MainActivity.this,moviesList,MainActivity.this,moviesList.size());
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().destroyLoader(loader.getId());
            }
            else{
                Log.v(TAG,"Error: OnLoadFinished Setting Adapter");
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {
            Log.v(TAG,"Favorite RESET");
        }
    };
}
