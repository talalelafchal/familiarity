import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity
{
	static final LatLng GPS = new LatLng( 25.03880310058, 121.56798553466797 );
	private GoogleMap mMap;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		MapFragment mMapFragment = MapFragment.newInstance();
		android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add( R.id.map, mMapFragment );
		fragmentTransaction.commit();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		Marker marker = mMap.addMarker( new MarkerOptions().position( GPS ) );
		mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( GPS, 16 ) );
	}
}
