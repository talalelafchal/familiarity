package com.alorma.universidad;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by a557114 on 03/06/2014.
 */
public class VersionChecker extends AsyncTask<Void, Void, VersionResponse> {

    private Context context;
    private String url;
    private String languageCode;
    private String currentVersion;
    private VersionCheckerListener mCheckerListener;

    public VersionChecker(Context context, String url, String languageCode, VersionCheckerListener versionCheckerListener) {
        this.context = context;
        this.url = url;
        this.languageCode = languageCode;
        this.currentVersion = currentVersion();
        mCheckerListener = versionCheckerListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mCheckerListener != null) {
            mCheckerListener.onVersionAquired(currentVersion);
        }
    }

    // TODO
    @Override
    protected VersionResponse doInBackground(Void... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);

        VersionResponse versionResponse = null;

        try {
            HttpResponse response = httpclient.execute(httpget);
            InputStream instream = response.getEntity().getContent();
            String result = convertStreamToString(instream);

            versionResponse = new Gson().fromJson(result, VersionResponse.class);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionResponse;
    }

    @Override
    protected void onPostExecute(VersionResponse versionResponse) {
        super.onPostExecute(versionResponse);
        if (mCheckerListener != null) {
            if (versionResponse != null) {
                if (versionResponse.isVersionEnabled(currentVersion)) {
                    mCheckerListener.onVersionEnabled(currentVersion);
                } else {
                    mCheckerListener.onVersionDisabled(currentVersion, versionResponse.getMessage(languageCode));
                }
            } else {
                mCheckerListener.onVersionCheckError();
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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

    private String currentVersion() {
        String v = "";
        try {
            v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return v;
    }

    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                connected = ni.isConnected();
            }
        }
        return connected;
    }

    public void check() {
        if (context != null && isNetworkAvailable()) {
            execute();
        }
    }

    public interface VersionCheckerListener {
        void onVersionAquired(String currentVersion);
        void onVersionEnabled(String version);
        void onVersionDisabled(String version, String message);
        void onVersionCheckError();
    }
}