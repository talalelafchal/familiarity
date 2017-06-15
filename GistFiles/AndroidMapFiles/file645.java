package com.andraskindler.sandbox.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class WeatherListAdapter extends BaseAdapter {

    private List<WeatherData> items;

    public WeatherListAdapter() {
        items = new ArrayList<>();
    }

    public void update(final WeatherData weatherData){
        items.add(weatherData);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public WeatherData getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textView = new TextView(parent.getContext());
        textView.setText(items.get(position).name + "\n" + items.get(position).main.temp);

        return textView;
    }
}
