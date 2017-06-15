package com.example.myfirstapp;

import com.example.myfirstapp.DatabaseTable.DbHelper;

import android.net.Uri;
import android.util.Log;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class MyContactProvider extends ContentProvider {
	public static String AUTHORITY = "com.example.myfirstapp.SomeProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/contactsTable");
	private DbHelper helper;
	private static final int CONTACTS = 0;
	private static final int CONTACTS_ID = 1;
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		        sUriMatcher.addURI(AUTHORITY, DatabaseTable.DATABASE_NAME, CONTACTS);
		        sUriMatcher.addURI(AUTHORITY, DatabaseTable.DATABASE_NAME + "/#", CONTACTS_ID);

	}
	@Override
	  public boolean onCreate() {
	    helper = new DbHelper(getContext());
	    return false;
	  }

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder sq = new SQLiteQueryBuilder();
		sq.setTables(DatabaseTable.DATABASE_TABLE);
		int uriType = sUriMatcher.match(uri);
		Log.d("Uri", "" + uriType);
		switch (uriType) {
			case CONTACTS:
		            break;
		    case CONTACTS_ID:
		    	sq.appendWhere(DatabaseTable.COL_ID + " = "
		    	          + uri.getLastPathSegment());
		    	break;
		    case -1:
		            throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase data = helper.getWritableDatabase();
	    Cursor cursor = sq.query(data, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    Log.d("cursor" , "" + cursor);
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
}
