package honjo;

import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;
import android.os.Bundle;

public class Slide08Activity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapView mapView = new MapView(this, "APIキー");
		setContentView(mapView);

		mapView.setRotation(true);
		mapView.setDegrees(45.0f);
	}
}
