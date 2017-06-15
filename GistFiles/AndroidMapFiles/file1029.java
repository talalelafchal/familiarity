import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class ActCityMap extends MapActivity {

	TextView tvCityName;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);
		
		tvCityName = (TextView)findViewById(R.id.tvCityName);
		
		Intent startupIntent = getIntent();
		String cityName = startupIntent.getStringExtra("cityname");
		
		tvCityName.setText(cityName);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}