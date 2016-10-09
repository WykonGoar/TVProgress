package com.example.wouter.tvprogress.model.API;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.wouter.tvprogress.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Wouter on 23-11-2015.
 */
public class ShowResourceAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<ShowResource> mResources;
    private List<ShowResource> mResourcesFilterList;
    private ValueFilter valueFilter;

    public ShowResourceAdapter(Context context, List<ShowResource> resources) {
        this.mContext = context;
        this.mResources = resources;
        this.mResourcesFilterList = resources;
    }

    @Override
    public int getCount() { return mResources.size(); }

    @Override
    public Object getItem(int position) { return mResources.get(position); }

    @Override
    public long getItemId(int position) {return mResources.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.resource_row, parent, false);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvResourceName);
        TextView tvURL = (TextView) rowView.findViewById(R.id.tvURL);

        ShowResource resource = mResources.get(position);

        tvTitle.setText("" + resource.getTitle());
        tvURL.setText("" + resource.getStatus());

        return rowView;
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
                ArrayList<ShowResource> filterList = new ArrayList<ShowResource>();
                for (int i = 0; i < mResourcesFilterList.size(); i++) {
                    if ((mResourcesFilterList.get(i).getTitle().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        ShowResource resource = new ShowResource();
                        resource.setTitle(mResourcesFilterList.get(i).getTitle());
                        resource.setStatus(mResourcesFilterList.get(i).getStatus());

                        filterList.add(resource);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mResourcesFilterList.size();
                results.values = mResourcesFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mResources = (ArrayList<ShowResource>) results.values;
            notifyDataSetChanged();
        }
    }
}
