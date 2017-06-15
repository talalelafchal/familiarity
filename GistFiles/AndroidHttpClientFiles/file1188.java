package com.matpompili.settle;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo on 24/10/14.
 */
public class SendUpdateView extends Activity {

    private boolean buttonClicked;

    private class Result {
        public Integer availability;
        public Integer quietness;
        public Boolean isLectureGoing;
        public String roomID;

        public Result(){

        }
    }

    Result updateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_update);

        RoomObject room = (RoomObject)getIntent().getSerializableExtra("room");

        setTitle("Invia aggiornamento");

        updateResult = new Result();
        updateResult.roomID = room.roomID;

        final Button sendButton = (Button) findViewById(R.id.buttonSend);
        buttonClicked = false;
        final TextView roomNameText = (TextView) findViewById(R.id.textRoom);
        final TextView buildingNameText = (TextView) findViewById(R.id.textBuilding);

        roomNameText.setText(Utilities.capitalize(room.name));
        buildingNameText.setText(Utilities.capitalize((String)getIntent().getSerializableExtra("buildingName")));

        final TextView availabilityText = (TextView) findViewById(R.id.textAvailabilityFollow);
        final TextView quietnessText = (TextView) findViewById(R.id.textQuietnessFollow);

        final SeekBar availabilitySeekBar = (SeekBar) findViewById(R.id.seekBarAvailability);
        availabilitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                availabilityText.setText(Integer.toString(progress+1));
            }
        });


        final SeekBar quietnessSeekBar = (SeekBar) findViewById(R.id.seekBarQuietness);
        quietnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quietnessText.setText(Integer.toString(progress+1));
            }
        });


        final RadioButton radioButtonYes= (RadioButton) findViewById(R.id.radioLectureYes);
        final RadioButton radioButtonNo= (RadioButton) findViewById(R.id.radioLectureFalse);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (buttonClicked){
                   new AsyncSend().execute();
                    sendButton.setText("Sto inviando...");
                    sendButton.setEnabled(false);
                } else {
                    updateResult.availability = availabilitySeekBar.getProgress();
                    updateResult.quietness = quietnessSeekBar.getProgress();
                    updateResult.isLectureGoing = radioButtonYes.isChecked();
                    availabilitySeekBar.setEnabled(false);
                    quietnessSeekBar.setEnabled(false);
                    radioButtonYes.setEnabled(false);
                    radioButtonNo.setEnabled(false);
                    buttonClicked = true;
                    sendButton.setText("Premi ancora per confermare");
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.root, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_info:
                Toast.makeText(getApplicationContext(), "Applicazione realizzata da Matteo Pompili: matpompili@gmail.com", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncSend extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            if (!Utilities.isOnline()) {
                Toast.makeText(getApplicationContext(), "Sembra che tu non sia connesso a internet, riprova quando lo sarai!", Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://matpompili.altervista.org/settle/postUpdate.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("roomID", updateResult.roomID));
                nameValuePairs.add(new BasicNameValuePair("availability", updateResult.availability.toString()));
                nameValuePairs.add(new BasicNameValuePair("quietness", updateResult.quietness.toString()));
                nameValuePairs.add(new BasicNameValuePair("isLectureGoing", (updateResult.isLectureGoing)?"1":"0"));
                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                String address = info.getMacAddress();
                address = Utilities.getMD5EncryptedString(address);
                nameValuePairs.add(new BasicNameValuePair("user", address));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                return inputStreamToString(response.getEntity().getContent()).equalsIgnoreCase("ok");


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(getApplicationContext(), "Aggiornato", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "C'è stato un errore", Toast.LENGTH_SHORT).show();
            }

            finish();

        }
    }

    private String inputStreamToString(InputStream is) throws IOException {
        String s = "";
        String line = "";

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) { s += line; }

        // Return full string
        return s;
    }


}
