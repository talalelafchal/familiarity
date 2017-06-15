package cardexc.com.practicework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends ArrayAdapter<Place> {

    private final Context context;
    private final List<Place> places;

    public MyListAdapter(Context context, ArrayList<Place> places) {

        super(context, R.layout.list_item, places);
        this.context = context;
        this.places = places;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView item_list_firstText  = (TextView) rowView.findViewById(R.id.item_list_firstText);
        TextView item_list_secondText = (TextView) rowView.findViewById(R.id.item_list_secondText);
        ImageView item_list_image = (ImageView) rowView.findViewById(R.id.item_list_image);

        item_list_firstText.setText(places.get(position).getPlace());
        item_list_secondText.setText(places.get(position).getDateTime());
        item_list_image.setImageBitmap(places.get(position).getImage());
        return rowView;

    }
}