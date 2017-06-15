package com.example.android.core.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created by kiolt_000 on 06-May-14.
 */
public class ApiTask extends AsyncTask<Object,Void,Object> {
    private static final String LOG_TAG = "Application Tag";
    private static final String serverUrl = "http://localhost:8080/api/";
    private final String methodName;
    private final boolean post;
    List<NameValuePair> arguments;
    public ApiTask(String methodName, List<NameValuePair> arguments, boolean post){
        this.arguments = arguments;
        this.methodName = methodName;
        this.post = post;
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            String url = serverUrl+methodName;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpRequestBase request;
            if(post) {
                request = new HttpPost(url);
                ((HttpPost)request).setEntity(new UrlEncodedFormEntity(arguments));
            }else{
                String args = URLEncodedUtils.format(arguments, "utf-8");
                url += "?" + args;
                request = new HttpGet(url);
            }
            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity httpEntity = httpResponse.getEntity();
            String responseStr = EntityUtils.toString(httpEntity);
            Log.i(LOG_TAG,"Server response " + responseStr);
            return responseStr;

        }
        catch (Exception exp){
            Log.e(LOG_TAG,"Loader error " + exp.toString());
            return exp;
        }
    }

}
