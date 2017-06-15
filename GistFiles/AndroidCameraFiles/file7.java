import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.doapps.etaxiclient.R;

/**
 * Created by William_ST on 10/02/16.
 */
public class MapRouteUtil {

    private GoogleMap mMap;
    private Polyline line;
    private InterfaceEndDraw interfaceEndDraw;

    public final String TAG = MapRouteUtil.class.getSimpleName();

    public MapRouteUtil(GoogleMap mMap){
        this.mMap = mMap;
    }

    public void traceRourte(LatLng origin, LatLng destine){
        if(origin != null && destine != null) {
            new GetDirectionsAsync().execute(origin, destine);
        }
    }

    class GetDirectionsAsync extends AsyncTask<LatLng, Void, List<LatLng>> {

        JSONParser jsonParser;
        String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?sensor=false&language=pt";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LatLng> doInBackground(LatLng... params) {
            LatLng start = params[0];
            LatLng end = params[1];

            HashMap<String, String> points = new HashMap<>();
            points.put("origin", start.latitude + "," + start.longitude);
            points.put("destination", end.latitude + "," + end.longitude);
            points.put("mode","driving");

            jsonParser = new JSONParser();

            JSONObject obj = jsonParser.makeHttpRequest(DIRECTIONS_URL, "GET", points, true);
            if (obj == null) return null;

            try {
                List<LatLng> list = null;

                JSONArray routeArray = obj.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                list = decodePolylines(encodedString);

                return list;

            } catch (JSONException e) {
                Log.e(TAG, "doInbackground: "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<LatLng> pointsList) {
            try {
                if (pointsList == null) return;
                if (line != null) {
                    line.remove();
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                PolylineOptions options = new PolylineOptions().width(5).color(R.color.route_line).geodesic(true);
                for (int i = 0; i < pointsList.size(); i++) {
                    LatLng point = pointsList.get(i);
                    builder.include(pointsList.get(i));
                    options.add(point);
                }
                LatLngBounds bounds = builder.build();
                if (options != null) {
                    line = mMap.addPolyline(options);
                    //verificar si el mapa se asigna cuando ya esta instanciado
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    mMap.animateCamera(cu, 200, null);
//                    LatLng auxLocation = getCenterPosition();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                            .target(new LatLng(auxLocation.latitude, auxLocation.longitude))
//                            .zoom(16)
//                            .build()));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interfaceEndDraw.ready(line);
                        }
                    }, 1000);
                }

            } catch (NullPointerException e){
                Log.e(TAG, "onPostExecute "+e.toString());
            }
        }
    }

    private List<LatLng> decodePolylines(final String encodedPoints) {
        List<LatLng> lstLatLng = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0, lng = 0;
        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }
        return lstLatLng;
    }

    public interface InterfaceEndDraw{
        void ready(Polyline polyline);
    }

    public void setInterfaceEndDraw(InterfaceEndDraw interfaceEndDraw){
        this.interfaceEndDraw = interfaceEndDraw;
    }

    public LatLng getCenterPosition() {
        VisibleRegion visibleRegion = mMap.getProjection()
                .getVisibleRegion();
        Point x = mMap.getProjection().toScreenLocation(
                visibleRegion.farRight);
        Point y = mMap.getProjection().toScreenLocation(
                visibleRegion.nearLeft);
        Point centerPoint = new Point(x.x / 2, y.y / 2);
        return mMap.getProjection().fromScreenLocation(centerPoint);
    }
}
