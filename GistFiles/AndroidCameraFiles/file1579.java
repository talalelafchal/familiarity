package com.opennotifier.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.opennotifier.app.R;

/**
 * Created by Administrator on 2016/9/22.
 */
public class Contact{
    public Context context;

    public Contact(Context context) {
        this.context = context;
    }

    public String getNameByNumber(String number){
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor;
        String name = "";
        try {
            cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
            if(cursor.moveToFirst()){
                name = cursor.getString(0);
                cursor.close();
            }
        }catch (Exception e){
            Tip.show(context, R.string.tips_noo_primession);
        }
        return name;
    }
}
