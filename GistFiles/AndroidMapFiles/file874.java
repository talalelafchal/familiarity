package honjo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
import jp.co.mapion.android.maps.GeoPoint;
import jp.co.mapion.android.maps.MapView;
import jp.co.mapion.android.maps.Overlay;

public class ListenerOverlay extends Overlay implements
		GestureDetector.OnDoubleTapListener,
		GestureDetector.OnGestureListener {

	private GestureDetector gesture = new GestureDetector(this);
	private Context context;
	private MapView mapView;
	private Geocoder geocoder;

	public ListenerOverlay(Context context, MapView mapView) {
		this.context = context;
		this.mapView = mapView;
		geocoder = new Geocoder(context, Locale.JAPAN);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		gesture.onTouchEvent(e);
		return super.onTouchEvent(e, mapView);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		GeoPoint temp = mapView.getProjection()
			.fromPixels((int)e.getX(), (int)e.getY());
		double lat = temp.getLatitudeE6() / 1E6;
		double lon = temp.getLongitudeE6() / 1E6;
		try {
			List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
			Address address = addressList.get(0);
			StringBuilder sb = new StringBuilder();
			String buf;
			for (int i = 0; (buf = address.getAddressLine(i)) != null; i++) {
				sb.append("[line" + i + "]" + buf + "\n");
			}
			Toast.makeText(context, sb, Toast.LENGTH_LONG).show();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		GeoPoint temp = mapView.getProjection()
			.fromPixels((int)e.getX(), (int)e.getY());
		mapView.getController().animateTo(temp);
		return false;
	}
}
