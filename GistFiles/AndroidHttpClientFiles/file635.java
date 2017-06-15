package com.thisclicks.appdataroom.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: scottolson
 * Date: 5/20/13
 * Time: 11:06 AM
 */
public class ExecuteRequest extends IntentService {

    private int responseCode;
    private int method;
    private String message;
    String response;
    private String entity;
    private ArrayList <ParcelableNameValuePair> params;
    private ArrayList <ParcelableNameValuePair> headers;
    private HttpRequestBase request;
    private ResultReceiver receiver;

    private static final String TAG = "com.thisclicks.appdataroom";

    private String url;


    public ExecuteRequest() {
        super("executeRestRequest");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        params = intent.getParcelableArrayListExtra("params");
        headers = intent.getParcelableArrayListExtra("headers");
        url = intent.getStringExtra("url");
        receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        method = (int) intent.getIntExtra("method", 1);
        entity = intent.getStringExtra("entity");
        try {
            execute(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executeDelete(String deletePath) throws Exception
    {
        request = new HttpDelete(url + deletePath);

        //add headers
        for(NameValuePair h : headers)
        {
            request.addHeader(h.getName(), h.getValue());
        }
        commit();
    }

    void execute(int method) throws Exception
    {
        switch(method) {
            case RestService.GET:
            {
                String combinedParams = "";
                if(!params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : params)
                    {
                        if(p.getName() != null && p.getValue() != null)
                        {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                            if(combinedParams.length() > 1)
                            {
                                combinedParams  +=  "&" + paramString;
                            }
                            else
                            {
                                combinedParams += paramString;
                            }
                        }
                    }
                }

                request = new HttpGet(url + combinedParams);
                //Log.i(TAG, String.format("Http Get: %s %s", url, combinedParams));

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }

                commit();
                break;
            }
            case RestService.POST:
            {
                request = new HttpPost(url);

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }
                if (entity != null && !entity.equals("")) {
                    StringEntity se = new StringEntity(entity);
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    ((HttpPost) request).setEntity(se);
                }
                commit();
                break;
            }
            case RestService.PUT:
            {
                request = new HttpPut(url);

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                    ((HttpPut) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }
                commit();
                break;
            }
            case RestService.DELETE:
            {
                request = new HttpDelete(url);

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }
                commit();
            }

        }
    }

    private void commit(){
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();


            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                String response = convertStreamToString(instream);
                Bundle responseBundle = new Bundle();
                responseBundle.putString("result", response);
                responseBundle.putString("responseCode", String.valueOf(responseCode));
                responseBundle.putString("responseMessage", message);
                receiver.send(method, responseBundle);
                // Closing the input stream will trigger connection release
                instream.close();

            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }


    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
