package honjo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

public class Slide06Activity extends MapActivity {

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
					GeoPoint one = new GeoPoint(35641625, 139749803);
					GeoPoint two = new GeoPoint(35642625, 139747803);
					GeoPoint three = new GeoPoint(35642625, 139751803);
					
					Point onePoint = mapView.getProjection().toPixels(one, null);
					Point twoPoint = mapView.getProjection().toPixels(two, null);
					Point threePoint = mapView.getProjection().toPixels(three, null);

					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setColor(Color.argb(150, 255, 0, 255));
					paint.setStyle(Paint.Style.FILL_AND_STROKE);
					Path path = new Path();
					
					path.moveTo(onePoint.x, onePoint.y);
					path.lineTo(twoPoint.x, twoPoint.y);
					path.lineTo(threePoint.x, threePoint.y);
					path.lineTo(onePoint.x, onePoint.y);
					canvas.drawPath(path, paint);
				}
			}
		};
		mapView.getOverlays().add(overlay);
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
