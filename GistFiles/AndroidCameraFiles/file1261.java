package com.example.delle4310.wsepinm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static int ID = 0;
    private static String file_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.action_bar, null);
        actionBar.setCustomView(v);

        new DownloadFile(this).execute(getString(R.string.url_0), getString(R.string.url_1));

        //Listener on the btn1
        this.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ActivityOne.class);
                //Start ActivityOne
                startActivityForResult(intent, ActivityOne.ID);
            }
        });
        //Listener on the btn2
        this.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                v = getLayoutInflater().inflate( R.layout.layout_menu_item_two, null );

                ArrayList<String> names = new ArrayList<String>();
                final ArrayList<String> contents = new ArrayList<String>();


                try {
                    XmlPullParserFactory xmlFactoryObject = null;
                    XmlPullParser myParser = null;
                    FileInputStream input = null;

                    //It is the content useful
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    myParser = xmlFactoryObject.newPullParser();
                    File file = new File(getExternalFilesDir("dir_for_me"), "document.xml");
                    if(!file.exists()) {
                        Toast.makeText(v.getContext(), "The content is not available", Toast.LENGTH_LONG).show();
                    } else {
                        input = new FileInputStream(file);
                        myParser.setInput(input, null);
                        int event = myParser.getEventType();
                        boolean findValue = true;
                        //Scan the XML document
                        String nameFind = "activity";
                        String nameFound = "";
                        String attributeFind = "2";
                        String attributeFound = "";
                        boolean now = false;
                        boolean nextStep = false;
                        while (event != XmlPullParser.END_DOCUMENT && findValue) {
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    nameFound = myParser.getName();
                                    attributeFound = myParser.getAttributeValue(null, "number");
                                    if(attributeFound == null) attributeFound="";
                                    if(!now && !nextStep && nameFound.equals(nameFind) && attributeFound.equals(attributeFind)){ nameFind = "item"; nextStep = true; break;}
                                    if(!now && nextStep && nameFound.equals(nameFind)){ names.add(myParser.getAttributeValue(null, "name")); nameFind = "contentHTML"; now = true; nextStep= false; break;}
                                    break;
                                case XmlPullParser.END_TAG:
                                    nameFound = myParser.getName();
                                    if(nextStep && nameFound.equals("activity")) { findValue = false; break; }
                                case XmlPullParser.TEXT:
                                    if(now && nameFound.equals(nameFind)) { nameFound=""; contents.add(myParser.getText()); nameFind = "item"; nextStep = true; now = false;}
                                    break;
                            }
                            event = myParser.next();
                        }
                        input.close();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                int size = names.size();
                int count = 0;
                while(count < size) {
                    //Create frame where I can put the content of the menu item
                    FrameLayout frame = new FrameLayout(v.getContext());
                    frame.setBackgroundResource(R.drawable.top_border_item);

                    //Content of the menu item
                    TextView tv = new TextView(frame.getContext());
                    TextViewCompat.setTextAppearance(tv, R.style.TextItem);
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(names.get(count));
                    FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    int dpValue = 10; // margin in dips
                    float d = v.getResources().getDisplayMetrics().density;
                    int margin = (int) (dpValue * d); // margin in pixels
                    llp.setMargins(margin, margin, margin, margin);
                    tv.setLayoutParams(llp);
                    tv.setClickable(true);
                    tv.setId(count);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String s = contents.get(v.getId());
                            startActivityTwo(v, s);
                        }
                    });

                    //Add the content in the layout
                    frame.addView(tv);
                    LinearLayout ll = (LinearLayout) v.findViewById(R.id.llMenuTwo);
                    ll.addView(frame);
                    count++;
                }

                //To register the button with context menu.
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(v);
                builder.setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
