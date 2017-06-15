package de.vogella.android.listview3d;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterAnimation extends ArrayAdapter<String> {
    private Context context;
    private LayoutInflater mInflater;
    private String[] strings;

    public AdapterAnimation(Activity context, String[] strings) {
        super(context, 0, strings);
        this.context = context;
        //this.mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater = context.getLayoutInflater();
        this.strings = strings;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String str = this.strings[position];
        final TextView textview;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem, null);
        }
        textview = (TextView)convertView.findViewById(R.id.textView_number2);
        textview.setText(str);

        Animation animation = null;
        animation = AnimationUtils.loadAnimation(context, R.anim.mycombo);
        convertView.startAnimation(animation);

        return convertView;
    }
}