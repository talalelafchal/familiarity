package com.chengyu.paginglistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chengyu on 2016/02/05.
 */
class PagingListAdapter extends BaseAdapter {

    private ArrayList<Map<String, String>> data = null;
    private Context context = null;
    private LayoutInflater inflater;

    private class ViewHolder {
        TextView tv1;
        TextView tv2;
    }

    public PagingListAdapter(Context context, ArrayList<Map<String, String>> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return (data == null) ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.tv1 = (TextView) view.findViewById(R.id.list_item_tv1);
            holder.tv2 = (TextView) view.findViewById(R.id.list_item_tv2);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv1.setText(data.get(position).get("msg1"));
        holder.tv2.setText(data.get(position).get("msg2"));

        return view;
    }

    /**
     * refresh data
     * @param newData
     */
    public void refreshData(ArrayList<Map<String, String>> newData) {
        this.data = newData;
        this.notifyDataSetChanged();
    }
}
