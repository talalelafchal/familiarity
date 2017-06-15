package com.example.administrator.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/10/13.
 */
public class BaseAdapterDemo extends Activity {
    private ListView lv;
    public List<Map<String, Object>> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lv = (ListView) findViewById(R.id.lv);

        /* 獲取將要綁定的數據, 存入data中 */
        data = getData();
        MyAdapter adapter = new MyAdapter(this);
        lv.setAdapter(adapter);

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (int i = 0 ; i < 10 ; i++) {
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.ic_launcher);
            map.put("title", "肯德基");
            map.put("info", "有這麼好吃嗎???");
            list.add(map);
        }

        return list;
    }

    /* ViewHolder 靜態類 */
    static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new BaseAdapterDemo.ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
            } else {
                holder = (BaseAdapterDemo.ViewHolder) convertView.getTag();
            }

            holder.img.setBackgroundResource((Integer) data.get(position).get("img"));
            holder.title.setText((String) data.get(position).get("title"));
            holder.info.setText((String) data.get(position).get("info"));

            return convertView;
        }
    }
}