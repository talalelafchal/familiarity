package com.jlebrech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;


public class MatchReportActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

	List<Ticket> tickets = TicketDAO.readAll();
	int size = tickets.size();

	setListAdapter(new ArrayAdapter<String>(this, R.layout.main.list, tickets));


	ListView lv = (ListView) findViewById(R.layout.main.list);
	lv.setTextFilterEnabled(true);

	lv.setOnItemClickListener(new OnItemClickListener() {
	  public void onItemClick(AdapterView<?> parent, View view,
	      int position, long id) {
	    // When clicked, show a toast with the TextView text
	    Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	        Toast.LENGTH_SHORT).show();
	  }
	});

  }


}