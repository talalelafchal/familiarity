package com.example.untitled3;

import java.util.ArrayList;

import android.content.Context;
import com.example.untitled3.Feed;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: zemin
 * Date: 19.03.2013
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class FeedItemAdapter extends ArrayAdapter<Feed> {

    private ArrayList<Feed> feedArrayList;
    private Activity activity;
    public ImageManager imageManager;


    public FeedItemAdapter(Activity a, int textViewResourceId, ArrayList<Feed> feedArrayList){
        super(a,textViewResourceId,feedArrayList);
        this.feedArrayList = feedArrayList;
        activity = a;
        imageManager =
                new ImageManager(activity.getApplicationContext(),100);
    }

    public static class ViewHolder{
        public TextView username;
        public TextView text;
        public ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.username = (TextView) v.findViewById(R.id.username);
            holder.text = (TextView) v.findViewById(R.id.text);
            holder.image = (ImageView) v.findViewById(R.id.image);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();

        final Feed feed = feedArrayList.get(position);
        if (feed != null) {
            holder.username.setText(feed.username);
            holder.text.setText(feed.text);
            holder.image.setTag(feed.image_url);
            imageManager.displayImage(feed.image_url, holder.image,position);
        }
        return v;
    }

}
