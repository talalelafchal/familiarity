package com.gmail.fedorenko.kostia.app1lesson4;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kfedoren on 22.09.2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "testtable";

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL create table
        String CREATE_ITEM_TABLE = "CREATE TABLE " + Item.TABLE_NAME + " ( " +
                Item.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Item.KEY_PLACE + " TEXT, " +
                Item.KEY_TIME + " TEXT, " +
                Item.KEY_DATE + " TEXT, " +
                Item.KEY_IMAGE + " BLOB, " +
                Item.KEY_REGION + " TEXT )";

        //create table
        db.execSQL(CREATE_ITEM_TABLE);
    }

    public void addItem(Item item) {
        //get writeable DB
        SQLiteDatabase db = this.getWritableDatabase();
        //create ContentValues to add
        ContentValues values = new ContentValues();
        values.put(item.KEY_PLACE, item.getPlace());
        values.put(item.KEY_TIME, item.getTime());
        values.put(item.KEY_DATE, item.getDate());
        values.put(item.KEY_IMAGE, Util.bitmapToByteArray(item.getImage()));
        values.put(item.KEY_REGION, item.getRegion());
        //Insert
        db.insert(item.TABLE_NAME, null, values);
        //close
        db.close();
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        String query = "SELECT * FROM " + Item.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Item item = null;
        if (cursor.moveToFirst()) {
            do {
                item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setPlace(cursor.getString(1));
                item.setTime(cursor.getString(2));
                item.setDate(cursor.getString(3));
                item.setImage(Util.byteArrayToBitmap(cursor.getBlob(4)));
                item.setRegion(cursor.getString(5));
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    public int updateAd(Item item) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(item.KEY_PLACE, item.getPlace());
        values.put(item.KEY_TIME, item.getTime());
        values.put(item.KEY_DATE, item.getDate());
        values.put(item.KEY_IMAGE, Util.bitmapToByteArray(item.getImage()));
        values.put(item.KEY_REGION, item.getRegion());

        // 3. updating row
        int i = db.update(item.TABLE_NAME, values, item.KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});

        // 4. close
        db.close();
        return i;
    }

    public void deleteItem(Item item) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(item.TABLE_NAME, item.KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});

        // 3. close
        db.close();
        Log.d("deleteItem(" + item.getId() + ")", item.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS items");
        // create fresh table
        this.onCreate(db);
    }

    public Item getItem(int id) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(Item.TABLE_NAME, // a. table
                Item.COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by - how to group rows
                null, // f. having - which row groups to include (filter)
                null, // g. order by
                null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build ad object
        Item item = new Item();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setPlace(cursor.getString(1));
        item.setTime(cursor.getString(2));
        item.setDate(cursor.getString(3));
        item.setImage(Util.byteArrayToBitmap(cursor.getBlob(4)));
        item.setRegion(cursor.getString(5));

        Log.d("getItem(" + id + ")", item.toString());
        return item;
    }

    public Item getItemByDesc(String desc) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(Item.TABLE_NAME, // a. table
                Item.COLUMNS, // b. column names
                " place = ?", // c. selections
                new String[]{desc}, // d. selections args
                null, // e. group by - how to group rows
                null, // f. having - which row groups to include (filter)
                null, // g. order by
                null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build ad object
        Item item = new Item();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setPlace(cursor.getString(1));
        item.setTime(cursor.getString(2));
        item.setDate(cursor.getString(3));
        item.setImage(Util.byteArrayToBitmap(cursor.getBlob(4)));
        item.setRegion(cursor.getString(5));

        Log.d("getItem(" + desc + ")", item.toString());
        return item;
    }

}
