package com.example.administrator.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;


/**
 * Created by Administrator on 2014/10/3.
 */
public class EX0427 extends Activity {
    private GridView gv;
    private SlidingDrawer sd;
    private ImageView im;
    private int[] icons = {
            R.drawable.ic_launcher_gallery,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher_maps,
            R.drawable.ic_launcher_camera
    };

    private String[] items = {
            "gallery", "null", "maps", "camera"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex0427);

        gv = (GridView) findViewById(R.id.myContent);
        sd = (SlidingDrawer) findViewById(R.id.mySlid);
        im = (ImageView) findViewById(R.id.myImage);

        /* 使用自定義的MyGridViewAdapter 設置GridView裡面的item內容 */
        MyGridViewAdapter adapter = new MyGridViewAdapter(this, items, icons);
        gv.setAdapter(adapter);

        /* 設置SlidingDrawer被打開的事件處理 */
        sd.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                im.setImageResource(R.drawable.abc_ic_go);
            }
        });

        /* 設置SlidingDrawer被關閉的事件處理 */
        sd.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                im.setImageResource(R.drawable.ic_launcher_camera);
            }
        });
    }
}

class MyGridViewAdapter extends BaseAdapter {
    private Context _con;
    private String[] _items;
    private int[] _icons;

    public MyGridViewAdapter(Context con, String[] items, int[] icons) {
        _con = con;
        _items = items;
        _icons = icons;
    }

    @Override
    public int getCount() {
        return _items.length;
    }

    @Override
    public Object getItem(int arg0) {
        return _items[arg0];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(_con);

        /* 使用ex0427_grid.xml, 作為每個item的Layout */
        View v = (View) factory.inflate(R.layout.ex0427_grid, null);
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        TextView tv = (TextView) v.findViewById(R.id.text);

        /* 設置顯示的Image與文字 */
        iv.setImageResource(_icons[position]);
        tv.setText(_items[position]);
        return v;
    }
}