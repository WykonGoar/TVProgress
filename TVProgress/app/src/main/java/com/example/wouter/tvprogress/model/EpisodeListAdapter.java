package com.example.wouter.tvprogress.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.wouter.tvprogress.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Wouter on 16-11-2015.
 */
public class EpisodeListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LinkedList<Integer> mListDataHeader;
    private HashMap<Integer, LinkedList<Episode>> mListChildData;

    private DatabaseConnection mDatabaseConnection;

    public EpisodeListAdapter(Context context, LinkedList<Integer> listDataHeader, HashMap<Integer, LinkedList<Episode>> listChildData) {
        mContext = context;
        mListDataHeader = listDataHeader;
        mListChildData = listChildData;

        SQLiteDatabase mDatabase = mContext.openOrCreateDatabase("TVProgressDB", mContext.MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, mContext);
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
       return mListDataHeader.get(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int headerTitle = (Integer) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.episode_group_row, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText("Season " + headerTitle);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mListChildData.get(this.mListDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListChildData.get(this.mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.episode_row, null);
        }

        CheckBox cbSeen = (CheckBox) convertView.findViewById(R.id.cbSeen);
        TextView tvEpisode = (TextView) convertView.findViewById(R.id.tvEpisode);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        Episode episode = (Episode) getChild(groupPosition, childPosition);

        cbSeen.setChecked(episode.isSeen());

        tvEpisode.setText("Episode " + episode.getEpisode());
        tvTitle.setText("" + episode.getTitle());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


}
