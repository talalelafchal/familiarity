package com.baidu.demo.controller;

import android.util.Log;
import com.baidu.demo.model.DemoSSModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-10-29
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public class DemoSSController {

    private DemoSSModel model;

    public DemoSSController() {
        model = new DemoSSModel();
    }

    public String getJsonStreetDataFromUrl(String url) {
        String json = null;
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 404) {
                json = EntityUtils.toString(response.getEntity());
                Log.d("Demo", "Response: " + json);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return json;
    }

}
