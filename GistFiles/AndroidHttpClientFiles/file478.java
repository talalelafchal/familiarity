package com.matpompili.settle;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;


public class Root extends Activity {
    ArrayList<BuildingObject> buildings;
    SwingBottomInAnimationAdapter animationAdapter;
    SwipeRefreshLayout swipeView;
    MenuItem menuUpdateApp;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
    private static String KEY_FIRST_RUN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CHECK FIRST RUN
        sharedPreferences = getSharedPreferences("com.matpompili.settle", MODE_PRIVATE);
        if (!sharedPreferences.contains("KEY_FIRST_RUN")) {
            KEY_FIRST_RUN = getString(R.string.version);
            Intent intent = new Intent(Root.this, Welcome.class);
            startActivity(intent);
        } else {

        }
        editor = sharedPreferences.edit();
        editor.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
        editor.commit();

        setContentView(R.layout.activity_root);

        buildings = new ArrayList<BuildingObject>();

        BuildingAdapter adapter = new BuildingAdapter(this, buildings);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.listView);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeView.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeView.setEnabled(false);

        animationAdapter = new SwingBottomInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(listView);
        listView.setAdapter(animationAdapter);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncRefresh().execute();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                swipeView.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(Root.this, RoomView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("building", buildings.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //check internet connection
        new CheckUpdate().execute();
        new AsyncRefresh().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.root, menu);
        menuUpdateApp=menu.getItem(0);
        menuUpdateApp.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_update_app:
                String url = "http://matpompili.altervista.org/settle/settle.apk";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Scaricando l'ultima versione");
                request.setTitle("Aggiornamento di Settle");
                // in order for this if to run, you must use the android 3.2 to compile your app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "settle.apk");
                request.setMimeType("application/vnd.android.package-archive");

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);


/*                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://matpompili.altervista.org/settle/settle.apk"));
                startActivity(browserIntent);*/
                return true;
            case R.id.action_info:
                Toast.makeText(getApplicationContext(), "Applicazione realizzata da Matteo Pompili: matpompili@gmail.com", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void getBuildings() throws XmlPullParserException, IOException {
        buildings.clear();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        //System.out.print("Started Buildings Download");
        InputStream is = new URL(getString(R.string.xml_remote_url)+"buildings").openConnection().getInputStream();
        parser.setInput(is, null);
        //System.out.print("Finished Download");
        int eventType = parser.getEventType();
        String text = null;
        String tagName;
        BuildingObject building = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("building")) {
                        // create a new instance of employee
                        building = new BuildingObject();
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("building")) {
                        // add employee object to list
                        this.buildings.add(building);
                    } else if (tagName.equalsIgnoreCase("name")) {
                        assert building != null;
                        building.setName(text);
                    } else if (tagName.equalsIgnoreCase("id")) {
                        assert building != null;
                        building.setBuildingID(text);
                    } else if (tagName.equalsIgnoreCase("rooms")) {
                        assert building != null;
                        building.setRoomCount(Integer.parseInt(text));
                    } else if (tagName.equalsIgnoreCase("imageURL")) {
                        assert building != null;
                        building.imageURL = text;
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    private class AsyncRefresh extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            if (Utilities.isOnline()) {
                buildings.clear();
                animationAdapter.notifyDataSetChanged();
                swipeView.setRefreshing(true);
            } else {
                Toast.makeText(getApplicationContext(), "Sembra che tu non sia connesso a internet, riprova quando lo sarai!", Toast.LENGTH_LONG).show();
                swipeView.setRefreshing(false);
                cancel(true);
            }
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                getBuildings();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            animationAdapter.notifyDataSetChanged();
            swipeView.setRefreshing(false);
        }
    }

    private class CheckUpdate extends  AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            if (!Utilities.isOnline()) {
                Toast.makeText(getApplicationContext(), "Sembra che tu non sia connesso a internet, riprova quando lo sarai!", Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String version = null;

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getString(R.string.xml_remote_url)+"version");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                version = EntityUtils.toString(httpEntity);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return version;
        }

        @Override
        protected void onPostExecute(String version) {
            if (version == null){
                Toast.makeText(getApplicationContext(), "Connessione al server non riuscita", Toast.LENGTH_LONG).show();
            } else {
                if (!version.equalsIgnoreCase(getString(R.string.version))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.old_version_alert), Toast.LENGTH_LONG).show();
                    menuUpdateApp.setEnabled(true);
                } else {
                    System.out.println("Versione aggiornata.");
                }
            }
        }
    }
}
