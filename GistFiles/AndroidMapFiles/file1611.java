package app.sunshine.anroid.example.com.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//import android.text.format.Time;

//import android.text.format.Time;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu,
                                     MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will automatically
        // handle clicks on the Home/up button, so long as you specify a parent
        // activity in AndroidManifest.xml
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);


        // create some dummy data for the ListView
        String[] data = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Rainy - 64/51",
                "Fri - Foggy - 70/46",
                "Sat - Sunny - 76/68"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the TextView to populate.
                weekForecast // forecast data
        );


        ListView weatherListView = (ListView) rootView.findViewById(R.id.ListViewForecast);

        weatherListView.setAdapter(mForecastAdapter);
        //weatherListView.setAdapter(mForecastAdapter);
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();// if class being renamed, this will throw error.

        /*
        * Prepare the weather high/lows for presentation
        * */
        private String formatHighLows(double high, double low){
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         *  Take the String representing the complete forecast in JSON Format and
         *  pull out the data we need to construct the Strings needed for the wireframes.
         *
         *  Fortunately parsing is easy: constructor takes the JSON string and converts
         *  it into an Object hierarchy for us.
         * */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String OWN_LIST = "list";
            final String OWN_WEATHER = "weather";
            final String OWN_TEMPERATURE = "temp";
            final String OWN_MAX = "max";
            final String OWN_MIN = "min";
            final String OWN_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWN_LIST);



            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++){
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day.
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Create Gregorian Calender, which is in current date
                GregorianCalendar gc = new GregorianCalendar();
                // add i dates to current date of calendar
                gc.add(GregorianCalendar.DATE, i);
                // get the date, format it, and "save" it on variable day
                Date time = gc.getTime();
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                day = shortenedDateFormat.format(time);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWN_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWN_DESCRIPTION);

                // Temperatures are in a child object called "temp". Try not to name variables
                // "temp" when working with temperature. It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWN_TEMPERATURE);
                double high = temperatureObject.getDouble(OWN_MAX);
                double low = temperatureObject.getDouble(OWN_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            //for (String s : resultStrs){
            //    Log.v(LOG_TAG, "Forecast entry: " + s);
            //}
            return resultStrs;
        }
        @Override
        protected String[] doInBackground(String... params) {
            String[] weatherOutcome;

            // If there's no zip code, there's nothing to look up. Verify size of params.
            if (params.length == 0){
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw Json response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try{
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //String baseUrl
                //        = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,us&mode=json&cnt=7&units=metric";
                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseUrl.concat(apiKey));

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY).build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    // Stream was empty. No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                //Log.v(LOG_TAG,"Forecast string: " + forecastJsonStr);

            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the weather data, there's no point
                // in attempting to parse it.
                return null;
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }

            // this will happen only if there was an error getting or parsing the forecast.
            return null;

        }

        @Override
        protected void onPostExecute(String[] result){
            if (result != null){
                mForecastAdapter.clear();

                /*
                List<String> resultList = new ArrayList<String>(Arrays.asList(result));
                mForecastAdapter = new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the TextView to populate.
                        resultList // forecast data
                );*/

                for(String dayForecastStr : result){
                    mForecastAdapter.add(dayForecastStr);
                }
                //mForecastAdapter.addAll(result);
            }

        }

    }
}
