package com.amtera.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amtera.domain.User;

import java.util.ArrayList;


public class UserDAO{
	private ConnectionHelper con;
	private SQLiteDatabase db;
	private ContentValues values;

	public UserDAO(Context context){
		con = new ConnectionHelper(context);
        getDb();
	}

	private SQLiteDatabase getDb(){
		if (db == null){
			db = con.getWritableDatabase();
		}
		return db;
	}

	public void closeConnection(){
		con.close();
	}

	public synchronized boolean add(User user){
        values = new ContentValues();
    	values.put("login", user.getLogin());
    	values.put("password", user.getPassword());

    	try{
    		if (db.insert("user", null, values) != -1){
    			return true;
    		}
    		else{
    			return false;
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	}

    public synchronized ArrayList<User> listAllUser(){
        Cursor cursor = getDb().rawQuery("SELECT*FROM user", null);
        ArrayList<User> users = new ArrayList<User>();

        while (cursor.moveToNext()) {
            User user = new User();
            user.setLogin(cursor.getString(0));
            user.setPassword(cursor.getString(1));
            users.add(user);

        }
        System.out.println("1");
        return users;
    }

    public synchronized boolean loginCheck(User user){
        Cursor cursor = getDb().rawQuery("SELECT login, password FROM user WHERE login=? AND password=?", new String[] {user.getLogin(), user.getPassword()});
        return cursor.getCount() != 0;
    }
}