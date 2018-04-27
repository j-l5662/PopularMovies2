package com.johannlau.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachtoParent = false;

//        View view = inflater.inflate();
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView listMovieView;

        public MovieViewHolder(View itemView){
            super(itemView);

//            listMovieView = itemView.findViewById(R.id.movie_iv);
        }
// Create imageview from api and then use a grid recycler view layout manager
        void bind(Bitmap bitmap){
            listMovieView.setImageBitmap(bitmap);
        }
    }
}
