package com.sgt_tibs.demo.listcolor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lt_tibs on 9/15/15.
 */
public class SimpleAdapter extends BaseAdapter {

    Context mContext;
    List<RowItem> mItems;

    public class Row{
        LinearLayout mLinearLayout;
        TextView mTextView;
    }

    public SimpleAdapter(Context context, List<RowItem> items){
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Row theRow;

        // Inflate the row or re-use it
        if(convertView == null){
            theRow = new Row();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_row, null, false);
            theRow.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.llBackground);
            theRow.mTextView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(theRow);
        }else{
            theRow = (Row) convertView.getTag();
        }

        RowItem item = mItems.get(position);

        // Set the text
        theRow.mTextView.setText(item.text);

        if(item.selected){
            theRow.mLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected));
        }else{
            theRow.mLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        return convertView;
    }
}
