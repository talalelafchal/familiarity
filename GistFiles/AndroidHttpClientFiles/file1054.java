import android.location.Address;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * see https://developers.google.com/maps/documentation/geocoding/?hl=de#Audience
 */
public class MyGeoCoder {

	private static final String TAG = "MyGeoCoder";
	
	public static List<Address> getFromLocationName(String address) {
		
        String url = null;
        try {
        	url = String.format("http://maps.google.com/maps/api/geocode/json?address=%s&ka&sensor=true&language="+Locale.getDefault().getCountry()
            		, URLEncoder.encode(address, "UTF-8"));
            Log.d(TAG, "url="+url);
        } catch (UnsupportedEncodingException e) {
            //should never happen
        }
        return getAddressListFromUrl(url);
    }

	
	
    public static List<Address> getFromLocationName(String address,String region) {
        String url = null;
        try {
        	url = String.format("http://maps.google.com/maps/api/geocode/json?address=%s&region=%s&ka&sensor=true&language="+Locale.getDefault().getCountry()
            		, URLEncoder.encode(address, "UTF-8"),
            		URLEncoder.encode(region, "UTF-8"));
            Log.d(TAG, "url="+url);
        } catch (UnsupportedEncodingException e) {
            //should never happen
        }
        return getAddressListFromUrl(url);
    }

    public static List<Address> getFromLocation(double lat, double lng) {
        String url = String
                .format(Locale.ENGLISH,
                        "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                                + Locale.getDefault().getCountry(), lat, lng);
        Log.d(TAG, "url="+url);
        return getAddressListFromUrl(url);
    }

    private static List<Address> getAddressListFromUrl(String url) {
        try {

            JSONObject jsonObject = new JSONObject(getStringFromRequest(url));
            Log.d(TAG, "result:"+jsonObject);
            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                return parseJSON(jsonObject);
            }

        } catch (ClientProtocolException e) {
            Log.e(MyGeoCoder.class.getName(),
                    "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            Log.e(MyGeoCoder.class.getName(),
                    "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e(MyGeoCoder.class.getName(),
                    "Error parsing Google geocode webservice response.", e);
        }

        return null;
    }

    private static List<Address> parseJSON(JSONObject jsonRAW) throws JSONException {
        JSONArray json = jsonRAW.getJSONArray("results");
        List<Address> ret = new ArrayList<Address>(json.length());
        for (int i = 0; i < json.length(); i++) {
            JSONObject o = json.getJSONObject(i);
            ret.add(parseAddress(o));
        }
        return ret;
    }

    private static Address parseAddress(JSONObject json) throws JSONException {
        Address addr = new Address(Locale.getDefault());

        String addressLines = json.optString("formatted_address");
        if (addressLines != null) {
            String[] lines = addressLines.split(", ");
            for (int i = 0; i < lines.length; i++) {
                addr.setAddressLine(i, lines[i]);
            }
        }

        JSONArray address_components = json.optJSONArray("address_components");
        if (address_components != null) {

            for (int i = 0; i < address_components.length(); i++) {
                JSONObject item = address_components.getJSONObject(i);
                JSONArray types = item.optJSONArray("types");
                if (types != null) {
                    fillType(addr, types, item);
                }
            }

        }

        JSONObject geometry = json.optJSONObject("geometry");
        if (geometry != null) {
            JSONObject location = geometry.optJSONObject("location");
            if (location != null) {
                addr.setLatitude(location.optDouble("lat"));
                addr.setLongitude(location.optDouble("lng"));
                if (Double.isNaN(addr.getLatitude())) {
                    addr.clearLatitude();
                }
                if (Double.isNaN(addr.getLongitude())) {
                    addr.clearLongitude();
                }
            }
        }

        return addr;
    }

    private static String getStringFromRequest(String url) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    private static void fillType(Address addr, JSONArray types, JSONObject json) throws JSONException {

        String long_name = json.optString("long_name");
        String short_name = json.optString("short_name");
        if (long_name == null) {
            long_name = short_name;
        }
        if (short_name == null) {
            short_name = long_name;
        }
        if (short_name == null) {
            return;
        }

        if (hasType(types, "postal_code")) {
            addr.setPostalCode(short_name);
        } else if (hasType(types, "country")) {
            addr.setCountryName(long_name);
            addr.setCountryCode(short_name);
        } else if (hasType(types, "locality")) {
            addr.setLocality(long_name);
        } else if (hasType(types, "administrative_area_level_1")) {
            addr.setAdminArea(long_name);
        } else if (hasType(types, "administrative_area_level_2")) {
            addr.setSubAdminArea(long_name);
        } else if (hasType(types, "point_of_interest")) {
            addr.setFeatureName(long_name);
        } else if (hasType(types, "premise")) {
            addr.setPremises(long_name);
        } else if (hasType(types, "sublocality")) {
            addr.setSubLocality(long_name);
        } else if (hasType(types, "route")) {
            addr.setThoroughfare(long_name);
        } else if (hasType(types, "street_number")) {
            addr.setFeatureName(long_name);
        }
    }

    private static boolean hasType(JSONArray types, String type) throws JSONException {
        for (int i = 0; i < types.length(); i++) {
            if (type.equals(types.getString(i))) {
                return true;
            }
        }
        return false;
    }

}

