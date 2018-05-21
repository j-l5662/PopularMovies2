package com.johannlau.popularmovies.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johannlau.popularmovies.R;
import com.johannlau.popularmovies.utilities.TrailersInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends ArrayAdapter<TrailersInfo> {

    private String TAG = TrailersAdapter.class.getSimpleName();
    public TrailersAdapter(Activity context, List<TrailersInfo> trailers){
        super(context,0,trailers);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final TrailersInfo trailersInfo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailerdetail_activity,parent,false);
        }

        ImageView iconView = convertView.findViewById(R.id.trailer_thumbnail);
        Picasso.with(getContext()).load(trailersInfo.getVideo_thumbnailURL()).into(iconView);
        TextView titleView = convertView.findViewById(R.id.trailer_title);
        titleView.setText(trailersInfo.getVideo_title());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url = trailersInfo.formVideo_URL();
               Uri youtube_link = Uri.parse(url);
               Intent intent = new Intent(Intent.ACTION_VIEW,youtube_link);
               if (intent.resolveActivity(getContext().getPackageManager()) !=null){
                    getContext().startActivity(intent);
                }

            }
        });
        return convertView;

    }
}
