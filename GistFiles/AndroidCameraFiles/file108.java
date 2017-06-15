package edu.liu.shapefileexample;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.bbn.openmap.dataAccess.shape.DbfFile;
import com.bbn.openmap.dataAccess.shape.DbfTableModel;
import com.bbn.openmap.dataAccess.shape.ShapeUtils;
import com.bbn.openmap.dataAccess.shape.input.DbfInputStream;
import com.bbn.openmap.layer.shape.ESRIPoly;
import com.bbn.openmap.layer.shape.ESRIPolygonRecord;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        final GoogleMap map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng centerLatLng = new LatLng(40.776902,-73.969284);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(centerLatLng, 3);
        map.moveCamera(cameraUpdate);

        try {
            String path = Environment.getExternalStorageDirectory().toString();
            //String fileName = "tl_2012_us_state.zip";
            String fileName = "TM_WORLD_BORDERS_SIMPL.zip";

            // extracted file name would be "(zip file name)/(extracted file name).ext"
            String destPath = Installer.unzip(getApplicationContext(),fileName,path,false);

            String targetFilePath = destPath + File.separator +  "TM_WORLD_BORDERS_SIMPL.shp";

            Log.v("myapp","path = "  + targetFilePath);
            ShapeFile shapeFile = new ShapeFile(targetFilePath);

            // DbfInputStream2 modification is for educational purpose only.
            // I took the source code of DbfInputStream and remove swing import.
            // note, you have to put the modification as public domain. read bbn license here
            // http://openmap.bbn.com/license.html
            FileInputStream fileInputStream = new FileInputStream(new File(destPath + File.separator +  "TM_WORLD_BORDERS_SIMPL.dbf"));
            DbfInputStream2 dbfInputStream = new DbfInputStream2(fileInputStream);

            Log.v("myapp","row 200, column 4 = "+ ((ArrayList)dbfInputStream.getRecords().get(200)).get(4));

            for (ESRIRecord esriRecord = shapeFile.getNextRecord(); esriRecord!=null;esriRecord = shapeFile.getNextRecord()){
                String shapeTypeStr = ShapeUtils.getStringForType(esriRecord.getShapeType());
                Log.v("myapp","shape type = " + esriRecord.getRecordNumber() + "-" + shapeTypeStr);

                // In QGIS, US is 208 but index starts from 0 so add 1
                if (esriRecord.getRecordNumber() != 209){
                    continue;
                }

                if (shapeTypeStr.equals("POLYGON")) {
                    // cast type after checking the type
                    ESRIPolygonRecord polyRec = (ESRIPolygonRecord)esriRecord;

                    Log.v("myapp","number of polygon objects = " + polyRec.polygons.length);
                    for (int i=0; i<polyRec.polygons.length; i++){
                        // read for a few layers
                        ESRIPoly.ESRIFloatPoly poly = (ESRIPoly.ESRIFloatPoly)polyRec.polygons[i];

                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.strokeColor(Color.argb(150,200,0,0));
                        polygonOptions.fillColor(Color.argb(150,0,0,150));
                        polygonOptions.strokeWidth(2.0f);

                        Log.v("myapp","Points in the polygon = " + poly.nPoints);

                        for (int j=0; j<poly.nPoints; j++){
                            //Log.v("myapp",poly.getY(j) + "," + poly.getX(j));
                            polygonOptions.add(new LatLng(poly.getY(j), poly.getX(j)));
                        }
                        map.addPolygon(polygonOptions);
                        Log.v("myapp","polygon added");
                    }

                }
                else {
                    Log.v("myapp","error polygon not found (type = " + esriRecord.getShapeType() + ")");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("myapp","error=" + e);
        }


    }
}
