package com.bgstation0.android.application.zinc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bg1 on 2016/10/24.
 */

public class UrlListDBHelper extends SQLiteOpenHelper {

    // メンバフィールドの定義
    private final static String DB = "urllist.db";  // DB名"urllist.db"
    private final static int DB_VERSION = 1;    // バージョン1
    private final static String TABLE_BOOKMARK = "bookmark";    // bookmarkテーブル
    private final static String CREATE_TABLE_BOOKMARK = "create table " + TABLE_BOOKMARK + "( _id integer primary key autoincrement, datemillisec long, title string, url string);"; // bookmarkテーブルの作成.
    private final static String DROP_TABLE_BOOKMARK = "drop table " + TABLE_BOOKMARK + ";"; // bookmarkテーブルの削除.
    private SQLiteDatabase sqlite = null;   // SQLiteDatabase型sqliteをnullにセット.

    // コンストラクタ
    public UrlListDBHelper(Context context){
        super(context, DB, null, DB_VERSION);   // 親コンストラクタに任せる.
    }

    // DB作成時
    public void onCreate(SQLiteDatabase db){
        // テーブル作成
        try{
            db.execSQL(CREATE_TABLE_BOOKMARK);  // db.execSQLでCREATE_TABLE_BOOKMARKを実行.
        }
        catch (Exception ex){
            Log.e("Zinc", ex.toString());	// ex.toStringをLogに出力.
        }
    }

    // DBバージョン更新時
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // テーブル再作成.
        db.execSQL(DROP_TABLE_BOOKMARK );	// いったんbookmarkテーブルをドロップ.
        onCreate(db);	// 再作成.
    }

    // DBにURLを追加.
    public long addUrl(String title, String url){
        // 変数の宣言
        long id = -1; // insert時に返ってくるIDを格納するlong型idを-1にセット.
        // DBへの追加.
        try{
            // sqliteの取得.
            if (sqlite == null) {
                sqlite = getWritableDatabase(); // getWritableDatabaseでsqliteを取得.
            }
            ContentValues values = new ContentValues(); // テーブルに挿入する値の箱ContentValuesを用意.
            values.put("datemillisec", 0);  // valuesにキー"datemillisec", 値0を登録.
            values.put("title", title);    // valuesにキー"title", 値titleを登録.
            values.put("url", url); // valuesにキー"url", 値urlを登録.
            id = sqlite.insertOrThrow(TABLE_BOOKMARK, null, values); // sqlite.insertOrThrowで"bookmark"テーブルにvaluesをinsert.
        }
        catch (Exception ex){
            Log.e("Zinc", ex.toString());	// ex.toStringをLogに出力.
        }
        finally {
            // sqliteを閉じる.
            if (sqlite != null) {
                sqlite.close(); // sqlite.closeで閉じる.
                sqlite = null;  // sqliteにnullをセット.
            }
            return id;
        }
    }
}