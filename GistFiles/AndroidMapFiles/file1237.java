package com.gmail.fedorenko.kostia.app1lesson4;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_CODE_NEW_ITEM = 1;
    private static final int REQUEST_CODE_SHOW_ITEM = 4;
    private static final String TAG = "MainActivity";

    private ListView list;
    private ArrayList<Item> items = new ArrayList<>();
    private ListAdapter adapter;
    private MySQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MySQLiteHelper(this);
        items = db.getAllItems();
        list = (ListView) findViewById(R.id.list_view);
        useCustomAdapter();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) list.getAdapter().getItem(position);
                Intent i = new Intent(MainActivity.this, ShowItemActivity.class);
                i.putExtra("place", item.getPlace());
                i.putExtra("time", item.getTime());
                i.putExtra("date", item.getDate());
                i.putExtra("image", item.getImage());
                i.putExtra("region", item.getRegion());
                startActivityForResult(i, REQUEST_CODE_SHOW_ITEM);
            }
        });
    }

    private void useCustomAdapter(){
        adapter = new ItemAdapter(this, items);
        list.setAdapter(adapter);
    }

    private void useSimpleAdapter(){
        ArrayList<HashMap<String, Object>> listOfItems = new ArrayList<>();

        HashMap<String,Object> map;
        for (int i = 0; i<items.size(); i++){
            map = new HashMap<>();
            map.put("place", items.get(i).getPlace());
            map.put("date_and_time", "At: " + items.get(i).getTime() + "; On: " + items.get(i).getDate());
            listOfItems.add(map);
        }

        String[] from = {"place","date_and_time"};
        int[] to = {R.id.place, R.id.date_time};

        adapter = new SimpleAdapter(this, listOfItems, R.layout.item_list, from, to);
        list.setAdapter(adapter);
    }

    private void useCursorAdapter(){
        Cursor cursor = db.getWritableDatabase().rawQuery("SELECT rowid _id, * FROM " +
                Item.TABLE_NAME, null);

        String[] columns = new String[]{Item.KEY_PLACE, Item.KEY_DATE,};
        int[] to = new int[]{R.id.place, R.id.date_time};

        adapter = new SimpleCursorAdapter(this, R.layout.item_list, cursor, columns, to);

        list.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent i = new Intent(MainActivity.this, AddNewItemActivity.class);
                startActivityForResult(i, REQUEST_CODE_NEW_ITEM);
                break;
            case R.id.custom_adapter:
                useCustomAdapter();
                Toast.makeText(getApplicationContext(),"Using custom adapter...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cursor_adapter:
                useCursorAdapter();
                Toast.makeText(getApplicationContext(),"Using cursor adapter...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.simple_adapter:
                useSimpleAdapter();
                Toast.makeText(getApplicationContext(),"Using simple adapter...",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_NEW_ITEM && resultCode == RESULT_OK) {
                String place = data.getStringExtra("place");
                String date = data.getStringExtra("date");
                String time = data.getStringExtra("time");
                String region = data.getStringExtra("region");
                Bitmap bmp = null;
                try {
                    bmp = data.getParcelableExtra("image");
                } catch (NullPointerException e) {
                    Log.e(TAG, "bytearray is null", e);
                }

                Item item = new Item(place, time, date, bmp, region);
                db.addItem(item);
                items = db.getAllItems();
                useCustomAdapter();
            }
        else if (requestCode == REQUEST_CODE_SHOW_ITEM && resultCode == RESULT_OK) {
                String place = data.getStringExtra("place");
                Item item = db.getItemByDesc(place);
                db.deleteItem(item);
                items = db.getAllItems();
                useCustomAdapter();
        }
    }
}
