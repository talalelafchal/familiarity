package com.example.laptop.navdrawerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;




import adapter.ExpandablesListAdapter;
import model.ExpandedMenuModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String url = "http://93.103.22.144/konkurs/list.json";
    private static ImageView imgstar,imghd;
    private static ProgressDialog progressDialog;

    DrawerLayout mDrawerLayout;
    ExpandablesListAdapter listAdapter;
    VideoView video;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ExpandedMenuModel>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgstar = (ImageView)findViewById(R.id.imageViewstar);
        imghd = (ImageView)findViewById(R.id.imageViewhd);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        progressDialog = ProgressDialog.show(this,"","Please wait..",true);

        new loads().execute();

        String path = "http://best.str.nettvplus.com:8080/stream?sp=partners&u=konkurs&p=k0nk00rs&player=m3u8&channel=rts1&stream=1mb";
        video = (VideoView)findViewById(R.id.videodemand);
        Uri uri = Uri.parse(path);
        video.setVideoURI(uri);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                video.start();

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listeners();
    }

    private void listeners()
    {
       expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
       {
           public boolean onChildClick(ExpandableListView parent, View v,
                                       int groupPosition, int childPosition, long id) {
               boolean check= true;
               ExpandedMenuModel model = new ExpandedMenuModel();

                    model = (ExpandedMenuModel)listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);


                   getChaneel(model);

                   if(model.getFav()==check)
                   {
                       imgstar.setVisibility(View.VISIBLE);
                   }
                   else
                   {
                       imgstar.setVisibility(View.INVISIBLE);
                   }
                   if(model.getHd()==check)
                   {
                       imghd.setVisibility(View.VISIBLE);
                   }
                   else
                   {
                       imghd.setVisibility(View.INVISIBLE);
                   }



               return false;
           }
       });
    }


    private void getChaneel(ExpandedMenuModel model) {

        String path = "http://best.str.nettvplus.com:8080/stream?sp=partners&u=konkurs&p=k0nk00rs&player=m3u8&channel="+model.getPp()+"&stream="+model.getQ();
        Uri uri = Uri.parse(path);
        progressDialog = ProgressDialog.show(this,"","Please wait..",true);

        video.setVideoURI(uri);

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                video.start();

            }
        });


    }

    public class loads extends AsyncTask<Void, Void, Void> {

       HttpURLConnection urlConnection;

       @Override
       protected Void doInBackground(Void... params) {


           try {

               HttpHandler sh = new HttpHandler();

               String jsonStr = sh.makeServiceCall(url);
               listDataHeader = new ArrayList<String>();
               listDataHeader.add("All channels");
               listDataHeader.add("Favourites");
               listDataHeader.add("HD");
               listDataChild = new HashMap<String,List<ExpandedMenuModel>>();

               if(jsonStr!=null) {
                   try {
                       JSONObject jsonObj = new JSONObject(jsonStr);
                       JSONArray channels = jsonObj.getJSONArray("channels_list");

                       List<ExpandedMenuModel> all = new ArrayList<ExpandedMenuModel>();
                       List<ExpandedMenuModel> favor = new ArrayList<ExpandedMenuModel>();
                       List<ExpandedMenuModel> hdd = new ArrayList<ExpandedMenuModel>();

                       for (int i = 0; i < channels.length(); i++) {

                           ExpandedMenuModel model = new ExpandedMenuModel();
                           JSONObject c = channels.getJSONObject(i);
                           model.setId(Integer.parseInt(c.getString("id")));
                           model.setName(c.getString("name"));
                           model.setFav(c.getBoolean("fav"));
                           model.setNum(c.getString("num"));
                           model.setHd(c.getBoolean("hd"));
                           model.setPp(c.getString("pp"));
                           model.setQ(c.getString("q"));
                           all.add(model);

                           if (model.getFav()) {
                               favor.add(model);
                           }
                           if (model.getHd()) {
                               hdd.add(model);
                           }

                       }
                        Collections.sort(all, new Comparator<ExpandedMenuModel>() {
                            @Override
                            public int compare(ExpandedMenuModel o1, ExpandedMenuModel o2) {
                                return Integer.parseInt(String.valueOf(o1.getId())) -Integer.parseInt(String.valueOf(o2.getId()));
                            }
                        });
                       Collections.sort(favor, new Comparator<ExpandedMenuModel>() {
                           @Override
                           public int compare(ExpandedMenuModel o1, ExpandedMenuModel o2) {
                               return Integer.parseInt(String.valueOf(o1.getNum()))-Integer.parseInt(String.valueOf(o2.getNum()));
                           }
                       });
                       Collections.sort(hdd, new Comparator<ExpandedMenuModel>() {
                           @Override
                           public int compare(ExpandedMenuModel o1, ExpandedMenuModel o2) {
                               return Integer.parseInt(String.valueOf(o1.getNum()))-Integer.parseInt(String.valueOf(o2.getNum()));
                           }
                       });
                       listDataChild.put(listDataHeader.get(0), all);
                       listDataChild.put(listDataHeader.get(1), favor);
                     listDataChild.put(listDataHeader.get(2), hdd);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }

               return null;
           }
           finally {

           }

       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           listAdapter = new ExpandablesListAdapter(MainActivity.this,listDataHeader,listDataChild);
           expListView.setAdapter(listAdapter);
           listAdapter.notifyDataSetChanged();
       }
   }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Exiting already?");


        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        finish();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       // Toast.makeText(MainActivity.this,"I knew you wouldn't leave me :)",Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera)
//        {
//
//
//        }
//        else if (id == R.id.nav_gallery)
//        {
//
//        }
//        else if (id == R.id.nav_slideshow)
//        {
//
//        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                        return true;
                    }
                });
    }


}


