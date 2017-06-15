package com.example.android;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ADME;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.example.android.R;
import com.example.android.common.BaseVanillaMapFragment;
import com.example.android.common.BaseVanillaCursorLoader;
import com.example.android.common.LocationAwareActivity;

/**
 * Map fragment for stores
 */
public class StoreLocatorMapFragment extends BaseVanillaMapFragment implements LocationListener, GoogleMap.OnCameraChangeListener, GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<StoreLocatorMapFragment.StoreItem>, ClusterManager.OnClusterClickListener<StoreLocatorMapFragment.StoreItem>, ClusterManager.OnClusterInfoWindowClickListener<StoreLocatorMapFragment.StoreItem>, ClusterManager.OnClusterItemInfoWindowClickListener<StoreLocatorMapFragment.StoreItem> {

    private static final String STATE_MAP_LATITUDE = "latitude";
    private static final String STATE_MAP_LONGITUDE = "longitude";
    private static final String STATE_MAP_ZOOM = "zoom";
    private static final float DEFAULT_ZOOM = 13;
    private static final int LOADER_STORES_IN_MAP = 71203987;
    public static final String ARG_BOUNDS = "bounds";
    public static final String ARG_USER_LOC = "userLocation";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ClusterManager<StoreItem> mClusterManager;
    private Location mLastKnowLocation;
    private double mSavedUserLatitude;
    private double mSavedUserLongitude;
    private float mSavedUserZoom;
    private boolean shouldRestoreMapState;
    private boolean mMapPositionInitialized;
    private boolean mMapListenersInitialized;
    private Cursor mData;
    private SparseArray<StoreItem> mShowingStores = new SparseArray<StoreItem>();
    private StoreItem mMarkerClickedItem;


