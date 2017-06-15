package com.example.myfirstapp;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

/**
 * @author Sai Valluri
 * This class displays the information of contact pertaining BPA event info like flight number and hotel room
 */
public class FlightInfo extends Activity {
	private DatabaseTable db = new DatabaseTable(this);
	private String s;
	private static Integer id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);
        
        //Retrieval of the cursor from the ContactActivity class
        s = getIntent().getExtras().getString("id");
        id = Integer.parseInt(s);
        db.open();
        Cursor c = db.getRecord(id);
        displayFlight(c);
    }
    
    /**
     * @param cursor
     * This method displays the flight and hotel info of the contact
     */
    private void displayFlight(Cursor cursor) {
    	
    	TextView display_flight = (TextView)findViewById(R.id.textView5);
    	TextView display_hotel = (TextView)findViewById(R.id.textView7);
    	TextView display_arrival = (TextView)findViewById(R.id.textView6);
    	TextView display_depart = (TextView)findViewById(R.id.textView8);
    	
    	int flightIndex = cursor.getColumnIndex(DatabaseTable.COL_FLIGHT_NUMBER);
		int hotelIndex = cursor.getColumnIndex(DatabaseTable.COL_HOTEL_ROOM_NUMBER);
		int arrivalIndex = cursor.getColumnIndex(DatabaseTable.COL_ARRIVAL);
		int departIndex = cursor.getColumnIndex(DatabaseTable.COL_DEPARTURE);
		
		display_flight.setText(cursor.getString(flightIndex));
		display_hotel.setText(cursor.getString(hotelIndex));
		display_arrival.setText(cursor.getString(arrivalIndex));
		display_depart.setText(cursor.getString(departIndex));
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_flight_info, menu);
        return true;
    }
}
