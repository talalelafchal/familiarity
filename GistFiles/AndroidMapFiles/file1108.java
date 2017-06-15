package honjo;

import android.os.Bundle;

//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;

public class Slide01Activity extends MapActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapView mapView = new MapView(this, "APIキー");
		mapView.setClickable(true); // true => スクロール可 false => スクロール不可
		setContentView(mapView);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}
}