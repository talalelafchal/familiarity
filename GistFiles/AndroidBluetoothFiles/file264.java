package com.healthiot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Brain on 19/01/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;

    private static String DATABASE_NAME = "HealthIOT";

    private static String sql = "";

    private static String patient_details = "CREATE TABLE patient_details (op_no INTEGER PRIMARY KEY, name TEXT, address TEXT, contact TEXT, lastconsult DATE, age TEXT, bldgrp TEXT, dob DATE, report TEXT, doc_name TEXT, disease TEXT, aadhar TEXT)";

    private static String sensor_details = "CREATE TABLE sensor_details (ecg TEXT, temp TEXT,hb TEXT)";
    private static String login_details = "CREATE TABLE login_details (user TEXT, pass TEXT)";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sensor_details);
        db.execSQL(patient_details);
        db.execSQL(login_details);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS patient_details");
        db.execSQL("DROP TABLE IF EXISTS sensor_details");
        db.execSQL("DROP TABLE IF EXISTS login_details");
        onCreate(db);
    }

    //insert patient_details

    public long insert_patient(int op, String name, String address, String contact,String lastconsult, String age, String bldgrp, String dob, String report, String doc_name, String disease, String aadhar)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("op_no",op);
        values.put("name",name);
        values.put("address",address);
        values.put("contact",contact);
        values.put("lastconsult",lastconsult);
        values.put("age",age);
        values.put("bldgrp",bldgrp);
        values.put("dob",dob);
        values.put("report",report);
        values.put("doc_name",doc_name);
        values.put("disease",disease);
        values.put("aadhar",aadhar);

        long l = db.insert("patient_details", null, values);
        db.close();

        return l;

    }

    //check patient details

    public boolean check_patient()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from patient_details";
        boolean b = false;
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                b = true;
                c.moveToNext();
            }
        }
        db.close();
        return b;
    }

    //get patient details

    public ArrayList<String> get_patient_details()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from patient_details ";
        ArrayList<String> al = new ArrayList<String>();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                String name = c.getString(c.getColumnIndex("name"));
                String address = c.getString(c.getColumnIndex("address"));
                String age = c.getString(c.getColumnIndex("age"));
                String contact = c.getString(c.getColumnIndex("contact"));
                String aadhar = c.getString(c.getColumnIndex("aadhar"));
                al.add(name);
                al.add(address);
                al.add(age);
                al.add(contact);
                al.add(aadhar);
                c.moveToNext();
            }
        }
        db.close();
        return al;
    }

    //get patient medical details

    public ArrayList<String> get_patient_medical_details()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from patient_details ";
        ArrayList<String> al = new ArrayList<String>();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                String op = c.getInt(c.getColumnIndex("op_no"))+"";
                String doc = c.getString(c.getColumnIndex("doc_name"));
                String blood = c.getString(c.getColumnIndex("bldgrp"));
                String disease = c.getString(c.getColumnIndex("disease"));
                String report = c.getString(c.getColumnIndex("report"));
                String consult = c.getString(c.getColumnIndex("lastconsult"));
                al.add(op);
                al.add(doc);
                al.add(blood);
                al.add(disease);
                al.add(report);
                al.add(consult);
                c.moveToNext();
            }
        }
        db.close();
        return al;
    }



    //insert login details

    public long login_details(String user, String pass)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user",user);
        values.put("pass",pass);

        long l = db.insert("login_details", null, values);
        db.close();

        return l;

    }

    //check login

    public boolean check_login(String user,String pass)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from login_details where user = '"+user+"' and pass = '"+pass+"'";
        boolean b = false;
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                b=true;
                c.moveToNext();
            }
        }
        db.close();
        return b;
    }

    //check login_details for value

    public boolean check_login_available()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from login_details";
        boolean b = false;
        Cursor c = db.rawQuery(sql,null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                b=true;
                c.moveToNext();
            }
        }
        db.close();
        return b;
    }

    //Insert sensor details

    public long insert_sensors(String ecg, String temp, String hb)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ecg",ecg);
        values.put("temp",temp);
        values.put("hb",hb);


        long l = db.insert("sensor_details", null, values);
        db.close();

        return l;

    }

    //get sensor details

    public ArrayList<String> get_sensor_details()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from sensor_details ";
        ArrayList<String> al = new ArrayList<String>();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            while(!c.isAfterLast())
            {
                String ecg = c.getString(c.getColumnIndex("ecg"));
                String temp = c.getString(c.getColumnIndex("temp"));
                String hb = c.getString(c.getColumnIndex("hb"));

                al.add(ecg);
                al.add(temp);
                al.add(hb);

                c.moveToNext();
            }
        }
        db.close();
        return al;
    }



}
