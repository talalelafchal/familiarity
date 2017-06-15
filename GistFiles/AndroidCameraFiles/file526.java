package com.example.peter.blocspot.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ActionMenuView;

import com.example.peter.blocspot.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    private GoogleMap mMap;
    private Menu mMenu;
    private Toolbar mToolbar;
    View view;

    static int targetHeight = 0;

    private boolean mNotifyIsOn;

    private MenuItem add, search, settings, menu, notify;
    //used for determining which menu item is active
    private String activeMenu = "";

    private final String ADD_TITLE = "add";
    private final String SEARCH_TITLE = "search";
    private final String SETTINGS_TITLE = "settings";
    private final String MENU_TITLE = "menu";

    private boolean toggleMenuPressed(String itemPressed){
        boolean openWindow = true;

        //set all of the icons to dark
        add.setIcon(R.drawable.add_dark);
        search.setIcon(R.drawable.search_dark);
        settings.setIcon(R.drawable.settings_dark);
        menu.setIcon(R.drawable.menu_dark);

        //if the currently active menu is pressed, hide it
        if(activeMenu.equals(itemPressed)){
            activeMenu = "";
            openWindow = false;
        }else{
            //otherwise, set the new item pressed and turn on the icon
            activeMenu = itemPressed;
            //TODO - Swap appropriate fragmemnt
            switch (itemPressed){
                case ADD_TITLE:
                    add.setIcon(R.drawable.add_light);
                    break;
                case SEARCH_TITLE:
                    search.setIcon(R.drawable.search_light);
                    break;
                case SETTINGS_TITLE:
                    settings.setIcon(R.drawable.settings_light);
                    break;
                case MENU_TITLE:
                    menu.setIcon(R.drawable.menu_light);
                    break;
            }
        }

        return openWindow;
    }

    private void toggleNotify(){
        mNotifyIsOn = !mNotifyIsOn;
        notify.setIcon(mNotifyIsOn ? R.drawable.notify_light : R.drawable.notify_dark);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mToolbar = (Toolbar) findViewById(R.id.tb_maps_activity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupEvenlyDistributedToolbar();
         //Obtain the SupportMapFragment and get notified when the map is ready to be used.

        view =  findViewById(R.id.popupWindow);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(targetHeight == 0){
                        targetHeight = view.getHeight();
                        view.setVisibility(View.GONE);
                    }
                }
            });
        }
        if (!canAccessLocation() || !canAccessContacts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, 1);
            }
        }else{
            initMaps();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        initMaps();
    }

    private void initMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCamera() {
        return(hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessContacts() {
        return(hasPermission(Manifest.permission.READ_CONTACTS));
    }


    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_bar_menu, menu);
        this.mMenu = menu;

        this.menu = mMenu.getItem(0);
        this.add = mMenu.getItem(1);
        this.notify = mMenu.getItem(2);
        this.search = mMenu.getItem(3);
        this.settings = mMenu.getItem(4);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // returns true if the user is wanted to open a window
        if(item == this.notify){
            toggleNotify();
        }else{
            if(toggleMenuPressed(item.getTitle().toString())){
                expand(view);
            }else{
                collapse(view);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static void expand(final View v) {

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int height = (int)(targetHeight * interpolatedTime);
                v.getLayoutParams().height = height;
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        float density =  v.getContext().getResources().getDisplayMetrics().density;
        a.setDuration(350);
        a.setInterpolator(new LinearInterpolator());
        v.startAnimation(a);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void setupEvenlyDistributedToolbar(){
        // Use Display metrics to get Screen Dimensions
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.tb_maps_activity);
        // Inflate your menu
        mToolbar.inflateMenu(R.menu.bottom_bar_menu);

        // Add 10 spacing on either side of the toolbar
        mToolbar.setContentInsetsAbsolute(10, 10);

        // Get the ChildCount of your Toolbar, this should only be 1
        int childCount = mToolbar.getChildCount();
        // Get the Screen Width in pixels
        int screenWidth = metrics.widthPixels;

        // Create the Toolbar Params based on the screenWidth
        Toolbar.LayoutParams toolbarParams = new Toolbar.LayoutParams(screenWidth, ActionMenuView.LayoutParams.WRAP_CONTENT);

        // Loop through the child Items
        for(int i = 0; i < childCount; i++){
            // Get the item at the current index
            View childView = mToolbar.getChildAt(i);
            // If its a ViewGroup
            if(childView instanceof ViewGroup){
                // Set its layout params
                childView.setLayoutParams(toolbarParams);
                // Get the child count of this view group, and compute the item widths based on this count & screen size
                int innerChildCount = ((ViewGroup) childView).getChildCount();
                int itemWidth  = (screenWidth / innerChildCount);
                // Create layout params for the ActionMenuView
                ActionMenuView.LayoutParams params = new ActionMenuView.LayoutParams(itemWidth, ActionMenuView.LayoutParams.WRAP_CONTENT);
                // Loop through the children
                for(int j = 0; j < innerChildCount; j++){
                    View grandChild = ((ViewGroup) childView).getChildAt(j);
                    if(grandChild instanceof ActionMenuItemView){
                        // set the layout parameters on each View
                        grandChild.setLayoutParams(params);
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            System.out.println("GOT TO ATTEMPTING CAMERA SHIFT");
            Location userLocation = locationManager.getLastKnownLocation(provider);
            double lat = userLocation.getLatitude();
            double lng = userLocation.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        } catch (SecurityException e) {
            System.err.println("MapsActivity.onMapReady() -- caught SecurityException");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }
}