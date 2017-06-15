package com.oneunit.drunkcalls;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.oneunit.drunkcalls.FeedReaderContract.FeedEntry;

public class AddBlackList extends Activity {

    public int tagNumber;
    public static int[] tags;
    public static String name[];
    public static String phone[];
    public static String phoneSelected[];
    public static String nameSelected[];
    public FeedReaderDbHelper mdDbHelper;
    public AlarmService alarmService;
    public boolean alarmUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        this.mdDbHelper = new FeedReaderDbHelper(this);
        final SQLiteDatabase db = this.mdDbHelper.getWritableDatabase();

        this.alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        this.tagNumber = 0;
        this.nameSelected = new String[0];
        this.phoneSelected = new String[0];

        if (tags == null) {
            tags = new int[0];
        }
        if (name == null) {
            name = new String[0];
        }

        if (phone == null) {
            phone = new String[0];
        }

        TableLayout tl = (TableLayout) findViewById(R.id.activity_add_table_layout);
        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        while (phones.moveToNext()) {
            String name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            this.addRow(tl, name, phoneNumber);
        }
        phones.close();

        View view = new View(this);
        ArrayList<View> views = getViewsByTag(
                (ViewGroup) findViewById(R.id.activity_add_table_layout),
                "temp", db);

        for (int i = 0; i < views.size(); i++) {
            CheckBox checkBox = (CheckBox) views.get(i);
            checkBox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    if (checkBox.isChecked()) {
                        addSql(v.getId());
                    } else {
                        deleteSql(v.getId());
                    }

                }

                private void deleteSql(Object tag) {
                    for (int i = 0; i < phoneSelected.length; i++) {
                        if (phoneSelected[i]
                                .equals(phone[(Integer) tag - 1993])) {
                            if (phoneSelected.length == 0) {
                                phoneSelected = null;
                                nameSelected = null;
                                break;
                            } else {
                                for(int j = i; j < phoneSelected.length-1; j++ ){
                                    phoneSelected[j] = phoneSelected[j+1];
                                    nameSelected[j] = nameSelected[j+1];
                                }
                                String[] tempPhone = phoneSelected;
                                String[] tempName = nameSelected;
                                phoneSelected = new String[phoneSelected.length-1];
                                nameSelected = new String[nameSelected.length-1];
                                for(int k = 0; k< phoneSelected.length; k++){
                                    phoneSelected[k] = tempPhone[k];
                                    nameSelected[k] = tempName[k];
                                }
                                break;
                            }
                        }
                    }
                }

                private void addSql(Object tag) {

                    String[] tempPhone = phoneSelected;
                    String[] tempName = nameSelected;
                    phoneSelected = new String[phoneSelected.length + 1];
                    nameSelected = new String[nameSelected.length + 1];
                    for(int i = 0; i < tempPhone.length; i++){
                        phoneSelected[i] = tempPhone[i];
                        nameSelected[i] = tempName[i];
                    }
                    phoneSelected[phoneSelected.length-1] = phone[(Integer)tag -1993];
                    nameSelected[nameSelected.length-1] = (String) ((TextView) findViewById((Integer)tag+1000)).getText();
                    int[] temp = new int[tags.length];
                    temp = tags;
                    tags = new int[tags.length + 1];
                    for (int i = 0; i < temp.length; i++) {
                        tags[i] = temp[i];
                    }
                    tags[tags.length - 1] = (Integer) tag;

                }

                public void addName(int tag) {
                    tag += 1000;
                    String[] temp = new String[name.length];
                    temp = name;
                    name = new String[name.length + 1];
                    for (int i = 0; i < temp.length; i++) {
                        name[i] = temp[i];
                    }
                    name[name.length - 1] = (String) ((TextView) findViewById(tag))
                            .getText();
                }
            });
        }

        Button button = (Button) findViewById(R.id.activity_add_button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(phoneSelected.length == 0){
                    Toast.makeText(getApplicationContext(), "No number was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    ContentValues values = new ContentValues();
                    for(int i = 0; i < phoneSelected.length; i++){
                        values.put(FeedEntry._ID, phoneSelected[i]);
                        values.put(FeedEntry.COLUMN_NAME_TITLE,
                                nameSelected[i]);
                        values.put(FeedEntry.COLUMN_NAME_ENTRY_ID,
                                phoneSelected[i]);
                        values.put(FeedEntry.COLUMN_NAME_FOREVER, false);
                        long rowID = db.insert("BlockedNumbers", null, values);
                    }
                    startService(new Intent(AddBlackList.this, CallService.class));
                    if (!alarmUp) {
                        alarmService = new AlarmService(AddBlackList.this);
                        alarmService.startAlarm();
                    }
                }
            }
        });

        Button buttonSetTime = (Button) findViewById(R.id.activity_add_set_time);
        if (this.alarmUp) {
            buttonSetTime.setClickable(false);
        } else {
            buttonSetTime.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setTimeDialog timeDialog = new setTimeDialog(
                            AddBlackList.this);
                    timeDialog.show();
                }
            });
        }

        Button buttonAddOther = (Button) findViewById(R.id.activity_add_another);
        buttonAddOther.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addOtherDialog timeDialog = new addOtherDialog(
                        AddBlackList.this);
                timeDialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_group) {

            return false;
        }
        else if(id == R.id.see_group){
            Intent intent = new Intent(this, Drunk.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.group_pernament){
            Intent intent = new Intent(this, AddPermanent.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.see_pernament){
            Intent intent = new Intent(this, SeePernament.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void addPhone(String phone) {
        String temp[] = new String[this.phone.length];
        temp = this.phone;
        this.phone = new String[this.phone.length + 1];
        for (int i = 0; i < temp.length; i++) {
            this.phone[i] = temp[i];
        }
        this.phone[this.phone.length - 1] = phone;
    }

    public void addRow(TableLayout tl, String text, String phone) {

        SQLiteDatabase db = this.mdDbHelper.getWritableDatabase(); if
                (db.rawQuery( "SELECT * FROM " + FeedEntry.TABLE_NAME + " WHERE " +
                        FeedEntry._ID + " = '" + phone + "'", null) .moveToFirst()) {
            this.tagNumber++;
            return;
        }
		 
		/*for(int i = 0; i < phoneSelected.length; i++){
			if(phoneSelected[i].equals(phone)){
			return;
			}
		}*/
        addPhone(phone);
        LayoutInflater inflater = getLayoutInflater();
        TableRow tr = (TableRow) inflater.inflate(R.layout.add_row_layout, tl,
                false);
        TextView textView = (TextView) tr.findViewById(R.id.add_row_name);
        textView.setText(text);
        textView.setId(2993 + this.tagNumber);
        CheckBox checkBox = (CheckBox) tr.findViewById(R.id.add_row_check);
        checkBox.setId(1993 + this.tagNumber++);
        tl.addView(tr);

    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag,
                                                 SQLiteDatabase db) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag, db));
            }
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }
}