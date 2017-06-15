package com.baidu.demo.HttpRequst;

import android.util.Log;
import com.baidu.demo.util.ServerInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-10-29
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequest {

    public HttpRequest() {

    }

    public static InputStream getImage(String panoID, int colIdx, int rowIdx, String udt, int levelTemp) {
        String qt = "pdata";
        String urls = ServerInfo.IMAGE_SERVER
                + ServerInfo.KEY_WORD + "qt=" + qt
                + "&sid=" + panoID + "&pos=" + colIdx + "_" + rowIdx
                + "&z="+levelTemp+"&udt=" + udt;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(urls);
            if (null != url) {
                //Log.e("Demo","url"+urls);
                httpURLConnection = (HttpURLConnection) url.openConnection();


                //httpURLConnection.setConnectTimeout(5000);


                httpURLConnection.setDoInput(true);


                httpURLConnection.setRequestMethod("GET");

                int code = httpURLConnection.getResponseCode();

                if (200 == code) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else {
                    Log.e("Demo","inputStream"+code);
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public String getSinglePointMatch(String x, String y, String z, String t, String action, String time, String udt, String fn) {
        String qt = "qsdata";
        String url = ServerInfo.IP_ADDR + ServerInfo.PORT
                + ServerInfo.KEY_WORD + "qt=" + qt;
        if (x == null || y == null) {
            return null;
        } else {
            url += "&x=" + x + "&y=" + y;
        }

        if (z != null) {
            url += "&z=" + z;
        }

        if (t != null) {
            url += "&t=" + t;
        }

        if (action != null) {
            url += "&action=" + action;
        }

        if (time != null) {
            url += "&time=" + time;
        }

        if (udt != null) {
            url += "&udt=" + udt;
        }

        if (fn != null) {
            url += "&fn=" + fn;
        }
        return getJsonFromUrl(url);
    }

    private String getJsonFromUrl(String url) {
        String json = null;
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 404) {
                json = EntityUtils.toString(response.getEntity());
                Log.d("Demo", "Response: " + json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
