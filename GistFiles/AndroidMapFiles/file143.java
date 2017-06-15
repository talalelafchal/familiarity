package com.sosyolobi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by ibrahimaltinoluk on 12.08.2013.
 */
public class CustomAdapter extends BaseAdapter {
    static final String KEY_TITLE = "title";
    static final String KEY_DATE = "pubDate";
    static final String KEY_DESC = "description";
    static final String KEY_LINK = "link";
    static final String KEY_IMG = "img";
    static final String KEY_CONT = "content:encoded";


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            
            holder.position = position;
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map = data.get(position);

        new DownloadImageTask(position, holder)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, map.get(KEY_IMG));

        holder.title.setText(map.get(KEY_TITLE));
        holder.date.setText(map.get(KEY_DATE));
        notifyDataSetChanged();
        return convertView;
    }


}




