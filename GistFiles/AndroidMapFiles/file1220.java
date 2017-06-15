package ozawareechs.nanosensairapp;

/** tab2_map
 * Created by Jacob Miller on 5/18/2017.
**/

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class tab2_map extends Fragment implements OnMapReadyCallback{ 

//Create method for app
@Override public ViewonCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
       //inflater.inflate says what file is associated with the java code
        return inflater.inflate(R.layout.tab2_map, container, false);
    }

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }
  
@Override
public void onMapReady(GoogleMap googleMap) {
    LatLng marker =new LatLng(-33.867, 151.206);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));     
    googleMap.addMarker(new MarkerOptions().title("Hello GOOGLE MAPS").position(marker));
    }
}