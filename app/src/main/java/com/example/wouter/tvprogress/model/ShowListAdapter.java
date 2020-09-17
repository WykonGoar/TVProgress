package com.example.wouter.tvprogress.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wouter.tvprogress.R;

import java.util.LinkedList;

/**
 * Created by Wouter on 16-11-2015.
 */
public class ShowListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<Show> mShows;
    private LinkedList<Show> mShowFilterList;
    private ValueFilter valueFilter;
    private DatabaseConnection mDatabaseConnection;

    public ShowListAdapter(Context context, LinkedList<Show> shows) {
        mContext = context;
        mShows = shows;
        mShowFilterList = shows;


        //SQLiteDatabase mDatabase = context.openOrCreateDatabase("TVProgressDB", context.MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(context);
    }

    @Override
    public int getCount() {
        return mShows.size();
    }

    @Override
    public Object getItem(int position) {
        return mShows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mShows.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.show_row, parent, false);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvWatchedAll = rowView.findViewById(R.id.tvWatchedAll);
//        ImageView cbWatchedAll = rowView.findViewById(R.id.cbWatchedAll);
//        TextView tvStatus = (TextView) rowView.findViewById(R.id.tvStatus);
        TextView tvCurrentSeason = (TextView) rowView.findViewById(R.id.tvCurrentSeason);
        TextView tvCurrentEpisode = (TextView) rowView.findViewById(R.id.tvCurrentEpisode);

        Show show = mShows.get(position);

        tvTitle.setText(show.getTitle());

        if (show.getWatchedAll())
            tvWatchedAll.setVisibility(View.VISIBLE);
        else
            tvWatchedAll.setVisibility(View.INVISIBLE);

        //        tvStatus.setText(show.getStatus());

//        if(!show.getURL().isEmpty()){
//            Episode lastSeenEpisode = mDatabaseConnection.getLastSeenEpisode(show.getId());
//
//            if(lastSeenEpisode != null) {
//                tvCurrentSeason.setText("Season: " + lastSeenEpisode.getSeason());
//                tvCurrentEpisode.setText("Episode: " + lastSeenEpisode.getEpisode());
//            }
//            else
//            {
//                tvCurrentSeason.setText("Season: 1");
//                tvCurrentEpisode.setText("Episode: 0");
//            }
//        }
//        else {
        tvCurrentSeason.setText("Season: " + show.getCurrentSeason());
        tvCurrentEpisode.setText("Episode: " + show.getCurrentEpisode());
//        }

        return  rowView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                LinkedList<Show> filterList = new LinkedList<Show>();
                for (int i = 0; i < mShowFilterList.size(); i++) {
                    if ((mShowFilterList.get(i).getTitle().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        Show show = new Show(mShowFilterList.get(i).getId(), mShowFilterList.get(i).getTitle(), mShowFilterList.get(i).getCurrentSeason(), mShowFilterList.get(i).getCurrentEpisode(), mShowFilterList.get(i).getBanner(), mShowFilterList.get(i).getURL(), mShowFilterList.get(i).getStatus());

                        filterList.add(show);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mShowFilterList.size();
                results.values = mShowFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShows = (LinkedList<Show>) results.values;
            notifyDataSetChanged();
        }
    }
}
