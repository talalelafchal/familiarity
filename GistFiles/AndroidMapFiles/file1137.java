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
public class AdapterDemo extends Activity{
    private ImageView img;
    private List<Map<String, Object>> mData;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mData = getmData();
        MyAdapter adapter = new MyAdapter(this);
        listView = (ListView) findViewById(R.id.myListView);
        listView.setAdapter(adapter);

    }

    static class ViewHolder {
        public TextView title;
        public TextView time;
        public TextView info;
        public ImageView img;
    }

    /* 自定義適配器 */
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
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
            AdapterDemo.ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);      //自定義布局
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
            holder.title.setText(mData.get(position).get("title").toString());
            holder.time.setText(mData.get(position).get("time").toString());
            holder.info.setText(mData.get(position).get("info").toString());

            return convertView;
        }
    }

    /* 初始化一個List */
    private List<Map<String, Object>> getmData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> map;
        for (int i = 0 ; i < 20 ; i++) {
            map = new HashMap<String, Object>();
            map.put("title", "人物" + i);
            map.put("time", "9月20日");
            map.put("info", "我通過了你的好友驗證請求");
            map.put("img", R.drawable.ic_launcher);
            list.add(map);
        }

        return list;
    }
}
