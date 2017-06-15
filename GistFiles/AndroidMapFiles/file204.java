import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import java.util.ArrayList;


/**
 * Created by nikola on 9/9/14.
 */
public abstract class MapViewActivity extends ActionBarActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private static final String TAG = MapViewActivity.class.getSimpleName();
    private MapView mMapView;
    private Bundle mSavedInstanceState;
    private boolean isMapInitialized = false;
    private GoogleMap mMap;
    private boolean scheduleOnResumeFailure = false;
    private View mContentView;
    private boolean scheduleReplacementOnGlobalLayout = false;
    private GoogleMapOptions mGoogleMapsOptions;

    public abstract void onMapReadyToSetup();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        mContentView = findViewById(android.R.id.content);
        if(mContentView.getViewTreeObserver().isAlive()){
            mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }else{
            setupMapView(mSavedInstanceState);
            setUpMapIfNeeded();
        }

    }

    public boolean isMapInitialized() {
        return isMapInitialized;
    }

    private void setupMapView(Bundle savedInstanceState){
        if(mMapView == null) {
            View contentView = findViewById(android.R.id.content);
            mMapView = (MapView) findMapView(contentView);
        }
        if(mMapView != null){
            mMapView.onCreate(savedInstanceState);
            if(scheduleOnResumeFailure){
                scheduleOnResumeFailure = false;
                mMapView.onResume();
            }
            if(scheduleReplacementOnGlobalLayout){
                scheduleReplacementOnGlobalLayout = false;
                replaceMapViewWithGoogleMapOptions(mGoogleMapsOptions);
            }
            setUpMapIfNeeded();
        }
    }
    public void replaceMapViewWithGoogleMapOptions(GoogleMapOptions options){
        if(mMapView != null) {
            ViewGroup mapViewParent = (ViewGroup) mMapView.getParent();
            ViewGroup.LayoutParams mapViewLayoutParams = mMapView.getLayoutParams();
            mapViewParent.removeView(mMapView);
            mMapView = new MapView(this, options);
            mapViewParent.addView(mMapView, mapViewLayoutParams);
            forwardMapViewLifeCycleEvents();
            setUpMapIfNeeded();
        }else{
            scheduleReplacementOnGlobalLayout = true;
            mGoogleMapsOptions = options;
        }
    }

    private void forwardMapViewLifeCycleEvents() {
        mMapView.onCreate(mSavedInstanceState);
        mMapView.onResume();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            MapsInitializer.initialize(this);
            if(mMapView != null) {
                mMap = mMapView.getMap();
                if (mMap != null) {
                    Log.i(TAG, "setUpMapIfNeeeded() onMapReadyToSetup()");
                    onMapReadyToSetup();
                }
            }
        }
    }

   public MapView getMapView(){
       return mMapView;
   }
   public GoogleMap getGoogleMap(){
       return mMap;
   }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(mMapView != null) {
            mMapView.onResume();
        }else{
            scheduleOnResumeFailure = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedInstanceState = outState;
        if(mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    private View findMapView(View contentView) {
            ArrayList<View> unvisited = new ArrayList<View>();
            unvisited.add(contentView);
            while (!unvisited.isEmpty()) {
                View child = unvisited.remove(0);
                if(child instanceof MapView) {
                    Log.i(TAG, "MapView instance found! ");
                    unvisited.clear();
                    return child;
                }
                if (!(child instanceof ViewGroup)){
                    continue;
                }
                ViewGroup group = (ViewGroup) child;
                final int childCount = group.getChildCount();
                for (int i=0; i< childCount; i++){
                    unvisited.add(group.getChildAt(i));
                }
            }
        throw new IllegalArgumentException("MapView instance was not found in your layout.");
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onGlobalLayout() {
        Log.i(TAG, "onGlobalLayout()");
        removeGlobalLayoutListener(mContentView, this);
        isMapInitialized = true;
        setupMapView(mSavedInstanceState);
        setUpMapIfNeeded();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private static void removeGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
