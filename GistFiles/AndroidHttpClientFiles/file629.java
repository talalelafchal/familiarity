package com.example.Posten2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.DTDHandler;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: danielg
 * Date: 13.08.13
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class DetailView extends Activity {

    TextView txt_sporingsnummer = null;
    TextView txt_sisteOppdatering = null;
    TextView txt_avsenderLand = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview);
        String sporingsnummer = "";
        String avSenderLand = "";
        ArrayList<String> samleStatuser = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sporingsnummer = extras.getString("sporingsnummer");
            avSenderLand = extras.getString("avsenderLand");
            samleStatuser = extras.getStringArrayList("array");


        }
        txt_sporingsnummer = (TextView) findViewById(R.id.txt_sporingsnummer);
        txt_sisteOppdatering = (TextView) findViewById(R.id.txt_sisteOppdatering);
        txt_avsenderLand = (TextView) findViewById(R.id.txt_avsenderLand);

        txt_sporingsnummer.setText(sporingsnummer);
        txt_avsenderLand.setText(avSenderLand);

        ListView listView = (ListView)findViewById(R.id.lst_status);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, samleStatuser);
        listView.setAdapter(stringArrayAdapter);
    }




}
