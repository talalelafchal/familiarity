package com.PracticaSQLite.agenda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eder on 29/06/13.
 */
public class TodoSQLiteHelper extends SQLiteOpenHelper {
    public TodoSQLiteHelper(Context context){
        super(context, "todos_db", null, 1);
    }

    //creamos nuestra base de datos a travez de sentencias SQL
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE todos (_id INTEGER PRIMARY KEY AUTOINCREMENT, todo TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        db.execSQL("DROP TABLE IF EXISTS todos");
        onCreate(db);
    }
}
