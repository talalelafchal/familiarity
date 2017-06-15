package com.google.android.gms.car;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CarServiceDataStorage
  extends SQLiteOpenHelper
{
  private static volatile CarServiceDataStorage a;
  
  private CarServiceDataStorage(Context paramContext)
  {
    super(paramContext, "carservicedata.db", null, 9);
  }
  
  static CarInfoInternal a(Context paramContext, CarInfoInternal paramCarInfoInternal, boolean paramBoolean)
  {
    return a(paramContext).b(paramCarInfoInternal, paramBoolean);
  }
  
  private CarInfoInternal a(Cursor paramCursor)
  {
    CarInfoInternal localCarInfoInternal = new CarInfoInternal(paramCursor.getString(paramCursor.getColumnIndex("manufacturer")), paramCursor.getString(paramCursor.getColumnIndex("model")), paramCursor.getString(paramCursor.getColumnIndex("modelyear")), paramCursor.getString(paramCursor.getColumnIndex("vehicleid")), paramCursor.getInt(paramCursor.getColumnIndex("headUnitProtocolMajorVersionNumber")), paramCursor.getInt(paramCursor.getColumnIndex("headUnitProtocolMinorVersionNumber")));
    localCarInfoInternal.t = paramCursor.getLong(paramCursor.getColumnIndex("id"));
    localCarInfoInternal.e = paramCursor.getString(paramCursor.getColumnIndex("vehicleidclient"));
    if (paramCursor.getInt(paramCursor.getColumnIndex("bluetoothConnectionAllowed")) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      localCarInfoInternal.s = bool;
      localCarInfoInternal.u = paramCursor.getLong(paramCursor.getColumnIndex("connectiontime"));
      localCarInfoInternal.q = paramCursor.getString(paramCursor.getColumnIndex("nickname"));
      return localCarInfoInternal;
    }
  }