package com.sysfort.nfc_reader;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SenderTask extends AsyncTask<String, String, String> {
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int WAIT_RESPONSE_TIMEOUT = 10000;
    private static String TAG = SenderTask.class.getSimpleName();
    public AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public SenderTask(AsyncResponse response) {
        delegate = response;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        List<HttpPost> httpPostList;
        HttpClient httpclient = new DefaultHttpClient();
        HttpParams httpParameters = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, WAIT_RESPONSE_TIMEOUT);
        HttpConnectionParams.setTcpNoDelay(httpParameters, true);

        String fileName = "nfcread.csv";
        String path = Environment.getExternalStorageDirectory() + "/NFC/" + fileName;

        File mFile = new File(path);

        try {
            httpPostList = fetchDataFromFileToHttpResponce(mFile);
            for(HttpPost post : httpPostList){
                HttpResponse response = httpclient.execute(post);
                InputStream inputStream = response.getEntity().getContent();
                delegate.processFinish(convertStreamToString(inputStream)); //pass result to activity
                Log.d(TAG, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<HttpPost> fetchDataFromFileToHttpResponce(File file) throws IOException {
        List<HttpPost> result = new ArrayList<HttpPost>();

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line;
        if (reader.readLine() != null)  //Skip file header
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                HttpPost httppost = new HttpPost("http://www.busimanager.com/websevices/sams/addReading.php");
                httppost.setHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded;charset=UTF-8");
                httppost.setEntity(new UrlEncodedFormEntity(
                        bindValuePair(RowData[0],
                                RowData[1],
                                RowData[2],
                                RowData[3],
                                RowData[4],
                                RowData[5],
                                RowData[6],
                                RowData[7],
                                RowData[8],
                                RowData[9])));
                result.add(httppost);
            }
        reader.close();
        fileInputStream.close();

        return result;
    }


    private List<NameValuePair> bindValuePair(String name, String employeeID, String mobileDeviceID,
                                              String frontserialID, String frontScanDate,
                                              String frontScanTime, String backserialID,
                                              String backScanDate, String backScanTime,
                                              String timeDiffrence) {
        List<NameValuePair> result = new ArrayList<NameValuePair>(10);
        result.add(new BasicNameValuePair("Name", name));
        result.add(new BasicNameValuePair("EmployeeID", employeeID));
        result.add(new BasicNameValuePair("MobileDeviceID", mobileDeviceID));
        result.add(new BasicNameValuePair("FrontserialID", frontserialID));
        result.add(new BasicNameValuePair("FrontScanDate", frontScanDate));
        result.add(new BasicNameValuePair("FrontScanTime", frontScanTime));
        result.add(new BasicNameValuePair("BackserialID", backserialID));
        result.add(new BasicNameValuePair("BackScanDate", backScanDate));
        result.add(new BasicNameValuePair("BackScanTime", backScanTime));
        result.add(new BasicNameValuePair("TimeDiffrence", timeDiffrence));
        return result;
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}