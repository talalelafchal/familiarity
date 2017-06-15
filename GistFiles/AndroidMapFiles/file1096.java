package com.example.cirugias;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class bd extends SQLiteOpenHelper {

	public bd(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
		arg0.execSQL("create table cirugia(idCirugia integer primary key autoincrement not null,nombre String,nombre_C String,obra_Social String,dia_C integer,mes_C integer,an_C integer,lugar String,diaC integer,mesC,anC integer,monto float)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
