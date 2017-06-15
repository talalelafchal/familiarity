package com.ctrlsmart.Net;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2015/4/1.
 */
public class MyHttpClient
{
    public String executeGet(String paramString)
    {
        try
        {
//            URL url = new URL(paramString);
//            URLConnection urlConnection = url.openConnection();
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            WebResourceResponse wbR =new WebResourceResponse(null, "UTF-8",in);
////            Log.e(" wbR.getStatusCode();===="," wbR.getStatusCode");
////            wbR.getStatusCode();


            HttpResponse localHttpResponse = new DefaultHttpClient().execute(new HttpGet(paramString));
            if (localHttpResponse.getStatusLine().getStatusCode() == 200)
            {
                String str = EntityUtils.toString(localHttpResponse.getEntity(), "UTF-8");
                Log.e("","str-============"+str);
                return str;
            }
        }
        catch (ClientProtocolException localClientProtocolException)
        {
            localClientProtocolException.printStackTrace();
//            return "";
        }
        catch (IOException localIOException)
        {
//            for (;;)
//            {
//                localIOException.printStackTrace();
//            }
        }
        return "";
    }
}