

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.BannerListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchMovie extends Activity {

    String text;
    ListView lv;
    ArrayList<String> allMovieTitles;
    ArrayList<String> allMovieIDBM;
    SearchAdapt adapsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StartAppAd.showSplash(this, savedInstanceState);
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this,"00000000",true);
        setContentView(R.layout.activity_search_movie);


        EditText ed = (EditText) findViewById(R.id.seartET);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/tttt.ttf");
        ed.setTypeface(custom_font);


        lv = (ListView) findViewById(R.id.searchlist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String bdiItem = allMovieIDBM.get(position).toString();
                DownloadPagePlot downloadAsync = new DownloadPagePlot();
                downloadAsync.execute("http://omdbapi.com?i=" + bdiItem);
            }
        });



        ImageButton bt = (ImageButton) findViewById(R.id.searchBT);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText ed = (EditText) findViewById(R.id.seartET);

                text = ed.getText().toString();


                text = text.replace(" ", "%20");
                DownloadPage downloadAsync = new DownloadPage();
                downloadAsync.execute("http://omdbapi.com?s="+text);



            }
        });


        Banner ban=(Banner)findViewById(R.id.startAppBannerS);
        ban.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View view) {

            }

            @Override
            public void onFailedToReceiveAd(View view) {

            }

            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }


    class DownloadPage extends AsyncTask<String, Void, String> {


        private ProgressDialog mDialog;

        protected void onProgressUpdate(Integer... progress) {

            mDialog = new ProgressDialog(SearchMovie.this);
            mDialog.show();
            mDialog.setProgress((progress[0]));
        }
        protected void onPreExecute() {

            TextView loadingtx=(TextView)findViewById(R.id.textView4);
            loadingtx.setText("Loading......");
        }


        protected String doInBackground(String... urls) {

            String html = "";
            try {
                String url = urls[0];
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
                in.close();
                html = str.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return html;
        }


        protected void onPostExecute(String result) {

            TextView loadingtx=(TextView)findViewById(R.id.textView4);
            loadingtx.setText("");

            allMovieTitles = new ArrayList<String>();
            allMovieIDBM = new ArrayList<String>();


            try {


                JSONObject theBigOnject = new JSONObject(result);
                JSONArray myarray = theBigOnject.getJSONArray("Search");

                for (int i = 0; i < myarray.length(); i++) {
                    JSONObject obj = myarray.getJSONObject(i);
                    String movieTitle = obj.getString("Title");
                    String ID = obj.getString(("imdbID"));
                    allMovieTitles.add(movieTitle);
                    allMovieIDBM.add(ID);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

           // lv = (ListView) findViewById(R.id.searchlist);
            adapsearch = new SearchAdapt(SearchMovie.this,R.layout.searchitem, allMovieTitles);
           lv.setAdapter(adapsearch);


     //     ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchMovie.this ,android.R.layout.simple_list_item_1, android.R.id.text2,allMovieTitles);
       //     lv.setAdapter(ad);



        }


    }

    class DownloadPagePlot extends AsyncTask<String, Void, String>{

        //* loading message when we connect the net.
        protected void onPreExecute() {

            TextView loadingtx=(TextView)findViewById(R.id.textView4);
            loadingtx.setText("Loading......");
        }
        //* connecting the net for download the details.
        protected String doInBackground(String... urls) {

            String html = "";
            try {
                String url =  urls[0];
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
                in.close();
                html = str.toString();
            }catch (IOException ex)
            {
                ex.printStackTrace();
            }
            return html;
        }
        //* delete the message "loading" and download list.
        protected void onPostExecute(String result) {


            TextView loadingtx=(TextView)findViewById(R.id.textView4);
            //  TextView ratingTX=(TextView)findViewById(R.id.rating);
            loadingtx.setText(" ");

            try {



                JSONObject theBigOnject= new JSONObject(result);
                String movieTitle=theBigOnject.getString("Title");
                String urlImage=theBigOnject.getString("Poster");
                String Body=theBigOnject.getString("Plot");
                String IMDrating=theBigOnject.getString("imdbRating");
                String imdbid=theBigOnject.getString("imdbID");



                Intent intent=new Intent(SearchMovie.this,editmovie.class);
                intent.putExtra("subject",movieTitle);
                intent.putExtra("body", Body);
                intent.putExtra("url", urlImage);
                intent.putExtra("rating",IMDrating);
                intent.putExtra("imdbid",imdbid);
                startActivity(intent);
                finish();



            }

            catch (JSONException e) {
                e.printStackTrace();
            }





        }


    }
}
