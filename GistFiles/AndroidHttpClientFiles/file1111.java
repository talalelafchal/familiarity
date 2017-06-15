package com.matpompili.settle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matteo on 21/10/14.
 */
public class RoomView extends Activity {
    BuildingObject building;
    SwingBottomInAnimationAdapter animationAdapter;
    SwipeRefreshLayout swipeView;

    private class clickTimer {
        public int id;
        public long time;

        clickTimer() {
            id = -1;
            time = 0;
        }

        public boolean isDoubleClick(int id){
            if (id != this.id) {
                this.id = id;
                time = System.currentTimeMillis();
                return false;
            } else {
                if(System.currentTimeMillis()-time < 500){
                    this.id = -1;
                    time = 0;
                    return true;
                } else {
                    time = System.currentTimeMillis();
                    return false;
                }
            }
        }
    }

    clickTimer checkDoubleClick;
    Toast clickToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomview);
        //disable application icon from ActionBar
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        building = (BuildingObject)getIntent().getSerializableExtra("building");

        setTitle(Utilities.capitalize(building.name));

        // Construct the data source
        // Create the adapter to convert the array to views
        RoomAdapter adapter = new RoomAdapter(this, building);
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

        checkDoubleClick = new clickTimer();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (clickToast != null){
                    clickToast.cancel();
                }
                if (!checkDoubleClick.isDoubleClick(position)){
                    clickToast = Toast.makeText(getApplicationContext(), "Fai doppio click su una scheda per inviare un aggiornamento", Toast.LENGTH_LONG);
                } else {
                    //clickToast = Toast.makeText(getApplicationContext(), "Rilevato doppio click", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(RoomView.this, SendUpdateView.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("room", building.rooms.get(position));
                    bundle.putSerializable("buildingName", building.name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                clickToast.show();
            }
        });
        new AsyncRefresh().execute();
        //listView.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.root, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_info:
                Toast.makeText(getApplicationContext(), "Applicazione realizzata da Matteo Pompili: matpompili@gmail.com", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncRefresh extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            if(Utilities.isOnline()){
                building.rooms.clear();
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
                building.getRooms();
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


}
