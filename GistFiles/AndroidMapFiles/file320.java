package mono.samples.googlemaps;

import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


public class MyMapActivity extends com.google.android.maps.MapActivity {
    /** Called when the activity is first created. */

    MapView gmapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        double myLat = 45.869208;
        double myLong = 12.394029;
        gmapView = (MapView) findViewById(R.id.mapView);
        GeoPoint p = new GeoPoint((int) (myLat * 1000000), (int) (myLong * 1000000));
        
        List<Object> coords = MapReceiver.getCoordinates();
        
        GeoPoint pinball = new GeoPoint(
                    (int)(((Double)(coords.get(0))).doubleValue() * 1000000),
                    (int)(((Double)(coords.get(1))).doubleValue() * 1000000));

        MapOverlay mapOverlay = new MapOverlay();
        mapOverlay.setPoint(pinball);

        MapOverlay ov2 = new MapOverlay();
        ov2.setPoint(p);
        List<Overlay> listOfOverlays = gmapView.getOverlays();
        //listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);               
        listOfOverlays.add(ov2);               
        
        gmapView.getController().setCenter(p);
        gmapView.getController().setZoom(15);
        //gmapView.invalidate();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }


    class MapOverlay extends com.google.android.maps.Overlay
    {

        private GeoPoint p;

        public void setPoint(GeoPoint point){
            p = point;
        }

        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pinball);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
            return true;
        }
    } 

}



