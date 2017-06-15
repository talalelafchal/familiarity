/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Antonio Toro (antorof.dev@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package your.package.name;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class to help performing http petitions to a server.
 *
 * Includes methods to perform http GET and POST petitions and a function to convert an
 * {@link org.apache.http.HttpResponse} to <tt>String</tt>.
 */
public class Http {

    /**
     * Performs http POST petition to server.
     *
     * @param url        URL to perform POST petition.
     * @param parameters Parameters to include in petition.
     *
     * @return Response from the server.
     * @throws IOException If the <tt>parameters</tt> have errors, connection timmed out,
     *                     socket timmed out or other error related with the connection occurs.
     */
    public static HttpResponse post(String url, ArrayList<NameValuePair> parameters)
            throws IOException {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost(url);

        if (parameters != null)
            httppost.setEntity(new UrlEncodedFormEntity(parameters));

        HttpResponse response = httpclient.execute(httppost);

        return  response;
    }

    /**
     * Performs http POST petition to server.
     *
     * @param url URL to perform POST petition.
     *
     * @return Response from the server.
     * @throws IOException If the connection timmed out, socket timmed out or other error
     *                     related with the connection occurs.
     */
    public static HttpResponse post(String url)
            throws IOException {
        return post(url, null);
    }

    /**
     * Performs http GET petition to server.
     *
     * @param url        URL to perform GET petition.
     * @param parameters Parameters to include in petition.
     *
     * @return Response from the server.
     * @throws IOException If the <tt>parameters</tt> have errors, connection timmed out,
     *                     socket timmed out or other error related with the connection occurs.
     */
    public static HttpResponse get(String url, ArrayList<NameValuePair> parameters)
            throws IOException {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);

        if (parameters != null) {
            String paramString = URLEncodedUtils.format(parameters, "utf-8");
            url += paramString;
        }

        HttpGet httpget = new HttpGet(url);

        HttpResponse response = httpclient.execute(httpget);

        return  response;
    }

    /**
     * Performs http GET petition to server.
     *
     * @param url URL to perform POST petition.
     *
     * @return Response from the server.
     * @throws IOException If the connection timmed out, socket timmed out or other error
     *                     related with the connection occurs.
     */
    public static HttpResponse get(String url)
            throws IOException {
        return get(url, null);
    }

    /**
     * Converts {@link org.apache.http.HttpResponse} to <tt>String</tt>.
     * Converting a response to a string is useful, e.g. if you want to extract a Json.
     *
     * @param response Response to convert to <tt>String</tt>.
     * @param verbose  Show progress and data in <tt>Log</tt>
     *
     * @return Response converted to <tt>String</tt>.
     */
    public static String responseToString(HttpResponse response, boolean verbose) {
        HttpEntity entity = response.getEntity();
        String result = null;

        try{
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                if (verbose)
                    Log.v("readLine", "" + line);
            }

            inputStream.close();

            result=sb.toString();

            if (verbose)
                Log.v("responseToString", result);

        } catch(Exception e){
            if (verbose)
                Log.e("responseToString", "failed to convert response");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Converts {@link org.apache.http.HttpResponse} to <tt>String</tt>.
     * Converting a response to a string is useful, e.g. if you want to extract a Json.
     *
     * @param response Response to convert to <tt>String</tt>.
     *
     * @return Response converted to <tt>String</tt>
     */
    public static String responseToString(HttpResponse response) {
        return responseToString(response, false);
    }
}
