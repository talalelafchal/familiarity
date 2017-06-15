package com.gardentheory.app;

import com.gardentheory.app.Constants;

import android.app.IntentService;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;

import android.net.Uri;
import android.util.Base64;
import android.net.http.AndroidHttpClient;
import java.io.InputStream;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Date;
import java.io.IOException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.NotificationManager;


public class UploadService extends IntentService {

    private static final String TAG = "UploadService";

    private static final int loginNotificationId = 1;

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Log.d(TAG, "onHandleIntent");

        SharedPreferences prefs = getSharedPreferences(Constants.AUTH_PREFS, MODE_PRIVATE);
        String username = prefs.getString(Constants.PREFERENCES_USERNAME, null);
        String authToken = prefs.getString(Constants.PREFERENCES_AUTH_TOKEN, null);

        if (username == null || authToken == null) {
            Log.d(TAG, "Bad login credentials.");
            onLoginFailure(intent.getExtras());
            return;
        } else {
            Log.d(TAG, "Got auth token for upload.");
            handleSingleImage(intent, username, authToken);
        }
    }

    private void onLoginFailure(Bundle uploadExtras) {
        Log.d(TAG, "onLoginFailure.");
        Log.d(TAG, "Clearing auth token.");
        SharedPreferences prefs = getSharedPreferences(Constants.AUTH_PREFS, MODE_PRIVATE);
        // Clear their old token.
        Editor ed = prefs.edit();
        ed.putString(Constants.PREFERENCES_AUTH_TOKEN, null);
        ed.commit();

        Log.d(TAG, "Starting notification.");

        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(this, LoginNotificationActivity.class);

        // Pass our extras onto the intent.
        notifyIntent.putExtras(uploadExtras);

        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                this,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("Login Failed");
        builder.setContentText("Please update your credentials to finish your file upload.");
        builder.setAutoCancel(true);
        // TODO: USE A REAL ICON
        builder.setSmallIcon(R.drawable.ic_launcher);
        // Notifications are issued by sending them to the
        // NotificationManager system service.
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds an anonymous Notification object from the builder, and
        // passes it to the NotificationManager
        mNotificationManager.notify(loginNotificationId, builder.build());

        Log.d(TAG, "Finished sending notification.");

    }

    private void handleSingleImage(Intent intent, String username, String authToken) {
        Log.d(TAG, "handleSingleImageRequest");
        Bundle extras = intent.getExtras();
        if (extras.containsKey(Intent.EXTRA_STREAM)) {
            Log.d(TAG, "Found stream to upload, uploading.");
            try {
                // Get resource path from intent callee
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

                // Query gallery for camera picture via
                // Android ContentResolver interface
                ContentResolver cr = getContentResolver();
                InputStream is = cr.openInputStream(uri);
                InputStreamEntity ise = new InputStreamEntity(is, -1);
                String scheme = "http";
                String host = "192.168.0.3";
                String path = "/photos";
                HttpClient client = AndroidHttpClient.newInstance("Android");

                String putUrl = scheme + "://" + host + path +
                    "?username=" + URLEncoder.encode(username, "utf8") +
                    "&auth_token=" + URLEncoder.encode(authToken, "utf8");
                // @TODO: Use a URL builder.
                HttpPut put = new HttpPut(putUrl);
                put.setEntity(ise);
                HttpResponse response = client.execute(put);

                int responseStatus = response.getStatusLine().getStatusCode();
                if (responseStatus == 403 || responseStatus == 401) {
                    Log.d(TAG, "Bad login.");
                    onLoginFailure(extras);
                } else {
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else if (extras.containsKey(Intent.EXTRA_TEXT)) {
            Log.e(TAG, "Found text to upload, skipping.");
            return;
        }
    }

}