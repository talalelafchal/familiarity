package id.web.twoh.twohmaps;

import java.util.ArrayList;

import id.web.twoh.twohmaps.R;
import id.web.twoh.twohmaps.model.DBLokasi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapsActivity extends FragmentActivity{

	private GoogleMap map;
	private DBLokasi lokasi;
	private ArrayList<DBLokasi> values;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_map);
		 
		SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFrag.getMap();
		map.setMyLocationEnabled(true);
		
		Bundle b = this.getIntent().getExtras();
		
		if(b.containsKey("longitude")){
			final LatLng latLng = new LatLng(b.getDouble("latitude"), b.getDouble("longitude"));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
			map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			map.setOnMarkerClickListener(new OnMarkerClickListener() {			
				@Override
				public boolean onMarkerClick(Marker marker) {
					Toast.makeText(MapsActivity.this, "Lokasi saat ini "+latLng.latitude+","+latLng.longitude,Toast.LENGTH_SHORT).show();
					return false;
				}
			});
		}else if(this.getIntent().getSerializableExtra("lokasi")!=null)
		{
			lokasi = (DBLokasi) this.getIntent().getSerializableExtra("lokasi");
			if(lokasi!=null)
			{
				LatLng latLng = new LatLng(lokasi.getLatD(), lokasi.getLngD());
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
				map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title(lokasi.getNama()));
			}
			
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker marker) {					
					final Dialog dialog = new Dialog(MapsActivity.this);
					dialog.setTitle("Checkin Data :");				 
					dialog.setContentView(R.layout.fragment_dialog_datashow);
					TextView tvNama = (TextView) dialog.findViewById(R.id.tv_nama);
					TextView tvKoordinat = (TextView) dialog.findViewById(R.id.tv_koordinat);
					Button btOK = (Button) dialog.findViewById(R.id.bt_checkin_ok);
					
					tvNama.setText(String.format(getResources().getString(R.string.checkin_label_nama), marker.getTitle()));					
					tvKoordinat.setText(marker.getPosition().latitude+","+marker.getPosition().longitude);
					btOK.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.cancel();
							
						}
					});
					dialog.show();
					
					return false;
				}
			});
		}else
		{	
			LatLng init;
			DBLokasi lokInit;
			LatLng latLng;
			values = ((ArrayList<DBLokasi>) this.getIntent().getSerializableExtra("arraylokasi"));
			lokInit = values.get(0);
			init = new LatLng(lokInit.getLatD(), lokInit.getLngD());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(init, 16));
			for(DBLokasi lok : values)
			{
				latLng = new LatLng(lok.getLatD(), lok.getLngD());
				map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			}
			
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker marker) {
					final Dialog dialog = new Dialog(MapsActivity.this);
					dialog.setTitle("Checkin Data :");				 
					dialog.setContentView(R.layout.fragment_dialog_datashow);
					TextView tvNama = (TextView) dialog.findViewById(R.id.tv_nama);
					TextView tvKoordinat = (TextView) dialog.findViewById(R.id.tv_koordinat);
					Button btOK = (Button) dialog.findViewById(R.id.bt_checkin_ok);
					
					tvNama.setText(String.format(getResources().getString(R.string.checkin_label_nama), marker.getTitle()));					
					tvKoordinat.setText(marker.getPosition().latitude+","+marker.getPosition().longitude);
					btOK.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.cancel();
							
						}
					});
					dialog.show();
					return false;
				}
			});
		}
		
	}

}