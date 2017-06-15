/*** MaiFragment **/

/**
 * Created by ThanhCS94 on 1/6/17.
 * I'm HIDING. Don't waste your time to find me.
 */

public class SimpleMapsActivity extends Fragment implements OnMapReadyCallback {
    public static String TAG = "MAPS";
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private Button infoButtonReport;
    private Button infoButtonPins;
    private OnInfoWindowElemTouchListener infoButtonListenerReport;
    private OnInfoWindowElemTouchListener infoButtonListenerPin;
    MapWrapperLayout mapWrapperLayout;
    String locationText;
    public static int ZOOM_LEVEL = 13;
    private GPS_Tracker gps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapWrapperLayout = (MapWrapperLayout)view.findViewById(R.id.map_relative_layout);
        //Request permision
        requestPermissionLocation();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map_fragment, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        }
        return super.onOptionsItemSelected(item); // important line
    }


    private void requestPermissionLocation() {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constraint.PERMISSION_LOCATION);
            } else {
                initilizeMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initilizeMap() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.map, fragment);
        transaction.commit();
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mapWrapperLayout.init(map, getPixelsFromDp(getActivity(), 39 + 20));
        this.infoWindow = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.infor_windown_map, null);
        mapWrapperLayout.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
        this.infoWindow.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        infoButtonPins = (Button)infoWindow.findViewById(R.id.btn_pint);
        infoButtonReport = (Button)infoWindow.findViewById(R.id.btn_report);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        infoButtonListenerPin = new OnInfoWindowElemTouchListener(infoButtonPins,
                getResources().getDrawable(R.drawable.ic_pint_marker), //btn_default_normal_holo_light
                getResources().getDrawable(R.drawable.ic_pint_marker)) //btn_default_pressed_holo_light
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(getActivity(), marker.getTitle() + " | PINS", Toast.LENGTH_SHORT).show();
            }
        };

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        infoButtonListenerReport = new OnInfoWindowElemTouchListener(infoButtonReport,
                getResources().getDrawable(R.drawable.ic_nav_report), //btn_default_normal_holo_light
                getResources().getDrawable(R.drawable.ic_nav_report)) //btn_default_pressed_holo_light
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(getActivity(), marker.getTitle() + " | REPORT", Toast.LENGTH_SHORT).show();
            }
        };

        infoButtonPins.setOnTouchListener(infoButtonListenerPin);
        infoButtonReport.setOnTouchListener(infoButtonListenerReport);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoButtonListenerPin.setMarker(marker);
                infoButtonListenerReport.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                Geocoder gc = new Geocoder(getActivity());
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    Address add = list.get(0);
                    String address = add.getAddressLine(0);
                    String state = add.getAdminArea();
                    String country = add.getCountryName();
                    locationText = address + ", " + state+", "+ country;
                } catch (IOException e) {
                    e.printStackTrace();
                    SLog.logWtf(TAG ,"can't load location");
                }
                locationText =locationText.replace("null,", "");
                map.addMarker(new MarkerOptions()
                        .title(locationText)
                        .snippet(locationText)
                        .position(latLng)).showInfoWindow();
            }
        });
        getGPS(map);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        if (requestCode == Constraint.PERMISSION_LOCATION) {

            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "LOCATION services permission granted");
                    initilizeMap();
                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ");
                    showDialogOK(getString(R.string.loading),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            requestPermissionLocation();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            break;
                                    }
                                }
                            });
                }
            }
        }
    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager myFragmentManager = getChildFragmentManager();
        SupportMapFragment f
                = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

    private void getGPS(GoogleMap map) {
        gps = new GPS_Tracker(getActivity());
        // check if GPS enabled
        if(gps.canGetLocation()){
            Double mLat = gps.getLatitude();
            Double mLon = gps.getLongitude();
            // \n is for new line
            Log.wtf("LOCATION", "Your Location is - \nLat: " + mLat + "\nLong: " + mLon);
            //convetLagLonToAddress(mLat , mLon);
            LatLng latLng = new LatLng(mLat, mLon);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL);
            map.moveCamera(cameraUpdate);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}


