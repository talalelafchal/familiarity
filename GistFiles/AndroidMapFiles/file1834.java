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

public class Slide05Activity extends MapActivity {

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
					GeoPoint one = new GeoPoint((int) (35.641625 * 1E6), (int) (139.749803 * 1E6));
					GeoPoint two = new GeoPoint((int) (35.642625 * 1E6), (int) (139.747803 * 1E6));
					GeoPoint three = new GeoPoint((int) (35.642625 * 1E6), (int) (139.750803 * 1E6));

					Point onePoint = mapView.getProjection().toPixels(one, null);
					Point twoPoint = mapView.getProjection().toPixels(two, null);
					Point threePoint = mapView.getProjection().toPixels(three, null);
					
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					
					paint.setStrokeWidth(4);
					paint.setStyle(Paint.Style.STROKE);
					paint.setColor(Color.argb(150, 255, 0, 255));
					canvas.drawLines(new float[]{onePoint.x, onePoint.y, twoPoint.x, twoPoint.y, twoPoint.x, twoPoint.y, threePoint.x, threePoint.y}, paint);
				}
			}
		};
		mapView.getOverlays().add(overlay);
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
