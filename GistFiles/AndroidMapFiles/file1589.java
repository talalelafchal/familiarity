import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ActCityMap extends MapActivity {

	TextView tvCityName;
	MapView mv;
	MapController mc;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);
		
		tvCityName = (TextView)findViewById(R.id.tvCityName);
		mv = (MapView)findViewById(R.id.mapview);
		mc = mv.getController();
		
		Intent startupIntent = getIntent();
		String cityName = startupIntent.getStringExtra("cityname");
		Double lat = startupIntent.getDoubleExtra("lat", 0.0);
		Double lng = startupIntent.getDoubleExtra("lng", 0.0);
		
		mc.setCenter(new GeoPoint((int)(lat*1E6), (int)(lng*1E6)));
		mc.setZoom(12);
		
		mv.setBuiltInZoomControls(true);
		mv.setClickable(true);
		
		tvCityName.setText(cityName);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}