/***Inforwindown.xml **/
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center_vertical" >

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_marginRight="10dp" >

        <com.scity.view.TextViewBold
            android:id="@+id/title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="18sp"
            android:layout_gravity="center_vertical"
            android:minWidth="100dp"
            android:paddingLeft="@dimen/space_4dp"
            android:maxWidth="200dp"
            android:text="Title" />

    </LinearLayout>

    <View
        android:layout_width="1dp"
        android:layout_marginLeft="@dimen/space_4dp"
        android:layout_height="match_parent"
        android:background="@color/background_ccc"/>

    <Button
        android:id="@+id/btn_report"
        android:layout_width="45dp"
        android:layout_marginLeft="@dimen/space_4dp"
        android:layout_height="wrap_content"
        android:background="@drawable/ic_nav_report" />

    <View
        android:layout_width="1dp"
        android:layout_marginLeft="@dimen/space_4dp"
        android:layout_height="match_parent"
        android:background="@color/background_ccc"/>

    <Button
        android:id="@+id/btn_pint"
        android:layout_width="45dp"
        android:layout_height="wrap_content"
        android:layout_marginLeft="@dimen/space_4dp"
        android:background="@drawable/ic_pint_marker" />


</LinearLayout>

/**XML**/

<?xml version="1.0" encoding="utf-8"?>
<com.scity.view.MapWrapperLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
android:id="@+id/map_relative_layout"
android:layout_width="match_parent"
android:layout_height="match_parent">

<fragment
    android:id="@+id/map"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    class="com.google.android.gms.maps.SupportMapFragment"/>
</com.scity.view.MapWrapperLayout>

/*** MapWrapperLayout**/
package com.scity.view;

/**
 * Created by ThanhCS94 on 1/5/17.
 * I'm HIDING. Don't waste your time to find me.
 */

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MapWrapperLayout extends RelativeLayout {
    /**
     * Reference to a GoogleMap object
     */
    private GoogleMap map;

    /**
     * Vertical offset in pixels between the bottom edge of our InfoWindow
     * and the marker position (by default it's bottom edge too).
     * It's a good idea to use custom markers and also the InfoWindow frame,
     * because we probably can't rely on the sizes of the default marker and frame.
     */
    private int bottomOffsetPixels;

    /**
     * A currently selected marker
     */
    private Marker marker;

    /**
     * Our custom view which is returned from either the InfoWindowAdapter.getInfoContents
     * or InfoWindowAdapter.getInfoWindow
     */
    private View infoWindow;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Must be called before we can route the touch events
     */
    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    /**
     * Best to be called from either the InfoWindowAdapter.getInfoContents
     * or InfoWindowAdapter.getInfoWindow.
     */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = false;
        // Make sure that the infoWindow is shown and we have all the needed references
        if (marker != null && marker.isInfoWindowShown() && map != null && infoWindow != null) {
            // Get a marker position on the screen
            Point point = map.getProjection().toScreenLocation(marker.getPosition());

            // Make a copy of the MotionEvent and adjust it's location
            // so it is relative to the infoWindow left top corner
            MotionEvent copyEv = MotionEvent.obtain(ev);
            copyEv.offsetLocation(
                    -point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetPixels);

            // Dispatch the adjusted MotionEvent to the infoWindow
            ret = infoWindow.dispatchTouchEvent(copyEv);
        }
        // If the infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return ret || super.dispatchTouchEvent(ev);
    }
}

/**  click **/
package com.scity.listener;

/**
 * Created by ThanhCS94 on 1/5/17.
 * I'm HIDING. Don't waste your time to find me.
 */


import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.model.Marker;

public abstract class OnInfoWindowElemTouchListener implements OnTouchListener {
    private final View view;
    private final Drawable bgDrawableNormal;
    private final Drawable bgDrawablePressed;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public OnInfoWindowElemTouchListener(View view, Drawable bgDrawableNormal, Drawable bgDrawablePressed) {
        this.view = view;
        this.bgDrawableNormal = bgDrawableNormal;
        this.bgDrawablePressed = bgDrawablePressed;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        if (0 <= event.getX() && event.getX() <= view.getWidth() && 0 <= event.getY() && event.getY() <= view.getHeight()) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startPress();
                    break;

                // We need to delay releasing of the view a little so it shows the
                // pressed state on the screen
                case MotionEvent.ACTION_UP:
                    handler.postDelayed(confirmClickRunnable, 150);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endPress();
                    break;
                default:
                    break;
            }
        } else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackgroundDrawable(bgDrawablePressed);
            if (marker != null)
                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackgroundDrawable(bgDrawableNormal);
            if (marker != null)
                marker.showInfoWindow();
            return true;
        } else
            return false;
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