    private LoaderManager.LoaderCallbacks<Cursor> CB_STORES = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            LatLngBounds bounds = args.getParcelable(ARG_BOUNDS);
            LatLng userLoc = null;
            if (args.containsKey(ARG_USER_LOC)) {
                userLoc = args.getParcelable(ARG_USER_LOC);
            }
            return new MapStoresLoader(getActivity(), bounds, userLoc);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            onStoresLoaded(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MAP_ZOOM)) {
            mSavedUserLatitude = savedInstanceState.getDouble(STATE_MAP_LATITUDE);
            mSavedUserLongitude = savedInstanceState.getDouble(STATE_MAP_LONGITUDE);
            mSavedUserZoom = savedInstanceState.getFloat(STATE_MAP_ZOOM);
            shouldRestoreMapState = true;
        } else {
            shouldRestoreMapState = false;
        }
        mMapPositionInitialized = false;
        mMapListenersInitialized = false;
        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Location lastLocation = ((LocationAwareActivity) getActivity()).getLastLocation();
        if (lastLocation != null) {
            onLocationChanged(lastLocation);
        } else if (mMap != null) {
            Bundle args = buildStoresLoaderArgs();
            getLoaderManager().initLoader(LOADER_STORES_IN_MAP, args, CB_STORES);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putDouble(STATE_MAP_LATITUDE, mMap.getCameraPosition().target.latitude);
            outState.putDouble(STATE_MAP_LONGITUDE, mMap.getCameraPosition().target.longitude);
            outState.putFloat(STATE_MAP_ZOOM, mMap.getCameraPosition().zoom);
        }
    }

    public boolean isMapAvailable() {
        return getMap() != null;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #initializeMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = getMap();
            TypedValue tv = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
            final int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            mMap.setPadding(0, actionBarHeight, 0, getResources().getDimensionPixelSize(R.dimen.card_panel_height));
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mClusterManager = new ClusterManager<StoreItem>(getActivity(), mMap);
                initializeMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void initializeMap() {
        if (!mMapPositionInitialized) {
            mMap.setMyLocationEnabled(true);
            if (shouldRestoreMapState) {
                shouldRestoreMapState = false;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mSavedUserLatitude, mSavedUserLongitude), mSavedUserZoom));
                mMapPositionInitialized = true;
            } else if (mLastKnowLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()), DEFAULT_ZOOM));
                mMapPositionInitialized = true;
            }
        } else {
            Bundle args = buildStoresLoaderArgs();
            getLoaderManager().initLoader(LOADER_STORES_IN_MAP, args, CB_STORES);
        }

        if (!mMapListenersInitialized) {
            mMapListenersInitialized = true;
            mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mMap.setOnCameraChangeListener(this);
            mClusterManager.setOnClusterItemClickListener(this);
            mClusterManager.setOnClusterClickListener(this);
            mClusterManager.setOnClusterInfoWindowClickListener(this);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(this);
            mClusterManager.setRenderer(new StoreClusterRenderer(getActivity(), getMap(), mClusterManager));
//            mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(this);
        }
    }

    private void syncDataToMap() {
        if (mMap != null && mData != null) {
            int distanceColumnIndex = mData.getColumnIndex(StoreProvider.ByDistance.COL_COMPUTED_DISTANCE_METERS);
            mData.moveToPosition(-1);
            while (mData.moveToNext()) {
                Store store = ADME.cursorToEntity(mData, new Store());
                StoreItem storeItem = mShowingStores.get(store.hashCode());
                boolean add;
                if (storeItem != null) {
                    storeItem.setStore(store);
                    add = false;
                } else {
                    storeItem = StoreItem.from(store);
                    add = true;
                }
                if (store.getLatitude() != null && store.getLongitude() != null) {
                    if (distanceColumnIndex >= 0) {
                        storeItem.setDistance(mData.getDouble(distanceColumnIndex));
                    }
                    if (add) {
                        mClusterManager.addItem(storeItem);
                        mShowingStores.put(store.hashCode(), storeItem);
                    }
                } else if (!add) {
                    mClusterManager.removeItem(storeItem);
                    mShowingStores.remove(store.hashCode());
                }
            }
            mClusterManager.cluster();
        }
    }

    private Bundle buildStoresLoaderArgs() {
        Bundle args = new Bundle();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        args.putParcelable(ARG_BOUNDS, bounds);
        if (mLastKnowLocation != null) {
            args.putParcelable(ARG_USER_LOC, new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()));
        }
        return args;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnowLocation = location;
        if (mMap != null && !mMapPositionInitialized) {
            initializeMap();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Bundle args = buildStoresLoaderArgs();
        getLoaderManager().restartLoader(LOADER_STORES_IN_MAP, args, CB_STORES);
    }

    private void onStoresLoaded(Cursor data) {
        if (mData != data && mData != null) {
            mData.close();
        }
        mData = data;
        syncDataToMap();
    }

    @Override
    public boolean onClusterClick(Cluster<StoreItem> storeItemCluster) {
        LatLng position = storeItemCluster.getPosition();
        CameraPosition newCamera = new CameraPosition.Builder().target(position).zoom(mMap.getCameraPosition().zoom + 1).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamera));
        return true;
    }

    @Override
    public boolean onClusterItemClick(StoreItem storeItem) {
        mMarkerClickedItem = storeItem;
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (mMarkerClickedItem != null) {
            StoreItem storeItem = mMarkerClickedItem;
            mMarkerClickedItem = null;
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.store_map_infowindow, null, false);

            if (storeItem.getDistance() != null) {
                ((TextView) view.findViewById(R.id.distance)).setText(Store.formatDistance(storeItem.getDistance()));
            } else {
                ((TextView) view.findViewById(R.id.distance)).setText(Store.FORMAT_NO_DISTANCE);
            }
            ((TextView) view.findViewById(R.id.store_name)).setText(storeItem.getStore().formatName());
            ((TextView) view.findViewById(R.id.store_address)).setText(storeItem.getStore().formatAddress());
            return view;
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    public void onClusterInfoWindowClick(Cluster<StoreItem> storeItemCluster) {
        Toast.makeText(getActivity(), "Clicked store cluster = " + storeItemCluster.getSize(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClusterItemInfoWindowClick(StoreItem storeItem) {
//        Toast.makeText(getActivity(), "Clicked store = " + storeItem.getStore().getName(), Toast.LENGTH_SHORT).show();
        String distanceString = Store.formatDistance(storeItem.getDistance());
        ((OnStoreListener) getActivity()).onItemClick(storeItem.getStore(), distanceString);
    }

    public static class StoreItem implements ClusterItem {
        private Store store;
        private Double distance;
        private LatLng position;

        public static StoreItem from(Store store) {
            return new StoreItem(store, null);
        }

        public StoreItem(Store store, Double distance) {
            this.store = store;
            this.distance = distance;
            this.position = new LatLng(store.getLatitude(), store.getLongitude());
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        public Store getStore() {
            return store;
        }

        public Double getDistance() {
            return distance;
        }

        public StoreItem setStore(Store store) {
            this.store = store;
            return this;
        }

        public StoreItem setDistance(Double distance) {
            this.distance = distance;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StoreItem storeItem = (StoreItem) o;

            if (distance != null ? !distance.equals(storeItem.distance) : storeItem.distance != null)
                return false;
            if (position != null ? !position.equals(storeItem.position) : storeItem.position != null)
                return false;
            if (store != null ? !store.equals(storeItem.store) : storeItem.store != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return store != null ? store.hashCode() : 0;
        }
    }

    public static class MapStoresLoader extends BaseVanillaCursorLoader {
        private LatLngBounds bounds;
        private LatLng userLoc;

        @Override
        protected Cursor loadCursorInBackground() {
            if (userLoc != null) {
                return StoreProvider.ByDistance.queryStoresByBounds(getContext(), bounds, userLoc);
            } else {
                return StoreProvider.ByDistance.queryStoresByBounds(getContext(), bounds);
            }
        }

        public MapStoresLoader(Context context, LatLngBounds projection, LatLng userLocation) {
            super(context);
            bounds = projection;
            userLoc = userLocation;
        }

        public LatLngBounds getBounds() {
            return bounds;
        }

        public void setBounds(LatLngBounds bounds) {
            this.bounds = bounds;
        }

        public LatLng getUserLoc() {
            return userLoc;
        }

        public void setUserLoc(LatLng userLoc) {
            this.userLoc = userLoc;
        }
    }

    private class StoreClusterRenderer extends DefaultClusterRenderer<StoreItem> {
        private final IconGenerator mIconGenerator;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray<BitmapDescriptor>();
        private ShapeDrawable mColoredCircleBackground;
        private final float mDensity;

        public StoreClusterRenderer(Context context, GoogleMap map, ClusterManager<StoreItem> clusterManager) {
            super(context, StoreLocatorMapFragment.this.getMap(), StoreLocatorMapFragment.this.mClusterManager);
            mDensity = context.getResources().getDisplayMetrics().density;
            mIconGenerator = new IconGenerator(context);
            mIconGenerator.setContentView(makeSquareTextView(context));
            mIconGenerator.setTextAppearance(R.style.ClusterIcon_TextAppearance_normal);
            mIconGenerator.setBackground(makeClusterBackground());
        }

        @Override
        protected void onBeforeClusterItemRendered(StoreItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.fromResource((R.drawable.kiko_pointer_1))); // .title(item.mTitle).snippet(item.mSubtitle)
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<StoreItem> cluster, MarkerOptions markerOptions) {
            int bucket = getBucket(cluster);
            BitmapDescriptor descriptor = mIcons.get(bucket);

            if (descriptor == null) {
                mColoredCircleBackground.getPaint().setColor(getResources().getColor(R.color.main_purple));
                descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)));
                mIcons.put(bucket, descriptor);
            }
            // TODO: consider adding anchor(.5, .5) (Individual markers will overlap more often)
            markerOptions.icon(descriptor);
        }

        private LayerDrawable makeClusterBackground() {
            mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
            outline.getPaint().setColor(0x80ffffff); // Transparent white.
            LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
            int strokeWidth = (int) (mDensity * 3);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            return background;
        }

        private SquareTextView makeSquareTextView(Context context) {
            SquareTextView squareTextView = new SquareTextView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(R.id.text);
            int twelveDpi = (int) (12 * mDensity);
            squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
            return squareTextView;
        }
    }
}
