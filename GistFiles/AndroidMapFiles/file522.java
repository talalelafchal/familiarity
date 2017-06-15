package ca.ubc.cs.cpsc210.meetup.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Anna on 2015-03-18.
 */
public class GeoParser {
    ArrayList<GeoPoint> gPoints;

    public ArrayList<GeoPoint> parse(String input) {
        gPoints = new ArrayList<GeoPoint>();


        try {
            JSONTokener tokener = new JSONTokener(input);
            JSONObject obj = new JSONObject(tokener);
            JSONObject route = obj.getJSONObject("route");
            JSONObject shape = route.getJSONObject("shape");
            JSONArray shapePoints = shape.getJSONArray("shapePoints");


            for (int i = 0; i < shapePoints.length(); i+= 2) {
                Double lon = shapePoints.getDouble(i);
                Double lat = shapePoints.getDouble(i+1);
                GeoPoint value = new GeoPoint(lon,lat);
                gPoints.add(value);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gPoints;
    }
}