package honjo;

import android.os.Bundle;

//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;

public class Slide09Activity extends MapActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        MapView mapView = new MapView(this, "APIキー");
        mapView.setClickable(true);
		setContentView(mapView);

		mapView.getOverlays().add(new ListenerOverlay(this, mapView));
    }
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
