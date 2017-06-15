package com.example.nawfal.caridata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

public class Detail extends Activity {
    Biodata biodata = new Biodata();
    private static final String TAG_TOILET_ID="toilet_id";
    private static final String TAG_NAME="name";
    private static final String TAG_LATITUDE="latitude";
    private static final String TAG_LONGITUDE="longitude";
    private static final String TAG_PRICE="price";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Mengambil data dari intent
        Intent i = getIntent();
        String toilet_id = i.getStringExtra(TAG_TOILET_ID);
        String name = i.getStringExtra(TAG_NAME);
        String latitude = i.getStringExtra(TAG_LATITUDE);
        String longitude = i.getStringExtra(TAG_LONGITUDE);
        String price = i.getStringExtra(TAG_PRICE);
        TextView textid = (TextView) findViewById(R.id.textid);
        TextView textName = (TextView) findViewById(R.id.textName);
        TextView textLat = (TextView) findViewById(R.id.textLat);
        TextView textLon = (TextView) findViewById(R.id.textLon);
        TextView textPrice = (TextView) findViewById(R.id.textPrice);
        textid.setText("Id : " + toilet_id);
        textName.setText("Nama : " + name);
        textLat.setText("Latitude : " + latitude);
        textLon.setText("Longitude : " + longitude);
        textPrice.setText("Price : Rp " + price);
    }

    public void UpdateonClick (View view)
    {
        String toilet_id = getIntent().getStringExtra(TAG_TOILET_ID);
        Integer id = Integer.parseInt(toilet_id);
        getDataByID(id);
    }

    public void deleteonClick(int id) {
        biodata.deleteBiodata(id);

  /* restart acrtivity */
        finish();
    }

    public void getDataByID(int id) {

        String nameEdit = null, latitudeEdit = null, longitudeEdit = null, priceEdit = null;
        JSONArray arrayPersonal;

        try {

            arrayPersonal = new JSONArray(biodata.getBiodataById(id));

            for (int i = 0; i < arrayPersonal.length(); i++) {
                JSONObject jsonChildNode = arrayPersonal.getJSONObject(i);
                nameEdit = jsonChildNode.optString("name");
                latitudeEdit = jsonChildNode.optString("latitude");
                longitudeEdit = jsonChildNode.optString("longitude");
                priceEdit = jsonChildNode.optString("price");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout layoutInput = new LinearLayout(this);
        layoutInput.setOrientation(LinearLayout.VERTICAL);

        // buat id tersembunyi di alertbuilder
        final TextView viewId = new TextView(this);
        viewId.setText(String.valueOf(id));
        viewId.setTextColor(Color.TRANSPARENT);
        layoutInput.addView(viewId);

        final EditText editName = new EditText(this);
        editName.setText(nameEdit);
        layoutInput.addView(editName);

        final EditText editlatitude = new EditText(this);
        editlatitude.setText(latitudeEdit);
        layoutInput.addView(editlatitude);

        final EditText editlongitude = new EditText(this);
        editlongitude.setText(longitudeEdit);
        layoutInput.addView(editlongitude);

        final EditText editprice = new EditText(this);
        editprice.setText(priceEdit);
        layoutInput.addView(editprice);

        AlertDialog.Builder builderEditBiodata = new AlertDialog.Builder(this);
        builderEditBiodata.setIcon(R.drawable.daftar);
        builderEditBiodata.setTitle("Update Biodata");
        builderEditBiodata.setView(layoutInput);
        builderEditBiodata.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editName.getText().toString();
                String latitude = editlatitude.getText().toString();
                String longitude = editlongitude.getText().toString();
                String price = editprice.getText().toString();

                System.out.println("Name : " + name + " Latitude : " + latitude + " Longitude : " + longitude + " Price : " + price);

                String laporan = biodata.updateBiodata(viewId.getText().toString(), editName.getText().toString(),
                        editlatitude.getText().toString(),editlongitude.getText().toString(),editprice.getText().toString());

                Toast.makeText(Detail.this, "Data Berhasil Diupdate", Toast.LENGTH_LONG).show();

    /* restart acrtivity */
                finish();
            }

        });

        builderEditBiodata.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderEditBiodata.show();

    }
}
