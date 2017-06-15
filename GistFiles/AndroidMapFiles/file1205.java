package your.app.package;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("SpellCheckingInspection")
class ServerRequest
{
    private final String TAG = "ServerRequest";
    private ServerRequestInterface interf;
    private int index = 0;
    private ArrayList<Business> businesses = new ArrayList<>();
  
    ServerRequest(ServerRequestInterface i)
    {
        interf = i;
    }
  
    //----------------------------------------------------------------------------------------//
    // Get yelp access token
    //----------------------------------------------------------------------------------------//

    void getYelpAccessToken()
    {
        String url = "https://api.yelp.com/oauth2/token?" + "grant_type=client_credentials" +
                     "&client_id=" + MyApp.YELP_APP_ID + "&client_secret=" + MyApp.YELP_APP_SECRET;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String yelpAccessToken = response.getString("access_token");
                            interf.yelpAccessTokenRequestResult("Success", yelpAccessToken);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            interf.yelpAccessTokenRequestResult("An error occurred, please try again.", null);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Error while getting Yelp Access Token: " + error.toString());
                        interf.yelpAccessTokenRequestResult("An error occurred, please try again.", null);
                    }
                });

        MyApp app = MyApp.getInstance();
        app.addJsonRequestToQueue(jsObjRequest);
    }
  
    //----------------------------------------------------------------------------------------//
    // Fetch places
    //----------------------------------------------------------------------------------------//

    void fetchPlaces(double userLat, double userLong, String yelpAccessToken, String placeCategory)
    {
        String url = "https://api.yelp.com/v3/businesses/search?" +
                     "latitude=" + userLat + "&longitude=" + userLong +
                     "&categories=" + placeCategory + "&locale=en_US" +
                     "&limit=" + 50 + "&sort_by=distance";

        PlacesRequest.yelpAccessToken = yelpAccessToken;

        PlacesRequest placesRequest = new PlacesRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray businessesArray = response.getJSONArray("businesses");

                            if (businessesArray.length() > 0)
                            {
                                if (businesses.size() > 0)
                                {
                                    businesses.clear();
                                }
                                
                                createBusinessObjects(businessesArray);
                            }
                            else
                            {
                                interf.searchPlacesResult("Search returned no results for your surroundings.", null);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            interf.searchPlacesResult("An error occurred, please try again.", null);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        interf.searchPlacesResult("An error occurred or this app has reached it's daily search quota.", null);
                    }
                });
        
        MyApp app = MyApp.getInstance();
        app.addJsonRequestToQueue(placesRequest);
    }

    //----------------------------------------------------------------------------------------//
    // Create Business objects
    //----------------------------------------------------------------------------------------//

    private void createBusinessObjects(JSONArray businessesArray)
    {
        try
        {
            JSONObject obj = businessesArray.getJSONObject(index);

            Business business = new Business(obj);

            if (!business.isPlaceClosed())
            {
                if (!business.getPlaceName().equals("") && !business.getPlaceAddr().equals("") &&
                    (business.getPlaceLatitude() != 0) && (business.getPlaceLongitude() != 0))
                {
                    businesses.add(business);
                }
            }

            if (index < (businessesArray.length() - 1))
            {
                index++;
                createBusinessObjects(businessesArray);
            }
            else
            {
                index = 0;

                if (businesses.size() > 0)
                {
                    interf.searchPlacesResult("Success", businesses);
                }
                else
                {
                    interf.searchPlacesResult("Search returned no results for your surroundings.", null);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            if (index < (businessesArray.length() - 1))
            {
                index++;
                createBusinessObjects(businessesArray);
            }
            else
            {
                index = 0;

                if (businesses.size() > 0)
                {
                    interf.searchPlacesResult("Success", businesses);
                }
                else
                {
                    interf.searchPlacesResult("Search returned no results for your surroundings.", null);
                }
            }
        }
    }
}