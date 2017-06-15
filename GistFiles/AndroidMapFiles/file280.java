package com.example.administrator.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;

import java.text.AttributedCharacterIterator;

public class MainActivity extends AppCompatActivity {

    public String mFeatureServiceURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Louisville/LOJIC_PublicSafety_Louisville/MapServer/0";
    GraphicsLayer mGraphicsLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // String DYNAMIC_USA_HIHWAY_URL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Louisville/LOJIC_PublicSafety_Louisville/MapServer/0";

//        String mFeatureServiceURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Population_World/MapServer";
//        ArcGISDynamicMapServiceLayer StreetsLayer = new ArcGISDynamicMapServiceLayer(mFeatureServiceURL);
//        mMapView.addLayer(StreetsLayer);

        MapView mMapView = (MapView) findViewById(R.id.map);

        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);

        String mFeatureServiceURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Louisville/LOJIC_PublicSafety_Louisville/MapServer/0";
        ArcGISFeatureLayer mFeatureLayer = new ArcGISFeatureLayer(mFeatureServiceURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mMapView.addLayer(mFeatureLayer);


        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float v, float v1) {

                //when touch screen -> execute ^ ^ Query
                new QueryFeatureLayer().execute("11");

            }
        });

        mMapView.setOnLongPressListener(new OnLongPressListener() {
            @Override
            public boolean onLongPress(float v, float v1) {
                Toast toast = Toast.makeText(MainActivity.this, "LongPress", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        });


    }


    private class QueryFeatureLayer extends AsyncTask<String, Void, FeatureResult> {
        @Override
        protected FeatureResult doInBackground(String... params) {
            QueryParameters mParams = new QueryParameters();
            mParams.setWhere("SIADDRESS like '%" + params[0] + "%'");
            mParams.setReturnGeometry(true);
            mParams.setOutSpatialReference(SpatialReference.create(102100));

            FeatureResult results;

            QueryTask queryTask = new QueryTask(mFeatureServiceURL);

            try {
                results = queryTask.execute(mParams);
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FeatureResult results) {
            for (Object element: results){
                if(element instanceof Feature){
                    Feature feature = (Feature) element;

                    // create Symbol
                    SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.GREEN, 30, SimpleMarkerSymbol.STYLE.DIAMOND);

                    // add graphic to layer
                    Graphic graphic = new Graphic(feature.getGeometry(), symbol, feature.getAttributes());
                    mGraphicsLayer.addGraphic(graphic);

                    // set view point for user
                    MapView mMapView = (MapView) findViewById(R.id.map);
                    mMapView.setExtent(feature.getGeometry(), 500);
                }
            }
        }

    }



}
