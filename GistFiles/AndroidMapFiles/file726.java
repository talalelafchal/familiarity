package com.ozateck.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.util.Log;

public class SQLiteOpenHelperLocal extends SQLiteOpenHelper{

	private static final String TAG = "SQLiteOpenHelperLocal";

	// データベースパスとファイル名
	public static String DB_PATH;
	public static String DB_NAME;

	private Context context;
	private SQLiteDatabase db;

	public SQLiteOpenHelperLocal(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		
		// ファイルのパス
		DB_PATH = context.getFilesDir().getPath();
		DB_NAME = "master.sqlite";

		// DBが存在しない場合にシステムパス上にDBを作成
		if(isExists()){
			Log.d(TAG, "MySQLiteOpenHelper isExists:true");
			//updateDB();// テスト時に利用(強制リフレッシュ)
		}else{
			Log.d(TAG, "MySQLiteOpenHelper isExists:false");
			updateDB();// DBを作成
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		Log.d(TAG, "MySQLiteOpenHelper onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.d(TAG, "MySQLiteOpenHelper onUpgrade");
	}

	@Override
	public synchronized void close(){
		super.close();
		Log.d(TAG, "MySQLiteOpenHelper close");
		if(db != null)
			db.close();
	}
	
	// DBファイルの初期化
	public void updateDB(){
		//アプリのデフォルトシステムパスに作られる
		this.getReadableDatabase();
		//assetに格納したデータベースをコピーする
		copyDatabaseFromAsset();
	}

	// DBの存在を確認する
	public boolean isExists(){
		boolean exist = false;
		String dbPath = DB_PATH + DB_NAME;
		
		File file = new File(dbPath);
		if(file.exists()){
			try{
				db = SQLiteDatabase.openDatabase(dbPath, null,
						SQLiteDatabase.OPEN_READONLY);
				if(db != null)
					db.close();
				exist = true;
			}catch(SQLiteException e){
				Log.d(TAG, "SQLE:" + e.toString());
				exist = false;
			}
		}else{
			exist = false;
		}
		
		return exist;
	}
	
	// assetに格納したDBをデフォルトのDBパスに作成し、コピーする
	private void copyDatabaseFromAsset(){
		try{
			
			// DBを格納するディレクトリを自作
			File databaseDir = new File(DB_PATH);
			if (!databaseDir.exists()) databaseDir.mkdirs();
			
			// asset内のDBにアクセス
			InputStream is = context.getAssets().open(DB_NAME);
			
			// デフォルトのDBパスに作成した空のDB
			String dbPath = DB_PATH + DB_NAME;
			OutputStream os = new FileOutputStream(dbPath);
			
			// コピー
			byte[] buffer = new byte[1024];
			int size;
			while((size = is.read(buffer)) > 0){
				os.write(buffer, 0, size);
			}
			
			// クローズ
			is.close();
			os.flush();
			os.close();
			Log.d(TAG,"DB Copy:success");
		}catch(IOException e){
			Log.e(TAG, "IOE:" + e.toString());
			Log.d(TAG, "DB Copy:failed");
		}
	}
	
	// DBを開く
	public SQLiteDatabase openDataBase(){
		try{
			String dbPath = DB_PATH + DB_NAME;
			db = SQLiteDatabase.openDatabase(
					dbPath, null, SQLiteDatabase.OPEN_READWRITE);
		}catch(SQLiteException e){
			Log.d(TAG, "SQLE:" + e.toString());
		}
		return db;
	}
}
