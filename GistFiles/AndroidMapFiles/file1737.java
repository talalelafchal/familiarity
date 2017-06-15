package com.example.zdroa.fetchmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.TreeMap;

class ListViewAdaptor extends BaseAdapter {

    private Context mContext;
    private TreeMap<Integer, String> urls;
    private TreeMap<Integer, String> titles;


    ListViewAdaptor(Context context, TreeMap<Integer, String> URLS, TreeMap<Integer, String> TITLES) {
        mContext = context;
        urls = URLS;
        titles = TITLES;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.results_layout, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivPosters);
        TextView textView = (TextView) convertView.findViewById(R.id.results_layout_tv_movie_name);

        Drawable d = mContext.getResources().getDrawable(R.drawable.place_holder_img);


        String link_end = urls.ceilingEntry(position).getValue();
        String title = titles.ceilingEntry(position).getValue();

        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185" + link_end)
                .placeholder(d)
                .into(imageView);

        textView.setText(title);


        return convertView;
    }
}
