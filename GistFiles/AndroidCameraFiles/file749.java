import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class RestaurantListener implements View.OnClickListener
{
    private GoogleMap gmap;
    private Random rand;

    private static final RestaurantActivity.Restaurant [] RESTAURANTS = {new RestaurantActivity.Restaurant("J's 99 Grill", 44.849801, -123.229442),
            new RestaurantActivity.Restaurant("Burgerville", 44.84263, -123.229034),
            new RestaurantActivity.Restaurant("Little Ceasers", 44.848453, -123.230863),
            new RestaurantActivity.Restaurant("Sing Fay Chinese Restaurant", 44.848697, -123.237220)};
    
    
    public RestaurantListener(GoogleMap gm)
    {
        gmap = gm;
        rand = new Random();
    }
    
    
    @Override
    public void onClick(View v)
    {
        for(RestaurantActivity.Restaurant r : RESTAURANTS)
        {
            System.out.println(r);
            //should be Sing Fay, Sing Fay, Sing Fay, Sing Fay
        }
        RestaurantActivity.Restaurant chosen = randomRestaurant();
        LatLng temp = new LatLng(chosen.getLat(), chosen.getLon());
        System.out.println(chosen);
        gmap.clear();
        gmap.addMarker(new MarkerOptions().position(temp).title(chosen.getName()));

        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 14f));
    }

    //workaround for testing
    private RestaurantActivity.Restaurant randomRestaurant()
    {
        int choice = rand.nextInt(RESTAURANTS.length);
        switch (choice)
        {
            case 0:
                return new RestaurantActivity.Restaurant("J's 99 Grill", 44.849801, -123.229442);
            case 1:
                return new RestaurantActivity.Restaurant("Burgerville", 44.84263, -123.229034);
            case 2:
                return new RestaurantActivity.Restaurant("Little Ceasers", 44.848453, -123.230863);
            case 3:
                return new RestaurantActivity.Restaurant("Sing Fay Chinese Restaurant", 44.848697, -123.237220);
                default:
                    throw new RuntimeException("WTF");
        }
    }
}