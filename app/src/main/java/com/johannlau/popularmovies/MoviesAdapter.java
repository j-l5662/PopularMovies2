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
    private ArrayList<String> urlList;


    public MoviesAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.urlList = arrayList;
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


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
        Picasso.with(context).load(urlList.get(position)).into(holder.listMovieView);
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listMovieView;

        public MovieViewHolder(View itemView){
            super(itemView);
            listMovieView = itemView.findViewById(R.id.movie_view);
        }

        @Override
        public void onClick(View view){
            Toast.makeText(view.getContext(),"Clicked: Position:" +getAdapterPosition(),Toast.LENGTH_SHORT).show();

        }
    }
}
