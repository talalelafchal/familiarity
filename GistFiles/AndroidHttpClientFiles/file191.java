package com.example.joona.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WinNabuska on 6.8.2015.
 */
public class MovieDataAdapter {

    private SQLiteDatabase db;
    private Context context;
    private MySQLiteHelper dbHelper;

    public static final String DATABASE_NAME = "movies_database";
    public static final String TABLE_NAME = "watched_movies";
    public static final String ID = "_id";
    public static final String TITLE = "Title";
    public final static String YEAR = "Year";
    public final static String WATCHDATE = "WatchDate";
    public final static String WATCHLOCATION = "WatchLocation";
    public final static String GENRE = "Genre";
    public final static String DIRECTOR = "Director";
    public final static String ACTOR = "Actors";
    public final static String LANGUAGE = "Language";
    public final static String COUNTRY = "Country";
    public final static String POSTER = "Poster";

    protected MovieDataAdapter(Context context){
        Log.i("info", "in database contructor");
        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    /*Class SQLiteOpenHelper*/

    protected class MySQLiteHelper extends SQLiteOpenHelper {

        private static final String TABLE_CONSTUCTOR =
                "create table if not exists "+TABLE_NAME+
                        " ("+ID     +" integer primary key autoincrement, " +
                        TITLE       +" varchar(50) NOT NULL, "+
                        YEAR        +" varchar(4) NOT NULL, "+
                        //WATCHDATE   + TODO add date
                        //WATCHLOCATION +"//TODO add location+
                        GENRE       +" varchar(250) null, "+
                        DIRECTOR    +" varchar(50) null, "+
                        ACTOR       +" varchar(250) null, "+
                        LANGUAGE    +" varchar(50), "+
                        COUNTRY     +" varchar(50), "+
                        POSTER      +" varchar(200));";

        public MySQLiteHelper(Context context){
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CONSTUCTOR);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }

    /*End of SQLiteOpenHelper*/

    protected void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            db = dbHelper.getReadableDatabase();
            Log.i("info", "COULD NOT OPEN WRITEBLEDATABASE, READABLE DB -->");
        }
        db.execSQL(MySQLiteHelper.TABLE_CONSTUCTOR);
/*
        if(countRows()==0){
            ContentValues newRow = new ContentValues();
            newRow.put(TITLE, "Mad Max - Fury Road");
            newRow.put(YEAR, 2015);
            db.insert(TABLE_NAME, null, newRow);

            newRow = new ContentValues();
            newRow.put(TITLE, "BirdMan");
            newRow.put(YEAR, 2015);
            db.insert(TABLE_NAME, null, newRow);
        }*/
    }

    protected ArrayList<HashMap<String, String>> getAllContent(){
        ArrayList<HashMap<String, String>> content = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if(cursor!=null && cursor.moveToNext()){
            do{
                HashMap<String, String> current = new HashMap<>();
                current.put(TITLE, cursor.getString(cursor.getColumnIndex(TITLE)));
                current.put(YEAR, cursor.getString(cursor.getColumnIndex(YEAR)));
                current.put(GENRE, cursor.getString(cursor.getColumnIndex(GENRE)));
                current.put(DIRECTOR, cursor.getString(cursor.getColumnIndex(DIRECTOR)));
                current.put(ACTOR, cursor.getString(cursor.getColumnIndex(ACTOR)));
                current.put(LANGUAGE, cursor.getString(cursor.getColumnIndex(LANGUAGE)));
                current.put(POSTER, cursor.getString(cursor.getColumnIndex(POSTER)));
                content.add(current);
            }while(cursor.moveToNext());
        }
        return content;
    }

    protected void deleteEntry(HashMap<String, String> movieData){
        db.delete(TABLE_NAME, TITLE+" = " + movieData.get(TITLE).trim() +" AND " + YEAR + " = " + movieData.get(YEAR).trim(), null);
    }

    protected ArrayList<HashMap<String, String>> findMatchingSeenMovies(HashMap<String, String>movieData){
        ArrayList<HashMap<String, String>> mathingContent = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        if(cursor!=null && cursor.moveToNext()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(TITLE)).matches(movieData.get(TITLE).trim())) {
                    HashMap<String, String> current = new HashMap<>();
                    current.put(TITLE, cursor.getString(cursor.getColumnIndex(TITLE)));
                    current.put(YEAR, cursor.getString(cursor.getColumnIndex(YEAR)));
                    current.put(GENRE, cursor.getString(cursor.getColumnIndex(GENRE)));
                    current.put(DIRECTOR, cursor.getString(cursor.getColumnIndex(DIRECTOR)));
                    current.put(ACTOR, cursor.getString(cursor.getColumnIndex(ACTOR)));
                    current.put(LANGUAGE, cursor.getString(cursor.getColumnIndex(LANGUAGE)));
                    current.put(POSTER, cursor.getString(cursor.getColumnIndex(POSTER)));
                    mathingContent.add(current);
                }
            } while (cursor.moveToNext());
        }
        return mathingContent;
    }

    protected boolean matchesOldSeenMovie(HashMap<String, String> movieData){
        Log.i("info", "start if..");
        return 0 < db.query(
                TABLE_NAME,
                null,
                TITLE + " = '" + movieData.get(TITLE).trim() + "' AND " + YEAR + " = '" + movieData.get(YEAR).trim() + "'",
                null,
                null,
                null,
                null).getCount();
    }

    protected boolean insertEntry(HashMap<String, String> movieInfo){
        Log.i("info", "check inf statement");
        if(matchesOldSeenMovie(movieInfo)) {
            Log.i("info", "matching. FAIL");
            return false;
        }
        else {
            Log.i("info", "preparing insert");
            ContentValues newRow = new ContentValues();
            newRow.put(TITLE, movieInfo.get(TITLE));
            newRow.put(YEAR, movieInfo.get(YEAR));
            newRow.put(POSTER, movieInfo.get(POSTER));
            Log.i("info", "ready for insert");
            db.insert(TABLE_NAME, null, newRow);
            Log.i("info", "insert done. SUCCESS");
            return true;
        }
    }

    protected void close(){
        db.close();
    }

    protected int countRows(){
        return db.query(TABLE_NAME, null, null, null, null, null, null).getCount();
    }

}
