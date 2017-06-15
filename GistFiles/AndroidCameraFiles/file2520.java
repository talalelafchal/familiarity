import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import java.util.*;

/**
 * Loads POI objects on map asynchronously.
 */
public final class POIMarkersManager {

    private final UserRepository userRepository;
    private final AppPreferences appPreferences;
    private Subscription subscription;

    private final Map<POI, Marker> poiMarkers = new HashMap<>();

    private GoogleMap googleMap;
    private MyInfoWindowAdapter infoWindowAdapter;
    private float lastZoom;

    public POIMarkersManager(final UserRepository userRepository, final AppPreferences appPreferences) {
        this.userRepository = userRepository;
        this.appPreferences = appPreferences;
    }

    /**
     * Prepares marker manager. Call this every time Google Map is ready.
     * @param googleMap Google map
     * @param infoWindowAdapter InfoWindow adapter when clicking on markers
     */
    public void prepare(@NonNull final GoogleMap googleMap,
                        @NonNull final MyInfoWindowAdapter infoWindowAdapter) {
        this.lastZoom = Settings.DEFAULT_COUNTRY_CAMERA_ZOOM;
        this.googleMap = googleMap;
        this.infoWindowAdapter = infoWindowAdapter;
    }

    /**
     * Releases resources. Call it onDestroy()
     */
    public void release() {
        RxUtils.possiblyUnsubscribe(subscription);
        googleMap = null;
        infoWindowAdapter = null;
    }

    /**
     * Triggers new async load to fetch marker objects. Could be remote call to server or local call database. 
     */
    public void reload() {
        if (!appPreferences.isPointsOfInterestVisibleOnMap()) {
            subscription = Observable.just(Collections.<POI>emptyList()).subscribe(createObserver());
            return;
        }

        final ListPointsOfInterests usecase = new ListPointsOfInterests(userRepository);
        subscription = usecase.execute(createObserver());
    }

    /**
     * Checks whether marker is within our scope of objects.
     * @param marker Givem marker
     * @return True if yes.
     */
    public boolean containsMarker(Marker marker) {
        return poiMarkers.containsValue(marker);
    }

    /**
     * Get object from marker.
     * @param marker Given Marker
     * @return POI object if found, null if not.
     */
    public @Nullable POI getFromMarker(Marker marker) {
        if (!containsMarker(marker)) {
            return null;
        }

        for (Map.Entry<POI, Marker> entry : poiMarkers.entrySet()) {
            if (entry.getValue().equals(marker)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Observer<List<POI>> createObserver() {
        return new Observer<List<POI>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final List<POI> list) {

                if (googleMap == null) {
                    return;
                }

                clearMarkers(list);

                if (!appPreferences.isPointsOfInterestVisibleOnMap()) {
                    return;
                }

                for (final POI poi : list) {
                    addPOIMarker(poi);
                }

                if (infoWindowAdapter != null) {
                    infoWindowAdapter.setPOIMarkers(poiMarkers);
                }
            }
        };
    }

    private void addPOIMarker(final POI poi) {

        // remove old ones
        if (poiMarkers.containsKey(poi)) {
            final Marker old = poiMarkers.remove(poi);
            old.remove();
            poiMarkers.remove(poi);
        }

        final LatLng latLng = new LatLng(poi.gps.lat, poi.gps.lng);

        MarkerOptions options = new MarkerOptions();
        options.title(poi.name)
                .snippet(poi.description)
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_heart_marker))
                .anchor(0.5f, 0.5f)
                .visible(isVisibleOnMap(lastZoom))
                .infoWindowAnchor(0.5f, 0.6f);
        final Marker marker = googleMap.addMarker(options);
        poiMarkers.put(poi, marker);
    }

    private void clearMarkers(List<POI> newList) {
        for (Iterator<Map.Entry<POI, Marker>> it = poiMarkers.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<POI, Marker> entry = it.next();
            if (!newList.contains(entry.getKey())) {
                entry.getValue().remove();
                it.remove();
            }
        }
    }

    /**
     * Call it when Google Map camera moves. Markers are not visible at given zoom level. We want to hide it for extra performance.
     */
    public void onNewCameraBounds() {
        if (googleMap == null) {
            return;
        }

        final float currentZoom = googleMap.getCameraPosition().zoom;

        if (currentZoom == this.lastZoom) {
            return;
        }

        this.lastZoom = currentZoom;

        for (Iterator<Map.Entry<POI, Marker>> it = poiMarkers.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<POI, Marker> entry = it.next();
            entry.getValue().setVisible(isVisibleOnMap(lastZoom));
        }
    }

    private static boolean isVisibleOnMap(float zoomLevel) {
        return zoomLevel >= 9.0f;
    }
}
