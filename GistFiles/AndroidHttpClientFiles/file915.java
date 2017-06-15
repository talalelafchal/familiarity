package yv.trip.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yv.trip.R;
import yv.trip.XMLParser;

/**
 * Flights fragment
 */
public class FlightsFragment extends Fragment {

    private List<HashMap<String,String>> flightsList = new ArrayList<>();

    private Map<String, List<String>> flightsInfo = new HashMap<>();

    private SimpleAdapter simpleAdpt = null;

    private String END_POINT = "https://flightlookup-flightmetadata-uri.p.mashape.com/FlightMetaData/?";

    private String MASHAPE_KEY = "ENTER_MASHAPE_KEY_HERE";

    // save the fragment context menu
    private ContextMenu ctxMenu;

    private int INDEX_FROM          = 0;
    private int INDEX_TO            = 1;
    private int INDEX_DATE          = 2;
    private int INDEX_AIRLINE       = 3;
    private int INDEX_FLIGHT_NUMBER = 4;
    private int INDEX_FROM_CODE     = 5;
    private int INDEX_TO_CODE       = 6;


    public FlightsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.flights_main, container, false);

        initList();

        // getting the ListView component from the layout
        ListView lv = (ListView) rootView.findViewById(R.id.listView);

        simpleAdpt = new SimpleAdapter(rootView.getContext(), flightsList, R.layout.trip_list_item, new String[]{"flight"}, new int[]{android.R.id.text1});

        lv.setAdapter(simpleAdpt);

        // react to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {

                TextView clickedView = (TextView) view.findViewById(android.R.id.text1);

                Toast.makeText(rootView.getContext(), clickedView.getText() , Toast.LENGTH_SHORT).show();
            }
        });

        registerForContextMenu(lv);

        return rootView;
    }

    private void initList() {
        // We populate the flights details
        flightsList.add(createFlight("Tel Aviv", "Moscow", "03/24/2015", "SU", "505", "TLV", "VKO"));
        flightsList.add(createFlight("Moscow", "Tokyo", "03/24/2015", "SU", "260", "VKO", "NRT"));
        flightsList.add(createFlight("Osaka", "Hong Kong", "04/14/2015", "AI", "315", "KIX", "HKG"));
        flightsList.add(createFlight("Hong Kong", "Guilin", "04/19/2015", "CX", "5700", "HKG", "KWL"));
        flightsList.add(createFlight("Guilin", "Beijing", "04/23/2015", "CA", "1312", "KWL", "PEK"));
        flightsList.add(createFlight("Beijing", "Moscow", "04/28/2015", "UN", "8888", "PEK", "VKO"));
        flightsList.add(createFlight("Moscow", "Tel Aviv", "04/28/2015", "UN", "301", "VKO", "TLV"));
    }

    private HashMap<String, String> createFlight(String from, String to, String date, String airline, String flightNumber, String fromCode, String toCode) {
        HashMap<String, String> flight = new HashMap<>();
        String key = from + " - " + to;
        flight.put("flight", key);
        List<String> info = new ArrayList<>();
        info.add(INDEX_FROM, from);
        info.add(INDEX_TO, to);
        info.add(INDEX_DATE, date);
        info.add(INDEX_AIRLINE, airline);
        info.add(INDEX_FLIGHT_NUMBER, flightNumber);
        info.add(INDEX_FROM_CODE, fromCode);
        info.add(INDEX_TO_CODE, toCode);
        flightsInfo.put(key, info);
        return flight;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        ctxMenu = menu;

        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        HashMap map =  (HashMap) simpleAdpt.getItem(aInfo.position);

        String key = map.get("flight").toString();

        List<String> flightInfo = flightsInfo.get(key);

        menu.setHeaderTitle(map.get("flight").toString());
        menu.add(1, 1, 1, flightInfo.get(INDEX_DATE));
        menu.add(1, 2, 2, "please wait...");

        DataFetcher df = new DataFetcher();
        df.execute(key);
    }

    private class DataFetcher extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return generateRequest(params[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            ctxMenu.removeItem(2);
            ctxMenu.add(1, 2, 2, result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        private String generateRequest(String key) {
            try {
                String result = "";

                List<String> flightInfo = flightsInfo.get(key);

                String path = addParamsToUrl(END_POINT, flightInfo);

                // setting the client and the request
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGetReq = new HttpGet(path);

                httpGetReq.addHeader("X-Mashape-Key", MASHAPE_KEY);
                httpGetReq.addHeader("Accept", "text/plain");

                HttpResponse httpresponse = httpclient.execute(httpGetReq);

                HttpEntity httpEntity = httpresponse.getEntity();
                String xml = EntityUtils.toString(httpEntity);

                XMLParser parser = new XMLParser();

                Document doc = parser.getDomElement(xml);

                if(doc == null) {
                    return "Flight not found";
                }

                NodeList nl = doc.getElementsByTagName("segment");

                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    result = e.getAttribute("DepartureTime");
                    result += ", ";
                    result += e.getAttribute("DepartureTerminal");
                }

                return result;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "error";
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return "error";
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        /**
         * adding the flight parameters to the url
         * @param url - the base end point url
         * @param flightInfo - flight info list
         * @return {String}
         */
        protected String addParamsToUrl(String url, List<String> flightInfo){
            if(!url.endsWith("?")) {
                url += "?";
            }

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Airline", flightInfo.get(INDEX_AIRLINE)));
            params.add(new BasicNameValuePair("Date", flightInfo.get(INDEX_DATE)));
            params.add(new BasicNameValuePair("FlightNumber", flightInfo.get(INDEX_FLIGHT_NUMBER)));
            params.add(new BasicNameValuePair("From", flightInfo.get(INDEX_FROM_CODE)));
            params.add(new BasicNameValuePair("To", flightInfo.get(INDEX_TO_CODE)));
            params.add(new BasicNameValuePair("Airline", flightInfo.get(INDEX_AIRLINE)));
            params.add(new BasicNameValuePair("TimeRange", "ANY"));

            String paramString = URLEncodedUtils.format(params, "utf-8");

            url += paramString;
            return url;
        }
    }
}