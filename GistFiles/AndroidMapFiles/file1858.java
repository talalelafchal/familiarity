package honjo;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapView;
//import com.google.android.maps.OverlayItem;
import jp.co.mapion.android.maps.GeoPoint;
import jp.co.mapion.android.maps.ItemizedOverlay;
import jp.co.mapion.android.maps.MapActivity;
import jp.co.mapion.android.maps.MapView;
import jp.co.mapion.android.maps.OverlayItem;

public class Slide07Activity extends MapActivity {

	static class SampleItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		private Context context;
		private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		
		public SampleItemizedOverlay(Context context, Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			this.context = context;
			OverlayItem item0 = new OverlayItem(new GeoPoint((int) (35.641625 * 1E6), (int) (139.749803 * 1E6)), "title0", "snipet0");
			OverlayItem item1 = new OverlayItem(new GeoPoint((int) (35.642325 * 1E6), (int) (139.747803 * 1E6)), "title1", "snipet1");
			overlayItems.add(item0);
			overlayItems.add(item1);
			populate();
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			return overlayItems.get(arg0);
		}

		@Override
		public int size() {
			return overlayItems.size();
		}
		
		@Override
		protected boolean onTap(final int index) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			OverlayItem item = createItem(index);
			dialog.setTitle(item.getTitle());
			dialog.setNegativeButton("close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			dialog.show();
			
			return true;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapView mapView = new MapView(this, "APIキー");
		mapView.setClickable(true);
		setContentView(mapView);
		mapView.setBuiltInZoomControls(true);
		
		mapView.getController().setCenter(new GeoPoint((int) (35.641625 * 1E6), (int) (139.749803 * 1E6)));
		mapView.getController().setZoom(mapView.getMaxZoomLevel() - 2);

		Drawable marker = getResources().getDrawable(R.drawable.lightblue);
		SampleItemizedOverlay overlay = new SampleItemizedOverlay(this, marker);

		mapView.getOverlays().add(overlay);
	}
	
	protected boolean isRouteDisplayed() {
		return false;
	}
}
