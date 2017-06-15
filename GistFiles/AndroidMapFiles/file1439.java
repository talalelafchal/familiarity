package honjo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
import jp.co.mapion.android.maps.GeoPoint;
import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;
import jp.co.mapion.android.maps.Overlay;

public class Slide04Activity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapView mapView = new MapView(this, "APIキー");
		mapView.setClickable(true);
		setContentView(mapView);
		
		mapView.setBuiltInZoomControls(true);
		
		mapView.getController().setCenter(new GeoPoint((int) (35.641625 * 1E6), (int) (139.749803 * 1E6)));
		mapView.getController().setZoom(mapView.getMaxZoomLevel() - 2);

		Overlay overlay = new Overlay() {
			@Override
			public void draw(Canvas canvas, MapView mapView, boolean shadow) {
				if (!shadow) {
					GeoPoint p = new GeoPoint((int) (35.641625 * 1E6), (int) (139.749803 * 1E6));
					
					Point pos = mapView.getProjection().toPixels(p, null);
					
					Paint paint = new Paint();
					paint.setColor(Color.argb(150, 255, 0, 255));
					paint.setAntiAlias(true);

					canvas.drawCircle(pos.x, pos.y, 30.0f, paint);
				}
			}
		};
		mapView.getOverlays().add(overlay);
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
