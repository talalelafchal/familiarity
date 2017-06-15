package com.tukangandroid.tutorial;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DoubleTabView extends MapActivity {

	private ListView lv;
	private TabHost tabs;
	private MapView mapView;
	private List<Overlay> mapOverlays;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tabs=(TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec("list");
        spec.setContent(R.id.list);
        spec.setIndicator("List View");
        tabs.addTab(spec);
        spec=tabs.newTabSpec("mapview");
        spec.setContent(R.id.mapview);
        spec.setIndicator("Map View");
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();

        lv = (ListView)findViewById(R.id.list);
        // Adding dummy data
        final List<Restaurant> restaurants = new ArrayList<Restaurant>();
        restaurants.add(new Restaurant("Indonesian Restaurant", "babi guling", 35100000, 129100000));
        restaurants.add(new Restaurant("Chinese Restaurant", "cap cay", 35110000, 129110000));
        restaurants.add(new Restaurant("Korean Restaurant", "kimchi jige", 35120000, 129120000));
        
        lv.setAdapter(new RestaurantAdapter(this,android.R.layout.simple_list_item_1, restaurants));
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// When user click a row, we get the row coordinate and navigate to that location
	        	navigateToLocation(restaurants.get(position), mapView, false);
	        	tabs.setCurrentTab(1);
			}
		});
    }
	
	public void navigateToLocation(Restaurant restaurant, MapView mv, boolean isDefault) {
        mapOverlays.clear();

        Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
        HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(drawable);
        
    	GeoPoint point = new GeoPoint(restaurant.getLatitude(), restaurant.getLongitude());
        OverlayItem overlayitem = new OverlayItem(point, "", "");
        itemizedOverlay.addOverlay(overlayitem);
		
        mapOverlays.add(itemizedOverlay);
        mv.displayZoomControls(true); 
		MapController mc = mv.getController();
		mc.animateTo(point); // move map to the given point
		int zoomlevel = mv.getMaxZoomLevel() - 1;
		mc.setZoom(zoomlevel); // zoom
		mv.setSatellite(false); // display only "normal" mapview
	}
	
	// Our array adapter, in our view, we will create a title, a description and an icon for each row
    private class RestaurantAdapter extends ArrayAdapter<Restaurant> {

        private List<Restaurant> items;

        public RestaurantAdapter(Context context, int textViewResourceId, List<Restaurant> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        // Create a title and detail, icon is created in the xml file
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }
                Restaurant o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                        if (tt != null) {
                            tt.setText("Name : "+o.getName());          
                        }
                        if(bt != null){
                            bt.setText("Special menu: "+ o.getSpecialMenu());
                        }
                }
                return v;
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}