import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {
	private IMapController mapController;
	private GeoPoint startingPoint = new GeoPoint(51.5, .0);
	private MapTileProviderBasic tileProvider;
	private MapView mv;
	private MyLocationOverlay myLocationOverlay;
	
	private final String mapURL = "http://a.tiles.mapbox.com/v3/czana.map-e6nd3na3/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tileProvider = new MapTileProviderBasic(this);
		
		// Defines source, indicating tag, resource id (if any), min zoom level, max zoom level,
		// tile size in pixels, image format, and map url.
		ITileSource tileSource = new XYTileSource("Test", null, 3, 10, 256, ".png", mapURL); 
		tileProvider.setTileSource(tileSource);
		
		// Initializes the view
		mv = new MapView(this, 256, new DefaultResourceProxyImpl(this), tileProvider, null);
		
		// Sets the MapView as the current View
		setContentView(mv);
		
		// Sets initial position of the map camera
		mapController = mv.getController();
		mapController.setCenter(startingPoint);
		mapController.setZoom(9);
		
		// Activates pan & zoom controls
		mv.setBuiltInZoomControls(true);
		mv.setMultiTouchControls(true);
		
		// Adds an icon that shows location
		myLocationOverlay = new MyLocationOverlay(this, mv);
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.setDrawAccuracyEnabled(true);
		
		// Configures a marker
		OverlayItem myLocationOverlayItem = new OverlayItem("Hello", "Marker test", new GeoPoint(3,3));
	        Drawable markerDrawable = this.getResources().getDrawable(R.drawable.pin);
	        myLocationOverlayItem.setMarker(markerDrawable);
	        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	        items.add(myLocationOverlayItem);
	        
	        // Sets marker actions
	        ItemizedIconOverlay<OverlayItem> markerOverlay = new ItemizedIconOverlay<OverlayItem>(items,
	                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
	                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
	                        return true;
	                    }
	                    public boolean onItemLongPress(final int index, final OverlayItem item) {
	                        return true;
	                    }
	                }, new DefaultResourceProxyImpl(this));
	        
	        // Configures a line
	        PathOverlay po = new PathOverlay(Color.RED, this);
	        Paint linePaint = new Paint();
	        linePaint.setStyle(Paint.Style.STROKE);
	        linePaint.setColor(Color.BLUE);
	        linePaint.setStrokeWidth(5);
	        po.setPaint(linePaint);
	        po.addPoint(startingPoint);
	        po.addPoint(new GeoPoint(51.7, 0.3));
	        po.addPoint(new GeoPoint(51.2, 0));
	        
	        
	        
	        // Adds line and marker to the overlay
	        mv.getOverlays().add(markerOverlay);
	        mv.getOverlays().add(po);
	        mv.getOverlays().add(myLocationOverlay);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}
	

}
