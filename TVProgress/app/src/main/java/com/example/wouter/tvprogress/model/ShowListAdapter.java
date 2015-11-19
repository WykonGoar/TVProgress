package com.example.wouter.tvprogress.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wouter.tvprogress.R;

/**
 * Created by Wouter on 16-11-2015.
 */
public class ShowListAdapter extends ArrayAdapter<Show> {

    private Context mContext;
    private Show[] mShows;

    public ShowListAdapter(Context context, Show[] objects) {
        super(context, R.layout.show_row, objects);

        mContext = context;
        mShows = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.show_row, parent, false);

        ImageView ivShow = (ImageView) rowView.findViewById(R.id.ivShow);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvCurrentSeason = (TextView) rowView.findViewById(R.id.tvCurrentSeason);
        TextView tvCurrentEpisode = (TextView) rowView.findViewById(R.id.tvCurrentEpisode);

        Show show = mShows[position];
        tvTitle.setText(show.getTitle());
        tvCurrentSeason.setText("Season: " + show.getCurrentSeason());
        tvCurrentEpisode.setText("Episode: " + show.getCurrentEpisode());

        return  rowView;
    }
}
