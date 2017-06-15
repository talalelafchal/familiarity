package com.example.android;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ADME;
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
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.example.android.sync.ArrivaContentProvider;
import com.example.android.utils.ArrivaDateUtils;

import java.util.Date;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link NewsListMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NewsListMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ARG_URI = "data_uri";
    private static final String TAG = NewsListMapFragment.class.getSimpleName();
    public static final String PARAM_POI = "poi";

    // TODO: Rename and change types of parameters
    String mUri;

    private ClusterManager<MyItem> mClusterManager;

    class MyItem implements ClusterItem {
        public LatLng mCoordinates;
        public String mUrl;
        public String mTitle;
        public String mSubtitle;
        public String mDescription;
        public Date mDate;
        public BitmapDescriptor mIcon;
        public int mPosition;

        public MyItem(LatLng coordinates,String url, String title, String subtitle, String description, Date date, BitmapDescriptor icon, int position){
            mCoordinates = coordinates;
            mUrl = url;
            mTitle = title;
            mSubtitle = subtitle;
            mDescription = description;
            mDate = date;
            mIcon = icon;
            mPosition = position;
        }

        @Override
        public LatLng getPosition() {
            return mCoordinates;
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uri Parameter 1.
     * @return A new instance of fragment NewsListMapFragment.
     */
    public static NewsListMapFragment newInstance(String uri) {
        NewsListMapFragment fragment = new NewsListMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsListMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUri = getArguments().getString(ARG_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mClusterManager = new ClusterManager<MyItem>(getActivity(), getMap());
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setRenderer(new DefaultClusterRenderer<MyItem>(getActivity(),getMap(),mClusterManager){

            @Override
            protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
                super.onBeforeClusterItemRendered(item, markerOptions);
                markerOptions.title(item.mTitle).snippet(item.mSubtitle).icon(item.mIcon);

            }

            @Override
            protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
                super.onClusterItemRendered(clusterItem, marker);
            }
        });
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                startActivity(NewsDetailActivity.newIntent(
                        getActivity(),
                        myItem.mUrl,
                        ArrivaDateUtils.dateToString(myItem.mDate),
                        myItem.mTitle,
                        myItem.mSubtitle,
                        myItem.mDescription));
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().restartLoader(0, null, this);
    }

    private void drawPinToMap(Cursor cursor) {
        if (mClusterManager != null) {
            mClusterManager.clearItems();

            if (cursor != null) {

                Bundle args = getArguments();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_mappa);

                final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                boolean boundsReady = false;
                cursor.moveToPosition(-1); // maybe already used ;)
                while (cursor.moveToNext()) {
                    Item itemFeed = ADME.cursorToEntity(cursor, new Item());
                    ItemLink itemLink = ADME.cursorToEntity(cursor, new ItemLink());
                    ItemLocation itemLocation = ADME.cursorToEntity(cursor, new ItemLocation());

                    if (itemLocation.getLatitude() == 0 && itemLocation.getLongitude() == 0) continue; // skip element without lat/long => lat/long == 0

                    final LatLng coordinate = new LatLng(itemLocation.getLatitude(), itemLocation.getLongitude());
                    bounds.include(coordinate);
                    boundsReady = true;
                    MyItem item = new MyItem(coordinate,itemLink.getUrl(),itemFeed.getTitle(),itemFeed.getSubtitle(),itemFeed.getDescription(),itemFeed.getCreatedOn(),icon, cursor.getPosition());
                    mClusterManager.addItem(item);
                }

//              Workaround: http://stackoverflow.com/a/13800112
                if (boundsReady) {
                    getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            // Move camera.
                            getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
                            // Remove listener to prevent position reset on camera move.
                            getMap().setOnCameraChangeListener(mClusterManager);
                        }
                    });
                }
                mClusterManager.cluster();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ArrivaContentProvider.getRssUri(mUri), new String[]{ Item.TABLE_NAME + ".*, " + ItemLocation.TABLE_NAME + ".*, " + ItemLink.TABLE_NAME + ".*"}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        drawPinToMap(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        drawPinToMap(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_map, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.show_list:
                replaceFragment(NewsListFragment.newInstance(mUri));
                return true;
            case R.id.show_map:
                replaceFragment(NewsListMapFragment.newInstance(mUri));
                return true;
            case R.id.show_camera:
                replaceFragment(NewsListCameraFragment.newInstance(mUri));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.container);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, currentFragment.getTag());
        ft.commit();
    }
}
