import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ActCityMap extends MapActivity {

	Button btnCityWiki;
	MapView mv;
	MapController mc;
	String cityName;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);
		
		btnCityWiki = (Button)findViewById(R.id.btnCityWiki);
		mv = (MapView)findViewById(R.id.mapview);
		mc = mv.getController();
		
		Intent startupIntent = getIntent();
		cityName = startupIntent.getStringExtra("cityname");
		Double lat = startupIntent.getDoubleExtra("lat", 0.0);
		Double lng = startupIntent.getDoubleExtra("lng", 0.0);
		
		mc.setCenter(new GeoPoint((int)(lat*1E6), (int)(lng*1E6)));
		mc.setZoom(12);
		
		mv.setBuiltInZoomControls(true);
		mv.setClickable(true);
		
		btnCityWiki.setText("Open " + cityName + " Wiki");
		btnCityWiki.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Uri uri = Uri.parse( "http://en.m.wikipedia.org/wiki/" + cityName);
				startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}