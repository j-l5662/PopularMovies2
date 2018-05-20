package com.johannlau.popularmovies.adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.johannlau.popularmovies.utilities.MovieDetail;
import com.johannlau.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private String TAG = MoviesAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<MovieDetail> movieList;

    private int mCount;

    final private ImageItemClickListener mOnClickListener;
    public interface ImageItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesAdapter(Context context, ArrayList<MovieDetail> arrayList,ImageItemClickListener listener,int count) {
        this.context = context;
        this.movieList = arrayList;
        this.mOnClickListener = listener;
        this.mCount = count;

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachParent = false;
        int height = parent.getMeasuredHeight()/2;
        View view = inflater.inflate(R.layout.recyclerview_movie,parent,attachParent);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        String movieURI = movieList.get(position).returnMoviePoster();
        if(movieURI.substring(0,5).equals("/data")){
            File file = new File(movieURI);
            Picasso.with(context).load(file).into(holder.listMovieView);
        }
        else {
            Picasso.with(context).load(movieURI).into(holder.listMovieView);
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void clearAdapter(){
        movieList.clear();
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listMovieView;

        public MovieViewHolder(View itemView){
            super(itemView);
            listMovieView = itemView.findViewById(R.id.movie_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
