package com.xxx;
 
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
 
public class CustomAdapter extends BaseAdapter {
 
    private Activity mActivity;
    private ArrayList<String> mData; 
 
    public CustomAdapter(Activity a, ArrayList<String> d) {
        mActivity = a;
        mData = d;
    }
 
    public int getCount() {
        return mData.size();
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
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            
            holder.text = (TextView) convertView.findViewById(R.id.title);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        holder.img.setImageBitmap(xxxx);
        holder.text.setText(xxx);
        return convertView;
    }
 
    class ViewHolder {
        ImageView img;
        TextView text;
    }
}