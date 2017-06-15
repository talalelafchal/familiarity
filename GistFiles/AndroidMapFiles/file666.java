package jp.co.mapion.honjo.mapionmapstest3;

import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;
import android.os.Bundle;

public class Slide10Activity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapView mapView = new MapView(this, "develop");
		mapView.setClickable(true);
		mapView.changeMap(MapView.STANDARD); // オフィシャルと同じ画像
		// mapView.changeMap(MapView.LIGHT); // 軽い画像
		setContentView(mapView);
	}
}
