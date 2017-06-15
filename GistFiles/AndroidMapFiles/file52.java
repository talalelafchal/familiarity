
package honjo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
import jp.co.mapion.android.maps.GeoPoint;
import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;

public class Slide03Activity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout layout = new LinearLayout(this);
		setContentView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);

		final MapView mapView = new MapView(this, "APIキー");
		mapView.setClickable(true);
		
		mapView.setBuiltInZoomControls(true);
		
		mapView.getController().setCenter(new GeoPoint((int) (35.68 * 1E6), (int) (139.68 * 1E6)));
		mapView.getController().setZoom(mapView.getMaxZoomLevel() - 2);

		Button button = new Button(this);
		button.setText("クリック");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mapView.getController().setCenter(new GeoPoint((int) (35.7 * 1E6), (int) (139.7 * 1E6))); // ノンアニメーション
				mapView.getController().animateTo(new GeoPoint((int) (35.7 * 1E6), (int) (139.7 * 1E6))); // アニメーション
			}
		});

		layout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.addView(mapView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
