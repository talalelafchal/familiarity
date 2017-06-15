import android.location.Address;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MSaudi on 12/27/2014.
 */
public class LocationUtils {

    /**
     *
     * @param lat
     * @param lng
     * @param maxResult the number of addresses you want that matches this location, usually 1
     * @param lang  ar for Arabic , en for English 
     * @return
     */
    public static List<Address> getFromLocation(double lat, double lng, int maxResult, String lang){

        String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+new Locale(lang), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            String jsonText = EntityUtils.toString(entity, HTTP.UTF_8);

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(jsonText);


            retList = new ArrayList<Address>();

            String locality=null;
            String subLocality=null;
            String route=null;
            String country=null;
            if("OK".equalsIgnoreCase(jsonObject.getString("status"))){
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0;i<results.length();i++ ) {

                    JSONObject result = results.getJSONObject(i);
                    JSONArray addressComonents = result.getJSONArray("address_components");
                    for (int j = 0; j < addressComonents.length(); j++) {
                        JSONObject addComponent = addressComonents.getJSONObject(j);
                        if (addComponent.getString("types").contains("locality"))
                            locality = addComponent.getString("long_name");
                        if (addComponent.getString("types").contains("sublocality"))
                            subLocality = addComponent.getString("long_name");

                        if (addComponent.getString("types").contains("route"))
                            route = addComponent.getString("long_name");

                        if (addComponent.getString("types").contains("country"))
                            country = addComponent.getString("short_name");


                    }
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(new Locale("ar"));

                    addr.setAddressLine(0, route);
                    addr.setLocality(locality);
                    addr.setSubLocality(subLocality);

                    if (country.equalsIgnoreCase("sa"))
                        addr.setCountryName(country);
                    else
                        addr.setCountryName(null);
                    retList.add(addr);

                }
            }


        } catch (ClientProtocolException e) {
            Log.e(LocationUtils.class.getName(), "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            Log.e(LocationUtils.class.getName(), "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e(LocationUtils.class.getName(), "Error parsing Google geocode webservice response.", e);
        }

        return retList;
    }

}
