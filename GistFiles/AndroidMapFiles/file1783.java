package com.gmail.fedorenko.kostia.app1lesson4;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kfedoren on 17.09.2015.
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private final ArrayList<Item> values;

    public ItemAdapter(Context context, ArrayList<Item> values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_list, parent, false);

        TextView place = (TextView) rowView.findViewById(R.id.place);
        TextView dateTime = (TextView) rowView.findViewById(R.id.date_time);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

        String placeStr = values.get(position).getPlace();
        String dateStr = values.get(position).getDate();
        String timeStr = values.get(position).getTime();
        Bitmap image = values.get(position).getImage();

        place.setText(placeStr);
        dateTime.setText("On: " + dateStr + "; at: " + timeStr);
        icon.setImageBitmap(image);
        return rowView;
    }
}
