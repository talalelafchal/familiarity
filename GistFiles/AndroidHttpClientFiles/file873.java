package inc.bs.vturesultchecker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    JSONArray ja;
    JsonObjectRequest jor;
    String url1,url2,ve;
    int loll;
    RequestQueue requestQueue;
    ProgressDialog pd;
    TextView title;
    Button b1,b2,b23,b4,b5,b6;
    int nl;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title=(TextView) findViewById(R.id.title);
        b1=(Button) findViewById(R.id.button1);
        b2=(Button) findViewById(R.id.button2);
        b23=(Button) findViewById(R.id.button23);


        url1="http://result.vtu.ac.in/cbcs_results2017.aspx?usn=1KS15CS003&sem=3";

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uriUrl = Uri.parse("http://results.vtu.ac.in/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
                MainActivity.this.startActivity(intent);
            }});
        b23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uriUrl = Uri.parse("http://vtu.ac.in/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
                MainActivity.this.startActivity(intent);
            }});

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Loading");
                pd.show();

                if ( !isNetworkAvailable() ) { // loading offline
                    Toast.makeText(MainActivity.this, "No Internet!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Please connect to internet and then click reload");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                else {
                    try {
                        Log.d("HTTP","HTTP arrived");

                        pd.dismiss();

           /*             HttpGet httpRequest = new HttpGet(url1);
                        HttpEntity httpEntity = null;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response = httpclient.execute(httpRequest);
                        nl = response.getStatusLine().getStatusCode();

             */

               /*         URL u = new URL( url1);
                        Log.d("HTTP URL",url1);
                        HttpURLConnection huc =  (HttpURLConnection) u.openConnection();
                        huc.setRequestMethod("POST");
                        HttpURLConnection.setFollowRedirects(true);
                        huc.connect();
                        nl = huc.getResponseCode();
                        Log.d("Http res","RES HERE!"+String.valueOf(nl));
                 */

                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                       //                 Log.d("HTTP RESPONSE","Response is: "+ response.substring(100,5000));
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                               Log.d("HTTP VOLLEY","ERROR");
                            }
                        }){

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                nl= response.statusCode;
                                Log.d("Http res","RES HERE!"+String.valueOf(nl));
                                loll= response.statusCode;
                                if(loll==200)
                                    ve="RESULTS ANNOUNCED";
                                else
                                    ve="RESULTS NOT ANNOUNCED";

                                return super.parseNetworkResponse(response);
                            }};
                        queue.add(stringRequest);
                        Log.d("HTTP n1",String.valueOf(loll));


        /*                URL url = new URL(url1);
                        Log.d("HTTP URL",url1);
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        int code = connection.getResponseCode();
                        Log.d("HTTP n1",String.valueOf(code));
                        HttpURLConnection.setFollowRedirects(false);
*/
//                        nl=code;
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(ve);
                        builder1.setCancelable(true);

                        builder1.setNegativeButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    } catch (Exception E) {
                        Toast.makeText(MainActivity.this, "SOMETHING WENT WRONG!!", Toast.LENGTH_SHORT);
                    }
                }    }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}





/*        requestQueue = Volley.newRequestQueue(this);

        jor = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            ve = "ERROR";
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {
                                    case 302:
                                        ve = "Results not announced yet";
                                        break;
                                    case 200:
                                        ve = "RESULTS MAY HAVE BEEN ANNOUNCED!!";
                                        break;
                                }
                                //Additional cases
                            }
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage(ve);
                            builder1.setCancelable(true);

                            builder1.setNegativeButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            builder1.setPositiveButton(
                                    "Reload",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            recreate();
                                        }
                                    }
                            );
                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Volley", "Error");
                        pd.dismiss();
                    }
                }
        );
        jor.setShouldCache(false);
        requestQueue.add(jor);
*/
