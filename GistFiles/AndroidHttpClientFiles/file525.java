package com.shareobj.search;

import java.util.List;

import com.shareobj.search.R;
import com.shareobj.search.RowItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

    Context context;
    RowItem rowItem;
    
    public CustomListViewAdapter(Context context, int resourceId,
                                 List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageButton angry_btn;
        ImageView imageView;
        TextView txtPhone;
        TextView txtTitle;
        TextView txtDistance;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
       rowItem = getItem(position);
      
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.phones);
            holder.txtDistance = (TextView) convertView.findViewById(R.id.distance);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.angry_btn = (ImageButton) convertView.findViewById(R.id.angry_btn);

            convertView.setTag(holder);
        } 
            holder = (ViewHolder) convertView.getTag();
        
       
        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtPhone.setText(rowItem.getPhone());
        holder.txtDistance.setText(rowItem.getDistance());
        holder.imageView.setImageResource(R.drawable.ic_launcher);
        Picasso.with(this.context).load(rowItem.getUrl()).into(holder.imageView);

        holder.angry_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {            	
            	
				Toast.makeText(context, getItem(position).getPhone(),
						Toast.LENGTH_SHORT).show();

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" +  getItem(position).getPhone()));
				context.startActivity(callIntent);            	

            }
        });

        return convertView;
    }
}