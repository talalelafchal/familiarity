package your.app.package;

import android.app.Application;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;

// Note: https://www.yelp.com/developers/documentation/v3/get_started

public class MyApp extends Application
{
    static final String YELP_APP_ID = "your-yelp-app-id";
    static final String YELP_APP_SECRET = "your-yelp-app-secret";
    private static final String TAG = "MyApp";
    private static MyApp mInstance;
    private RequestQueue mRequestQueue;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);
    }
    
    //----------------------------------------------------------------------------------------//
    // Get instance singleton
    //----------------------------------------------------------------------------------------//

    public static synchronized MyApp getInstance()
    {
        return mInstance;
    }

    //----------------------------------------------------------------------------------------//
    // Volley
    //----------------------------------------------------------------------------------------//

    public void addJsonRequestToQueue(JsonObjectRequest jsObjRequest)
    {
        jsObjRequest.setTag(TAG);
        mRequestQueue.add(jsObjRequest);
    }

    private void cancelPendingRequests()
    {
        mRequestQueue.cancelAll(TAG);
    }
}