package com.example.sahil.pcsma_ass1_android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager sensorManager;
    Sensor accelerometer;
    File accFile;
    FileWriter fileWriter;

    String sFileName = "accerlerometer.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void onStartClick(View view){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStopClick(View view){
        sensorManager.unregisterListener(this);
    }

    public void onSendClick(View view){
        postData();
    }

    protected void onStart(){
        super.onStart();
        try {
            accFile = new File(getBaseContext().getFilesDir(), sFileName);
            fileWriter = new FileWriter(accFile);
        } catch(IOException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to open file", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
    }

    protected void onResume(){
        super.onResume();
        try {
            accFile = new File(getBaseContext().getFilesDir(), sFileName);
            fileWriter = new FileWriter(accFile);
        } catch(IOException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to open file", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
    }

    protected void onPause(){
        super.onPause();

        if(fileWriter != null){
            try{
                fileWriter.close();
            } catch(IOException e){
                Toast toast = Toast.makeText(getApplicationContext(), "Failed to close file.", Toast.LENGTH_SHORT);
                toast.show();
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x_axis_value = event.values[0];
        float y_axis_value = event.values[1];
        float z_axis_value = event.values[2];

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();
        String hour = Integer.toString(date.getHours());
        String minute = Integer.toString(date.getMinutes());
        String day = Integer.toString(date.getDay());
        String month = Integer.toString(date.getMonth());
        String year = Integer.toString(date.getYear());

        String timestamp = day + "/" + month + "/" + year +" " + hour + ":" + minute;
        String data = timestamp + "," + x_axis_value + "," + y_axis_value + "," + z_axis_value + "\n";
        Log.d("Data written ###:", data);
        try{
            fileWriter.append(data);
            fileWriter.flush();

        } catch (IOException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to write data", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(getApplicationContext(), "File is not Open", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
    }

    public void postData() {
        File file = new File(getBaseContext().getFilesDir(), sFileName);
        new senddata().execute();
        // Create a new HttpClient and Post Header
//        httpclient = new DefaultHttpClient();
//        httppost = new HttpPost("http://localhost:8080/upload");
//
//        try {
//            // Add your data
//            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//            //entity.addPart("name", new StringBody("acc.csv"));
//            //entity.addPart("file", new FileBody(file));
//            httppost.setEntity(entity);
//            new senddata().execute();
//            // Execute HTTP Post Request
////            HttpResponse response = httpclient.execute(httppost);
////            Toast toast = Toast.makeText(getApplicationContext(), "Response Code: " + response.getStatusLine().getStatusCode(), Toast.LENGTH_SHORT);
////            toast.show();
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//        }
    }

    class senddata extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                File file = new File(getBaseContext().getFilesDir(), sFileName);
                RestTemplate restTemplate = new RestTemplate(true);
                MultiValueMap<String, File> body = new LinkedMultiValueMap<>();
                List<File> list = new ArrayList<>();
                list.add(file);
                body.put("file", list);
                return restTemplate.postForObject("http://localhost:8080/upload", body, String.class);


                //return Integer.toString(httpclient.execute(httppost).getStatusLine().getStatusCode());
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response){
            Toast toast = Toast.makeText(getApplicationContext(), "Response Code: " + response, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}