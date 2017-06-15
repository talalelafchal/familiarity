package your.app.package;

public class MainActivity extends Activity implements LocationListener, ServerRequestInterface
{
    private ServerRequest serverRequest;
    private String yelpAccessToken = "";
    private double userLat = 0;
    private double userLong = 0;
    
    private String[] placesNamesArray = {
            "Restaurants", "Cafes", "Bakeries", "Bars", "Supermarkets", "Bookstores",
            "Shopping", "Home goods", "Pets", "Museums", "Theaters", "Movie theaters",
            "Churches", "Parks, Plazas & Zoos", "Bus stations", "Subway stations",
            "Beauty salons", "Gym", "Pharmacies", "Hospitals"
    };
    
    // Font: https://www.yelp.com/developers/documentation/v3/all_category_list
    private String[] placeCategoriesArray = {
            "foodtrucks,restaurants,kiosk,dancerestaurants",
            "acaibowls,bubbletea,churros,coffee,convenience,cupcakes,delicatessen,empanadas,friterie,gelato,icecream,internetcafe,milkshakebars,cakeshop,pretzels,tea,tortillas,coffeeshops",
            "bakeries,bagels,donuts",
            "barcrawl,bars,beergardens,jazzandblues,pianobars,poolhalls",
            "butcher,ethicgrocery,farmersmarket,fishmonger,grocery,intlgrocery,organic_stores,markets,seafoodmarkets",
            "bookstores,comicbooks,musicvideo,mags,usedbooks",
            "childcloth,deptstores,hats,lingerie,maternity,menscloth,plus_size_fashion,shoes,sleepwear,sportswear,swimwear,vintage,womenscloth,shoppingcenters,thrift_stores,watches,wigs",
            "furniture,homedecor,mattresses,gardening,outdoorfurniture,paintstores,tableware",
            "emergencypethospital,groomer,vet",
            "galleries,museums,planetarium",
            "opera,theater",
            "movietheaters",
            "churches,synagogues",
            "gardens,parks,playgrounds,publicplazas,zoos",
            "busstations",
            "metrostations",
            "barbers,eyebrowservices,hair_extensions,waxing,hair,makeupartists,othersalons",
            "cardioclasses,dancestudio,gyms,pilates,swimminglessons,taichi,yoga,gymnastics",
            "drugstores,herbalshops,pharmacy,vitaminssupplements",
            "emergencyrooms,hospitals,medcenters,urgent_care"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverRequest = new ServerRequest(this);

        if (savedInstanceState != null)
        {
            yelpAccessToken = savedInstanceState.getString("yelpAccessToken");
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("yelpAccessToken", yelpAccessToken);
        super.onSaveInstanceState(outState);
    }
    
    //----------------------------------------------------------------------------------------//
    // LocationListener
    //----------------------------------------------------------------------------------------//

    @Override
    public void onLocationChanged(Location location) // Not called in simulator
    {
        // Note: See my gist -> https://gist.github.com/nissivm/b3832ecaaa5d46af2e53c60626a5efee
    
        userLat = location.getLatitude();
        userLong = location.getLongitude();

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        startPlacesFetching();
    }
    
    //----------------------------------------------------------------------------------------//
    // Start places fetching
    //----------------------------------------------------------------------------------------//
    
    private void startPlacesFetching()
    {
        if (yelpAccessToken.equals(""))
        {
            serverRequest.getYelpAccessToken();
        }
        else
        {
            serverRequest.fetchPlaces(userLat, userLong, yelpAccessToken, placeCategoriesArray[0]);
        }
    }
    
    //----------------------------------------------------------------------------------------//
    // ServerRequestInterface
    //----------------------------------------------------------------------------------------//

    @Override
    public void yelpAccessTokenRequestResult(String result, String acessToken)
    {
        if (result.equals("Success"))
        {
            yelpAccessToken = acessToken;
            serverRequest.fetchPlaces(userLat, userLong, yelpAccessToken, placeCategoriesArray[0]);
        }
        else
        {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, result, duration);
            toast.show();
        }
    }

    @Override
    public void searchPlacesResult(String result, ArrayList<Business> businesses)
    {
        if (result.equals("Success"))
        {
            // Use businesses objcets somehow
        }
        else
        {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, result, duration);
            toast.show();
        }
    }
}