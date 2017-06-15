package com.projet.consulting.lttd.m3appli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by APACE on 04/07/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "LTTD";

    // Articles table name
    private static final String TABLE_ARTICLE = "article";

    // Articles Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATE = "create_article";
    private static final String KEY_UPDATE = "update_article";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLES_TABLE = "CREATE TABLE " + TABLE_ARTICLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PRICE + " DOUBLE,"
                + KEY_USER + " TEXT," + KEY_CREATE + " DATETIME," + KEY_UPDATE + " DATETIME" +")";
        db.execSQL(CREATE_ARTICLES_TABLE);
        Log.d(CREATE_ARTICLES_TABLE, "table article Created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);

        // Create tables again
        onCreate(db);
        Log.d("Tables", " Updated");
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new Article
    void addArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, article.getName());
        values.put(KEY_PRICE, article.getPrice());
        values.put(KEY_USER, article.getUser());
        values.put(KEY_CREATE, convertDate(article.getCreate()));
        // values.put(KEY_UPDATE, article.getUpdate().toString());

        // Inserting Row
        db.insert(TABLE_ARTICLE, null, values);
        db.close();
    }

    // Getting single Article
    Article getArticle(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ARTICLE, new String[] { KEY_ID,
                        KEY_NAME, KEY_PRICE,KEY_USER,KEY_CREATE,KEY_UPDATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mi:ss");
             Article article = new Article(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),Double.parseDouble(cursor.getString(2)),cursor.getString(3), convertDate(cursor.getString(4)),convertDate(cursor.getString(5)));
            return article;
    }

    // Getting All Articles
    public List<Article> getAllArticles() {
        List<Article> articleList = new ArrayList<Article>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ARTICLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Article article = new Article();
                article.setId(Integer.parseInt(cursor.getString(0)));
                article.setName(cursor.getString(1));
                article.setPrice(Double.parseDouble(cursor.getString(2)));
                article.setUser(cursor.getString(3));
                article.setCreate(convertDate(cursor.getString(4)));
                article.setUpdate(convertDate(cursor.getString(5)));
                // Adding Article to list
                articleList.add(article);
            } while (cursor.moveToNext());
        }

        // return Article list
        return articleList;
    }

    // Updating single Article
    public int updateArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, article.getName());
        values.put(KEY_USER, article.getUser());
        values.put(KEY_PRICE, article.getPrice());
        //values.put(KEY_CREATE, article.getCreate().toString());
        values.put(KEY_UPDATE, convertDate(article.getUpdate()));
        // updating row
        return db.update(TABLE_ARTICLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(article.getId()) });
    }

    // Deleting single Article
    public void deleteArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTICLE, KEY_ID + " = ?",
                new String[] { String.valueOf(article.getId()) });
        db.close();
    }


    // Getting Articles Count
    public int getArticlesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ARTICLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    private Date convertDate(String date) {
        Date d = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            d = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    private String convertDate(Date date) {
        String value="";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            value = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
