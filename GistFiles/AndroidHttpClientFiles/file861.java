package com.vesicant.ActivityTest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: vesicant
 * Date: 13/03/13
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class DisplayMessageActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String userName = intent.getStringExtra(Main.USER_NAME);
        String userPass = intent.getStringExtra(Main.USER_PASS);

        String restResult = null;

        ICallBack<String,String> callBack = new ICallBack<String, String>() {
            @Override
            public String callBack(String result) {
                // Yay, we have data, and we're on the UI thread. \o/
                TextView textView = new TextView(DisplayMessageActivity.this);
                textView.setTextSize(40);
                textView.setText( result );

                setContentView(textView);
                
                return result;
            }
        }; 

        new AuthenticateTask().execute(callBack);
    }

    public interface ICallBack<T,U> {
        public T callBack(U result);
    }

    private class AuthenticateTask extends AsyncTask<ICallBack<String,String>, String, String> {
        private String httpResponse;
        private ICallBack<String,String> callBackRef;

        @Override
        protected String doInBackground(ICallBack<String,String>... callBack) {

            this.callBackRef = callBack[0];

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://gateway.routemonkey.com");

            post.setHeader("content-type", "application/json");

            post.setParams(DefaultRestRequestParams());

            HttpResponse response = null;
            try {
                response = client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return response.toString();
        }

        protected void onPostExecute(String result) {
            this.callBackRef.callBack(result);
        }
    }

    private String PerformRestRequest(String userName, String userPass, String keyword, String reference) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://gateway.routemonkey.com");

        post.setHeader("content-type", "application/json");

        HttpParams params = new BasicHttpParams();
        params.setParameter("keyword", keyword);
        params.setParameter("reference", reference);

        post.setParams(params);
        JSONObject data = new JSONObject();

        return "";
    }

    private HttpParams DefaultRestRequestParams() {
        HttpParams params = new BasicHttpParams();

        params.setParameter("username", RMSettings.Username);
        params.setParameter("password", RMSettings.Password);
        params.setParameter("company", RMSettings.Company);

        return params;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}