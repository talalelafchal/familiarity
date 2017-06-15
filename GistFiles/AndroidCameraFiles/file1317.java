package com.module.candychat.net.adapter;

/**
 * Created by Phuc on 7/18/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.module.candychat.net.R;
import com.module.candychat.net.model.RelationsGroup;

import java.util.ArrayList;
import java.util.List;

public class TopicListViewAdapter extends BaseAdapter {

    private List<RelationsGroup.GroupEntity> groupList = new ArrayList<>();

    Context context;

    public TopicListViewAdapter(Context context, List<RelationsGroup.GroupEntity> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_topic_chat, parent, false);

            RelationsGroup.GroupEntity movie = groupList.get(position);
            viewHolder.txt_topic = (TextView) convertView.findViewById(R.id.txt_topic);
            viewHolder.txt_topic.setText(movie.getName());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private static class ViewHolder {
        TextView txt_topic;
    }
}