//                Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                //Start ActivityTwo
//                startActivityForResult(intent, ActivityTwo.ID);
            }
        });

        //Listener on the btn3
        this.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ActivityThree.class);
                //Start ActivityThree
                startActivity(intent);
            }
        });

        //Listener on the btn4
        this.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){

                v = getLayoutInflater().inflate( R.layout.layout_menu_item_four, null );
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(v);
                builder.setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void startActivityTwo(View v, String s){
        Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);

        Bundle bundle = new Bundle();
        bundle.putString("content", s);
        intent.putExtras(bundle);

        //Start ActivityTwo
        startActivity(intent);
    }

    public void startActivityFour(View v){
        Intent intent = new Intent(getApplicationContext(), ActivityFour.class);

        Bundle bundle = new Bundle();
        LinearLayout l = (LinearLayout) v;
        TextView tv = (TextView)l.getChildAt(1);
        String name = tv.getText().toString();
        bundle.putString("name", tv.getText().toString());
        intent.putExtras(bundle);

        //Start ActivityTwo
        startActivity(intent);
    }

    public void callNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        //set number
        intent.setData(Uri.parse("tel:"+getString(R.string.number)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.i("CALL", e.toString());
            Toast.makeText(this, "Impossible to call", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void goSite(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //set site
        intent.setData(Uri.parse(getString(R.string.site)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.i("VIEW", e.toString());
            Toast.makeText(this, "Impossible to open the page", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

class DownloadFile extends AsyncTask<String, String, String> {
    Context context;
    long ts;
    int ver;
    boolean running;
    boolean success;
    boolean already;

    public DownloadFile(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running = true;
        loadSavedPreferences();
        File file = new File(context.getExternalFilesDir("dir_for_me"), "document.xml");
        //After 5 hours it will contact again the server. Before it contact only if it doesn't have document.xml
        if(System.currentTimeMillis()/1000 - ts < 180 && file.exists()) running = false;
        if(!isNetworkAvailable()){
            running = false;
            Toast.makeText(context, "The content can not be updated." +
                                    "\nActive your internet connection and restart the application!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        success = false;
        already = false;

        if(running == true) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                //It wants to ask only the version
                HttpURLConnection firstConnection = (HttpURLConnection)url.openConnection();
                firstConnection.connect();

                //this will be used in reading the data from the internet
                InputStream input = new BufferedInputStream(url.openStream());

                byte data[] = new byte[1024];
                //I read the version from the server
                input.read(data);
                input.close();
                int current_ver = data[0];
                already = (ver == current_ver);
                if(!already) {
                    url = new URL(f_url[1]);
                    //I want to ask only the version
                    HttpURLConnection secondConnection = (HttpURLConnection)url.openConnection();
                    secondConnection.connect();

                    //this will be used in reading the data from the internet
                    input = new BufferedInputStream(url.openStream());

                    //create a new file, specifying the path and the filename
                    File file = new File(context.getExternalFilesDir("dir_for_me"), "downloaded.xml");

                    if(file.exists()) file.delete();
                    //this will be used to write the downloaded data into the file we created
                    OutputStream output = new FileOutputStream(file);

                    while ((count = input.read(data)) != -1) output.write(data, 0, count);
                    output.flush();
                    output.close();
                    input.close();

                    // if file already exists will do nothing
                    file.createNewFile();

                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
        return null;
    }

    // Progress
    protected void onProgressUpdate(String... progress) {

    }

    @Override
    protected void onPostExecute(String file_url) {

        if(success) {
            Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
            //Old content
            File fileOld = new File(context.getExternalFilesDir("dir_for_me"), "document.xml");
            //New content, downloaded in background
            File fileNew = new File(context.getExternalFilesDir("dir_for_me"), "downloaded.xml");

                if(fileOld.exists()) fileOld.delete();
                fileNew.renameTo(fileOld);
                savePreferences("timeStamp", System.currentTimeMillis()/1000);

            XmlPullParserFactory xmlFactoryObject = null;
            XmlPullParser myParser = null;
            FileInputStream input = null;

            try {
                xmlFactoryObject = XmlPullParserFactory.newInstance();
                myParser = xmlFactoryObject.newPullParser();
                input = new FileInputStream(fileNew);
                myParser.setInput(input, null);
                int event = myParser.getEventType();
                boolean findValue = true;
                //Scan the XML document
                String name = null;
                while (event != XmlPullParser.END_DOCUMENT && findValue)  {
                    switch (event){
                        case XmlPullParser.START_TAG:
                            name=myParser.getName();
                            break;
                        case XmlPullParser.TEXT:
                            //Try to find the version and save it in shared preferences
                            if(name.equals("version")){
                                String s = myParser.getText();
                                savePreferences("version", Integer.valueOf(s));
                                findValue = false;
                            }
                            break;
                    }
                    event = myParser.next();
                }
                input.close();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(running)
            Toast.makeText(context, "The content can not be updated.", Toast.LENGTH_LONG).show();

    }

    public void loadSavedPreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        ts = sp.getLong("timeStamp", 0);
        ver = sp.getInt("version", 0);
    }

    public void savePreferences(String key, long value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void savePreferences(String key, int value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}