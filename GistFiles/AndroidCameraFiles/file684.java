package com.example.tasneem.googleplacesapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tasneem on 10/08/15.
 */
public class LstAdap extends BaseAdapter{
    Context context;
    ArrayList<String> arrayList;

    public LstAdap(Context context,ArrayList<String> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        if(convertView==null){

            LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.lstitm,null);
            holder=new Holder();
            holder.txt=(TextView)convertView.findViewById(R.id.txt);
            convertView.setTag(holder);
        }
        else {
            holder=(Holder)convertView.getTag();
        }
        holder.txt.setText(arrayList.get(position));
        return convertView;
    }
    class Holder{
        TextView txt;
    }
}