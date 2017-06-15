package com.example.administrator.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2014/9/26.
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    CharSequence[] list = null;

    public MyAdapter(Context context, CharSequence[] list) {
        myInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return  list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //自定類別, 表達個別listItem中的view物件集合
        ViewTag viewTag;

        if (convertView == null) {
            //取得listItem容器 view
            convertView = myInflater.inflate(R.layout.adapter, null);

            //建構listItem內容view
            viewTag = new ViewTag(
                    (ImageView) convertView.findViewById(R.id.myAdapterImageView),
                    (TextView) convertView.findViewById(R.id.myTextView),
                    (CheckBox) convertView.findViewById(R.id.myCheckBox));

            //設置容器內容
            convertView.setTag(viewTag);
        } else {
            viewTag = (ViewTag) convertView.getTag();
        }

        switch (position) {
            case MyListView.MyListView_camera:
                viewTag.icon.setBackgroundResource(R.drawable.ic_launcher_camera);
                break;
            case MyListView.MyListView_album:
                viewTag.icon.setBackgroundResource(R.drawable.ic_launcher_gallery);
                break;
            case MyListView.MyListView_map:
                viewTag.icon.setBackgroundResource(R.drawable.ic_launcher_maps);
                break;
        }

        viewTag.title.setText(list[position]);
        return convertView;
    }

    /*自定類別, 表達個別listItem中的view物件集合*/
    class ViewTag {
        ImageView icon;
        TextView title;
        CheckBox cbx;

        public ViewTag(ImageView icon, TextView title, CheckBox cbx) {
            this.icon = icon;
            this.title = title;
            this.cbx = cbx;
        }
    }

}
