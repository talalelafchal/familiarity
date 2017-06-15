package batchadd.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends Activity{

    public static ArrayList<LatLng> list = new ArrayList<LatLng>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TransparentMap);
        setContentView(R.layout.activity_main);
        populateData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void gotoMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        this.startActivityForResult(intent, 0x4CFE4E2);
    }

    //fake date, would be from db or something, just a proof of concept
    public void populateData() {
            double latitude = 37.6928;
            double longitude = -122.073;
            for (int i = 0; i < 1000; i++) {
                latitude = latitude + 0.01;
                longitude = longitude + 0.01;
                addLocation(latitude, longitude);}
    }

    private void addLocation(double latitude, double longitude) {
        LatLng ll = new LatLng(latitude, longitude);
        list.add(ll);
    }
}