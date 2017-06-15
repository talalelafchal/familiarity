package com.survivingwithandroid.weathertest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Sample WeatherLib client init
        try {
            WeatherClient
                    client = (new WeatherClient.ClientBuilder())
                          .attach(this)
                          .httpClient(WeatherDefaultClient.class)
                          .provider(new OpenweathermapProviderType())
                          .config(new WeatherConfig())
                          .build();

            client.getCurrentCondition(new WeatherRequest("2988507"),
                      new WeatherClient.WeatherEventListener() {

                          @Override public void onWeatherRetrieved(CurrentWeather currentWeather) {
                               float currentTemp = currentWeather.weather.temperature.getTemp();
                               Log.d("WL", "City ["+currentWeather.weather.location.getCity()+"] Current temp ["+currentTemp+"]");
                          }

                          @Override public void onWeatherError(WeatherLibException e) {
                              Log.d("WL", "Weather Error - parsing data");
                              e.printStackTrace();
                          }

                          @Override
                          public void onConnectionError(Throwable throwable) {
                              Log.d("WL", "Connection error");
                              throwable.printStackTrace();
                          }
                      });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
