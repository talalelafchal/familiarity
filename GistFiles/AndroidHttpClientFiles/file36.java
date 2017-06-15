package com.matpompili.settle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WebCachedImageView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by matteo on 21/10/14.
 */
public class BuildingAdapter extends ArrayAdapter<BuildingObject> {
    private static class ViewHolderItem {
        WebCachedImageView image;
        TextView buildingName;
        TextView roomInfo;
    }


    public BuildingAdapter(Context context, ArrayList<BuildingObject> buildings) {
        super(context, 0, buildings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolderItem viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.building_card_layout, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.image = (WebCachedImageView) convertView.findViewById(R.id.imageBuilding);
            viewHolder.buildingName = (TextView) convertView.findViewById(R.id.textBuildingName);
            viewHolder.roomInfo = (TextView) convertView.findViewById(R.id.textRoomInfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        BuildingObject building = getItem(position);
        // Lookup view for data population
        //FetchableImageView image = (FetchableImageView) convertView.findViewById(R.id.imageBuilding);

        // Populate the data into the template view using the data object
        if (building != null){
            viewHolder.image.setImageUrl(building.imageURL);
            viewHolder.buildingName.setText(Utilities.capitalize(building.name));
            //roomInfo.setText(building.roomCount + " aule registrate");
            viewHolder.roomInfo.setText("visualizza aule");
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
