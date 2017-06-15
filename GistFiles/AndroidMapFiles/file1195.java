package david.com.showlocationaddpoints;

import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class MainActivity extends AppCompatActivity {

    //GraphicsLayer to hold poitns
    GraphicsLayer mGraphicsLayer;

    //Variable to holdl the Map
    MapView mMapView;

    //Tag for logging
    private static final String TAG = "MyTag";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get a hook to MapView
        mMapView = (MapView) findViewById(R.id.map);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if(status == STATUS.INITIALIZED){
                    Log.d(TAG, "Hey we loaded!");
                    //Call method to add graphics
                    addGraphics();
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

        // create a point in lat/long (decimal degrees) and project it to that of the MapView
        Point myNewPoint = GeometryEngine.project(23.63733, 37.94721, mMapView.getSpatialReference());

        //Just checking to make sure X is updated to meters and spatial reference is set
        Log.d(TAG, "x is: " + myNewPoint.getX());
        Log.d(TAG, "SR IS: " + mMapView.getSpatialReference());

        // create a graphic with the geometry and marker symbol
        Graphic pointGraphic = new Graphic(myNewPoint, simpleMarker);

        // add the graphic to the graphics layer
        mGraphicsLayer.addGraphic(pointGraphic);
    }
}