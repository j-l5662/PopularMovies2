package com.johannlau.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private String TAG = MoviesAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<MovieDetail> movieList;

    final private ImageItemClickListener mOnClickListener;
    public interface ImageItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesAdapter(Context context, ArrayList<MovieDetail> arrayList,ImageItemClickListener listener) {
        this.context = context;
        this.movieList = arrayList;
        this.mOnClickListener = listener;
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
        Picasso.with(context).load(movieList.get(position).returnMoviePoster()).into(holder.listMovieView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
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
            Log.v(TAG,"Clicked");
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
