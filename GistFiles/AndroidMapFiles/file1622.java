package david.com.showlocationaddpoints;

import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class MainActivity extends AppCompatActivity {

    //GraphicsLayer to hold poitns
    GraphicsLayer mGraphicsLayer;

    //Geometry Engine for projection purposes
    GeometryEngine mGeometryEngine;

    //MapView
    MapView mMapView;

    //Getting users current Location
    LocationDisplayManager mLocation;

    //Tag for logging
    private static final String TAG = "MyTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeometryEngine = new GeometryEngine();

        //Get a hook to MapView
        mMapView = (MapView) findViewById(R.id.map);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if(status == STATUS.INITIALIZED){
                    Log.d(TAG, "Hey we loaded!");
                    //Call method to add graphics
                    addGraphics();

                    //Call method to get user location
                    setupLocationListner();




                }
            }
        });


    }
    public void addGraphics(){
        //Create and add a GraphicsLayer (this will hold our points)
        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);

        // create a point marker symbol (red, size 10, of type circle)
        SimpleMarkerSymbol simpleMarker = new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE);

        // create a point at x=-302557, y=7570663 (for a map using meters as units; this depends
        Point myNewPoint = GeometryEngine.project(23.63733, 37.94721, mMapView.getSpatialReference());

        Log.d(TAG, "x is: " + myNewPoint.getX());
        Log.d(TAG, "SR IS: " + mMapView.getSpatialReference());

        // create a graphic with the geometry and marker symbol
        Graphic pointGraphic = new Graphic(myNewPoint, simpleMarker);

        // add the graphic to the graphics layer
        mGraphicsLayer.addGraphic(pointGraphic);
    }

    public void setupLocationListner(){
        if((mMapView != null) && mMapView.isLoaded()){
            mLocation = mMapView.getLocationDisplayManager();
            mLocation.setLocationListener(new LocationListener() {

                boolean locationChanged = false;

                //Zooms to current location when first GPS fix arrives
                @Override
                public void onLocationChanged(Location location) {
                    if(!locationChanged){
                        locationChanged = true;
                        zoomToLocation(location);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }


            });
            mLocation.start();
        }
    }
    /**
     * Zoom to location using a specific size of extent.
     *
     * @param loc  the location to center the MapView at
     */
    private void zoomToLocation(Location loc){
        Point mapPoint = getAsPoint(loc);
        Unit mapUnit = mMapView.getSpatialReference().getUnit();
        double zoomFactor = Unit.convertUnits(20, Unit.create(LinearUnit.Code.MILE_US), mapUnit);
        Envelope zoomExtent = new Envelope(mapPoint, zoomFactor, zoomFactor);
        mMapView.setExtent(zoomExtent);
    }

    private Point getAsPoint(Location loc) {
        Point wgsPoint = new Point(loc.getLongitude(), loc.getLatitude());
        return (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326),
                mMapView.getSpatialReference());
    }
}