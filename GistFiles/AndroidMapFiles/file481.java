package com.gmail.fedorenko.kostia.app1lesson4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by kfedoren on 23.09.2015.
 */
public class ShowItemActivity extends ActionBarActivity {
    private static final String TAG = "ShowItemActivity";
    private static TextView showtime;
    private static TextView showdate;
    private static TextView showdesc;
    private static TextView showregion;
    private static ImageView showimage;
    private String desc;
    private String date;
    private String time;
    private Bitmap bmp;
    private String region;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_item);
        Intent intent = getIntent();
        desc = intent.getStringExtra("place");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        region = intent.getStringExtra("region");
        bmp = null;
        try {
            bmp = intent.getParcelableExtra("image");
        } catch (NullPointerException e) {
            Log.e(TAG, "bytearray is null", e);
        }

        showimage = (ImageView) findViewById(R.id.show_image);
        showtime = (TextView) findViewById(R.id.show_time);
        showdate = (TextView) findViewById(R.id.show_date);
        showdesc = (TextView) findViewById(R.id.show_desc);
        showregion = (TextView) findViewById(R.id.show_region);

        showimage.setImageBitmap(bmp);
        showdate.setText(date);
        showtime.setText(time);
        showdesc.setText(desc);
        showregion.setText("Region: " + region);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Intent intent = new Intent();
                intent.putExtra("image", bmp);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("place", desc);
                intent.putExtra("region", region);
                Toast.makeText(getApplicationContext(), "Deleting item....!", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return true;
    }
}
