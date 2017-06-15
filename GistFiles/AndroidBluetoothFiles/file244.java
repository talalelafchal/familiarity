package com.healthiot;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Test;

import java.util.ArrayList;

/**
 * Created by Brain on 22/01/2016.
 */
public class ChatAdapter extends BaseAdapter {

   ArrayList<String> dates,msgs,status;
    Context context;
    int count=0;
    int height=0,width=0;

    public ChatAdapter(Context c,ArrayList<String> temp,ArrayList<String> temp1,ArrayList<String> temp2,int cc,int h,int w)
    {
        dates = temp;
        msgs = temp1;
        status = temp2;
        context = c;
        count = cc;
        height = h;
        width = w;
    }

    @Override
    public int getCount() {


        return count;
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



        String type = status.get(position);


        if(type.equals("sent")) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_item_sent, parent, false);

            LinearLayout l = (LinearLayout) convertView.findViewById(R.id.iddd);

            l.getLayoutParams().width = (int) (0.70*width);
            l.setGravity(Gravity.RIGHT);

            TextView lbl = (TextView) convertView.findViewById(R.id.lbl1);

            lbl.setText(dates.get(position));

            lbl = (TextView) convertView.findViewById(R.id.lbl2);

            lbl.setText(msgs.get(position));

            lbl = (TextView) convertView.findViewById(R.id.lbl3);

            lbl.setText(status.get(position));


        }
        else if(type.equals("receive"))
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_item_rcv, parent, false);

            LinearLayout l = (LinearLayout) convertView.findViewById(R.id.iddd);

            l.getLayoutParams().width = (int) (0.70*width);

            l.setGravity(Gravity.LEFT);

            TextView lbl = (TextView) convertView.findViewById(R.id.lbl1);

            lbl.setText(dates.get(position));

            lbl = (TextView) convertView.findViewById(R.id.lbl2);

            lbl.setText(msgs.get(position));

            lbl = (TextView) convertView.findViewById(R.id.lbl3);

            lbl.setText(status.get(position));
        }



        return convertView;
    }
}
