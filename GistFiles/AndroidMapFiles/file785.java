package masxdeveloper.peternakan.Adapter;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import masxdeveloper.peternakan.ModelData.GoogleDirection.DistanceAndDuration;
import masxdeveloper.peternakan.ModelData.GoogleDirection.LatLngBoundData;

/**
 * Created by JonesRandom  on 02/03/2017.
 * https://masx-dev.blogspot.co.id
 */

public class GoogleDirectionResponse {

    public static LatLngBoundData getBoundsData(String Json) {

        double NorthLat = 0;
        double NorthLng = 0;

        double SouthLat = 0;
        double SouthLng = 0;

        try {

            JSONObject DataJson = new JSONObject(Json);
            JSONArray Routes = DataJson.getJSONArray("routes");

            JSONObject RoutesObj = Routes.optJSONObject(0);
            JSONObject Bounds = RoutesObj.getJSONObject("bounds");

            JSONObject NorthData = Bounds.getJSONObject("northeast");
            NorthLat = NorthData.getDouble("lat");
            NorthLng = NorthData.getDouble("lng");

            JSONObject SouthData = Bounds.getJSONObject("southwest");
            SouthLat = SouthData.getDouble("lat");
            SouthLng = SouthData.getDouble("lng");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng North = new LatLng(NorthLat, NorthLng);
        LatLng South = new LatLng(SouthLat, SouthLng);

        return new LatLngBoundData(North, South);
    }

    public static DistanceAndDuration getDistanceAndValue(String Json) {

        String DurationText = "";
        String DistanceText = "";
        int DurationValue = 0;
        int DistanceValue = 0;

        try {

            JSONObject DataJson = new JSONObject(Json);
            JSONArray Routes = DataJson.getJSONArray("routes");

            JSONObject RoutesObj = Routes.optJSONObject(0);
            JSONArray LegsObj = RoutesObj.getJSONArray("legs");
            JSONObject Legs = LegsObj.getJSONObject(0);

            Log.d("Request_DATA", "getDistanceAndValue: " + Legs.toString());

            JSONObject Distance = Legs.getJSONObject("distance");
            DistanceText = Distance.getString("text");
            DistanceValue = Distance.getInt("value");

            JSONObject Duration = Legs.getJSONObject("duration");
            DurationText = Duration.getString("text");
            DurationValue = Duration.getInt("value");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        DistanceAndDuration data = new DistanceAndDuration();
        data.setDistanceText(DistanceText);
        data.setDurationText(DurationText);
        data.setDurationValues(DurationValue);
        data.setDistanceValue(DistanceValue);
        return data;
    }

    public static ArrayList<LatLng> getDirectionData(String Json) {

        ArrayList<LatLng> direction = new ArrayList<>();

        try {
            JSONObject DataJson = new JSONObject(Json);
            JSONArray Routes = DataJson.getJSONArray("routes");

            JSONObject RoutesObj = Routes.optJSONObject(0);
            JSONArray LegsObj = RoutesObj.getJSONArray("legs");
            JSONObject Legs = LegsObj.getJSONObject(0);

            JSONArray Steps = Legs.getJSONArray("steps");

            for (int i = 0; i < Steps.length(); i++) {

                JSONObject data = Steps.getJSONObject(i);

                JSONObject start = data.getJSONObject("start_location");
                JSONObject Polyline = data.getJSONObject("polyline");
                JSONObject end = data.getJSONObject("end_location");

                double startLat = start.getDouble("lat");
                double startLng = start.getDouble("lng");

                direction.add(new LatLng(startLat,startLng));

                String decode = Polyline.getString("points");
                List<LatLng> decodePolyline = decodePolyline(decode);
                direction.addAll(decodePolyline);

                double endLat = end.getDouble("lat");
                double endLng = end.getDouble("lng");

                direction.add(new LatLng(endLat, endLng));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return direction;
    }

    private static ArrayList<LatLng> decodePolyline(String polyline) {

        ArrayList<LatLng> decodePoly = new ArrayList<LatLng>();

        int index = 0, len = polyline.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;

            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            decodePoly.add(position);

        }

        return decodePoly;

    }
}
