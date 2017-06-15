package com.example.tasneem.googleplacesapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

/**
 * Created by Tasneem on 13/08/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    
    public DBHelper(Context context) {
        super(context, "GoogleplacesAPIdb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Places(category text, name text, lat text, lng text, dis text, vicinity text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String selCategory, String name, double lat, double lng, String vicinity) {
        SQLiteDatabase db=getWritableDatabase();
        float dis=distance(lat, lng);
        String str="insert into Places values('"+selCategory+"','"+name+"','"+lat+"','"+lng+"','"+dis+"','"+vicinity+"')";
        //db.execSQL(str);
        ContentValues values=new ContentValues();
        values.put("category",selCategory);
        values.put("name",name);
        values.put("lat",lat);
        values.put("lng",lng);
        values.put("dis",dis);
        values.put("vicinity",vicinity);
        db.insert("Places",null,values);
    }

    public Cursor getData(String sel_category) {
        SQLiteDatabase db=getReadableDatabase();
        Cursor data=db.rawQuery("select * from Places where category='"+sel_category+"' order by dis",null);
        return data;
    }

    public void delete(String sel_category) {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("delete from Places where category='"+sel_category+"'");

    }

    public float distance(double lat,double lng)
    {
        float[] results = new float[1];
        Location.distanceBetween(MapsActivity.latitude, MapsActivity.longitude, lat, lng, results);
        results[0]=results[0]/1000;
        return results[0];

    }

}