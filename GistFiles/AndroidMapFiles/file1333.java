package batchadd.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends Activity {

    private GoogleMap map;
    private ArrayList<LatLng> m_overlay = new ArrayList<LatLng>();
    private boolean isInitialised;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInitialised) {
            addBatchLocations(MainActivity.list);
        }
    }

    public void addBatchLocations(List<LatLng> batchLocations) {
        new AddMarkersTask(batchLocations).execute();
    }

    private class AddMarkersTask extends AsyncTask<Void, Object, Void> {
        private List<LatLng> mapLocations;
        //        private final List<LatLng> itemList = new ArrayList<LatLng>();
        private final List<MarkerOptions> mOptionsList = new ArrayList<MarkerOptions>();

        public AddMarkersTask(List<LatLng> mapLocations) {
            this.mapLocations = mapLocations;
        }

        protected Void doInBackground(Void... params) {
            final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            // calculate required bounds
            for (LatLng mapLocation : mapLocations) {
                bounds.include(mapLocation);
            }
            // move to centre of the bounds on main thread
            runOnUiThread(new Runnable() {
                public void run() {
                    invokeCameraMovement(bounds);
                }
            });
            for (LatLng item : mapLocations) {
                MarkerOptions markerOptions = createMarkerOptions(item);
                mOptionsList.add(markerOptions);
            }
            publishProgress();

            // task returns Void so whatever
            return null;
        }

        protected void onProgressUpdate(Object... params) {
            for (int i = 0; i < mapLocations.size(); i++) {
                LatLng item = mapLocations.get(i);
                MarkerOptions markerOptions = mOptionsList.get(i);
                map.addMarker(markerOptions);
                m_overlay.add(item);
            }
            animateToRegion();
        }

        protected void onPostExecute(Void result) {
            isInitialised = true;
            this.mapLocations = null;
        }
    }

    private static MarkerOptions createMarkerOptions(LatLng item) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(item);
        return marker;
    }

    private void invokeCameraMovement(final LatLngBounds.Builder bounds) {
        final MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        try {
            LatLngBounds latLngBounds = bounds.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
        } catch (IllegalStateException e) {
            // Layout has not yet been initialised, delay the movement to
            // onGlobalLayout
            final View mapView = mapfragment.getView();
            ViewTreeObserver viewTreeObserver = mapView.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    // Listener removal method changed since Jelly Bean,
                    // check current version and use proper one.
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        invokeCameraMovement(bounds);
                    }
                });
            }
        }
    }

    private void animateToRegion() {
        final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (LatLng item : m_overlay) {
            bounds.include(item);
        }
        invokeCameraMovement(bounds);

    }
}
