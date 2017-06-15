package com.felixglusch.shippotest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.shippo.Shippo;
import com.shippo.exception.ShippoException;
import com.shippo.model.Address;

import java.util.HashMap;
import java.util.Map;

/*
    You can look through all of the test classes for each particular model for examples.
    For useful debugging information including headers, server raw response etc, set Shippo.setDEBUG(true);

    https://github.com/goshippo/shippo-java-client
*/

public class MainActivity
        extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Shippo.apiKey = "<private auth key>";

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("object_purpose", "PURCHASE");
        addressMap.put("name", "Shippo Itle");
        addressMap.put("company", "Shippo");
        addressMap.put("street1", "215 Clayton St.");
        addressMap.put("city", "San Francisco");
        addressMap.put("state", "CA");
        addressMap.put("zip", "94117");
        addressMap.put("country", "US");
        addressMap.put("phone", "+1 555 341 9393");
        addressMap.put("email", "laura@goshipppo.com");

        new AsyncShippo().execute(addressMap);
    }

    public class AsyncShippo
            extends AsyncTask<Map<String, Object>, Void, String>
    {
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            System.out.println(s);
        }

        @Override
        protected String doInBackground(Map<String, Object>... maps)
        {
            try
            {
                Address address = Address.create(maps[0]);
                return address.toString();
            } catch ( ShippoException e )
            {
                e.printStackTrace();
            }
            return "***create failed***";
        }
    }
}