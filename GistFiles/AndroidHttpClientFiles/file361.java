package com.matpompili.settle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by matteo on 21/10/14.
 */
public class RoomAdapter extends ArrayAdapter<RoomObject> {
    public BuildingObject building;
    public RoomAdapter(Context context, BuildingObject building) {
        super(context, 0, building.rooms);
        this.building = building;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RoomObject room = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_card_layout, parent, false);
        }
        // Lookup view for data population
        TextView roomName = (TextView) convertView.findViewById(R.id.textRoomName);
        TextView buildingName = (TextView) convertView.findViewById(R.id.textBuildingName);
        TextView availabilityText = (TextView) convertView.findViewById(R.id.textAvailability);
        TextView quietnessText = (TextView) convertView.findViewById(R.id.textQuietness);
        TextView lectureText = (TextView) convertView.findViewById(R.id.textLecture);
        TextView lastUpdateText = (TextView) convertView.findViewById(R.id.textLastUpdate);
        // Populate the data into the template view using the data object
        roomName.setText(Utilities.capitalize(room.name));
        buildingName.setText(Utilities.capitalize(building.name));
        if(room.isUpdateAvailable) {
            availabilityText.setText("Disponibilità: " + String.format("%.1f", room.availability) + " su 5");
            quietnessText.setText("Silenzio: " + String.format("%.1f",room.quietness) + " su 5");
            lectureText.setText("Lezione: " + ((room.isLectureGoing) ? "Sì" : "No"));
            lastUpdateText.setText("Aggiornato " + room.getLastUpdate());
        } else {
            availabilityText.setText("Non ho informazioni su questa aula. Fai doppio click per inviare un aggiornamento.");
            quietnessText.setText("");
            lectureText.setText("");
            lastUpdateText.setText("");
        }

        // Return the completed view to render on screen
        return convertView;
    }
}