import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ActCityList extends ListActivity {
	ListView lv;
	ArrayList<City> cityList;
	ArrayAdapter<City> aa;
	EditText etCity;
	Button btnSubmit;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		lv = getListView();
		etCity = (EditText)findViewById(R.id.etCity);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		
		cityList = new ArrayList<City>();

		cityList.add(new City(0L, "Singapore", 1.283333, 103.833333));
		cityList.add(new City(1L, "Bangkok", 13.752222, 100.493889));
		cityList.add(new City(2L, "Tokyo", 35.700556, 139.715));
		cityList.add(new City(3L, "Jakarta", -6.2, 106.8));

		aa = new ArrayAdapter<City>(getApplicationContext(),
				android.R.layout.simple_list_item_1, cityList);
		lv.setAdapter(aa);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String tempCity = etCity.getText().toString();
				long id = cityList.size();
				cityList.add(new City(id, tempCity, 0.0, 0.0));
				aa.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent startMapIntent = new Intent(this, ActCityMap.class);
		
		startMapIntent.putExtra("cityname", cityList.get(position).getName());
		
		startActivity(startMapIntent);
		super.onListItemClick(l, v, position, id);
	}
}