package com.student.anurag.student_connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by anurag on 3/11/2015.
 */
public class Tabs extends Activity implements View.OnClickListener {

    public ListView searchResults;
    View myFragmentView;

    TabHost th;
    String tester1;
    String result;
    AlertDialog alertDialog;
    EditText ep;
    InputStream is = null;
    String[] info = new String[2];
    SwipeRefreshLayout swipeRefreshLayout;
    Button post, cancel;
    Context context = Tabs.this;
    TextView tester;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        searchResults = (ListView) findViewById(R.id.listView);
        post = (Button) findViewById(R.id.postb);
        ep = (EditText) findViewById(R.id.epost);
        cancel = (Button) findViewById(R.id.bcan);
tester=(TextView)findViewById(R.id.tester);
        th = (TabHost) findViewById(R.id.tabHost);

        post.setOnClickListener(this);

        info = MainActivity.nameSend();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } catch (NullPointerException e) {
            Log.e("Tag1", "over here");
        }

        th.setup();
        TabHost.TabSpec ts = th.newTabSpec("tag1");
        ts.setContent(R.id.Post);
        ts.setIndicator("Post");
        th.addTab(ts);
        ts = th.newTabSpec("tag2");
        ts.setContent(R.id.Events);
        ts.setIndicator("Events");
        th.addTab(ts);
        ts = th.newTabSpec("tag3");
        ts.setContent(R.id.Query);
        ts.setIndicator("Query");
        th.addTab(ts);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_dark, android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Log.d("Refresh", "Swipe");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        //new Tasktodo().execute();
                    }
                }, 3000);
            }
        });
    }

    public void testing(String m){
        tester1=m;
    }
    public interface MyInterface{
        public void calling(ArrayList<Posdent> aResults);
    }
    public void calling(ArrayList<Posdent> aResults) {
        searchResults.setAdapter(new SearchResultAdapter(Tabs.this, aResults));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postb:
                new task1().execute();
                new Tabasync2().execute(ep.getText().toString());
                tester.setText(tester1);
                searchResults.setVisibility(myFragmentView.VISIBLE);
                break;
            case R.id.bcan:
                break;
            case R.id.button:
                break;
        }
    }

    class SearchResultAdapter extends BaseAdapter {

        int count;
        Typeface type;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<Posdent> sdetails = new ArrayList<Posdent>();

        public SearchResultAdapter(Context context, ArrayList<Posdent> aResults) {

            layoutInflater = LayoutInflater.from(context);
            this.sdetails = aResults;
            this.count = aResults.size();
            this.context = context;
            type = Typeface.createFromAsset(context.getAssets(), "fonts/book.TTF");
            Log.d("Inside SearchResultAdapter", "" + count);
        }
        @Override
        public int getCount() {
            return count;
        }
        @Override
        public Object getItem(int position) {
            return sdetails.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Posdent posdent = sdetails.get(position);
            Log.d("Product Details Count=", " " + sdetails.size());
            Log.d("Name", " " + posdent.getStudent_name());
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listrow, null);
                holder = new ViewHolder();
                holder.student_name = (TextView) convertView.findViewById(R.id.student_name);
                holder.message = (TextView) convertView.findViewById(R.id.posthere);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.student_name.setText(posdent.getStudent_name());
            holder.student_name.setTypeface(type);

            holder.message.setText(posdent.getMessage());
            holder.message.setTypeface(type);

            return convertView;
        }
    }

    class task1 extends AsyncTask<String, String, Void> {
        //ProgressDialog progressDialog = new ProgressDialog(Tabs.this);

        @Override
        protected Void doInBackground(String... params) {
            String url_select = "http://amit2511.byethost22.com/new.php";
            String s_name = info[1];
            String m_usn = info[0];
            String message = ep.getText().toString();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_select);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>(3);
            param.add(new BasicNameValuePair("s_name", s_name));
            param.add(new BasicNameValuePair("m_usn", m_usn));
            param.add(new BasicNameValuePair("message", message));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(param));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                is = httpEntity.getContent();

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection" + e.toString());
                //    Toast.makeText(Tabs.this, "Please try again", Toast.LENGTH_LONG).show();
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result" + e.toString());
            }
            //mp.setText(result);
            return null;
        }
        protected void onPostExecute(Void v) {
        }
    }
}