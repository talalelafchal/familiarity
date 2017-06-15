package com.example.fm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class AnandDB 
{
	dbhelper helper;
	SQLiteDatabase db;
	Context context;
	Cursor cur;
	public static final String Db_Name="TravelDB";
    public static final String T_login="login";
    public static final String T_Registration="register";

    public static final String T_vechicleadv="vechicleadv";
	public static final String T_Driveradv="Driveradv";
    public static final String T_ManageVehicle="Vehicle";
    public static final String T_ManageDriver="Driver";

    public static final String KEY_Date="Date";
	public static final String KEY_Vechicle_no="Vechicle_no";
	public static final String KEY_Driver_name="Driver_name";
	public static final String KEY_Cleaner_name="Cleaner_name";
	public static final String KEY_Voucher_no="Voucher_no";
	public static final String KEY_Amount="Amount";

    public static final String KEY_Reg_Name="UserName";
    public static final String KEY_Email="Email";
    public static final String KEY_Phone="Phone";
    public static final String KEY_Pin="Pin";

    public static final String KEY_Vehicle_No="VehNo";
    public static final String KEY_Vechicle_Type="Vechicle_type";
    public static final String KEY_Veh_Insu="Veh_Insu";
    public static final String KEY_Veh_Permit="Permit";
    public static final String KEY_Veh_Explo="Explosive";
    public static final String KEY_Veh_PC="PC";
    public static final String  KEY_Veh_Tax="Veh_Tax";

    public static final String KEY_Driver_Name="DriverName";
    public static final String KEY_Driver_Type="DesignationType";
    public static final String KEY_Driver_Phone="PhoneNo";
    public static final String KEY_Driver_LicenseNo="LicenseNo";
    public static final String KEY_Driver_LicenseEnd="LicenseEndDate";
    public static final String KEY_Driver_Insurance="InsuranceEndDate";
   public static final String KEY_Driver_Address="Address";

	public AnandDB(Context con)
	{
		context=con;		helper=new dbhelper(con, Db_Name, null,1 );
	}
	public void open() {
		// TODO Auto-generated method stub
		db=helper.getWritableDatabase();
	}
	public void  Insertointotable(ContentValues cv,String table) {
		db.insert(table, null, cv);
	}
	public Cursor  Queryallrow(String table) 
	{		
		return db.query(table, null, null, null, null, null, null);
	}

//    public Cursor getContact(String Pin)
//    {
////Cursor mCursor=db.query(true,T_Registration,new String[] {KEY_Reg_Name,KEY_Email ,KEY_Phone ,KEY_Pin },KEY_Pin,"="+Pin,null,null,null,null,n  );
////  //      Cursor mCursor =  db.query(true, T_Registration, new String[] {KEY_Reg_Name,KEY_Email ,KEY_Phone ,KEY_Pin }, KEY_Pin + “=” +  Pin, null,null, null, null, null);
////        if (mCursor != null) {
////            mCursor.moveToFirst();
////        }
////        return mCursor;
//    }
	/*public Cursor quary()
	{
		Cursor c=db.rawQuery("select Driver_name from vechicleadv", null) ;
		return c;
		
	}*/
//	public List<String> quary()
//	{
//		List<String> list=new ArrayList<String>();
//		  cur=db.rawQuery("select * from vechicleadv", null) ;
//		 if (cur.moveToFirst()) {
//			 list.add("Driver list");
//	            do {
//
//	            	list.add(cur.getString(3));
//	            } while (cur.moveToNext());
//	        }
//		return list;
//	}
//	public List<String> quaryvechicleno()
//	{
//		List<String> list=new ArrayList<String>();
//		  cur=db.rawQuery("select * from vechicleadv", null) ;
//		 if (cur.moveToFirst()) {
//	            do {
//	            	list.add(cur.getString(2));
//	            } while (cur.moveToNext());
//	        }
//		return list;
//	}
	public class dbhelper extends SQLiteOpenHelper
	{

		public dbhelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase sdb) {
			// TODO Auto-generated method stub
            String S_Register="Create table "+T_Registration+"(_id integer primary key,KEY_Reg_Name text,KEY_Email text,KEY_Phone text,KEY_Pin text) ";
            String S_Login="Create table "+T_login+"(_id integer primary key,pin text) ";
			String S_vechicleadv="Create table "+T_vechicleadv+"(_id integer primary key,KEY_Date text,KEY_Vechicle_no text,KEY_Driver_name text,KEY_Cleaner_name text,KEY_Voucher_no text,KEY_Amount text) "; 
			String S_Driveradv="Create table "+T_Driveradv+"(_id integer primary key,Date text,Vechicle_no text,Driver_name text,Voucher_no text,Amount text) ";
            String S_ManageDrivers="Create table "+T_ManageDriver+"(_id integer primary key,KEY_Driver_Name text,KEY_Driver_Type text,KEY_Phone text,KEY_Address text,KEY_LicenseNo text,KEY_LicenseEnd text,KEY_InsuranceEnd text) ";
            String S_ManageVehicle="Create table "+T_ManageVehicle+"(_id integer primary key,KEY_Vehicle_No text,KEY_Vechicle_Type text,KEY_Veh_Insu text,KEY_Veh_Permit text,KEY_Veh_Explo text,KEY_Veh_PC text,KEY_Veh_Tax text)";

            sdb.execSQL(S_Register);
            sdb.execSQL(S_Login);
			sdb.execSQL(S_Driveradv);
			sdb.execSQL(S_vechicleadv);
            sdb.execSQL(S_ManageDrivers);
            sdb.execSQL(S_ManageVehicle);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		}
}

