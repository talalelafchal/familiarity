/*
 *Steps to be followed for JSON parsing with Gson Library :-
 *	Download Gson library from above link & add jar to eclipse project.
 *	Create AsyncTask class to handle background operations.
 *	Use the DefaultHttpClient to retrieve the data if this is a web resource.
 *	Create respective model classes to handle response data.
 *	Create instance of Gson class.
 *	Use the fromJson() in order to parse the JSON input and return the model object.
 *	Update the UI elements by using model objetcs.

/*
 * parse json
 */

package com.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

public class MyClass extends Activity {

       TextView capitalTextView;
       ProgressDialog progressDialog;

       /** Called when the activity is first created. */
       @Override
       public void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);   
              setContentView(R.layout.main);
              capitalTextView = (TextView) findViewById(R.id.capital_textview);
              this.retrieveCapitals();
       }

       void retrieveCapitals() {
              progressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data...", true, true);

              CapitalsRetrieverAsyncTask task = new CapitalsRetrieverAsyncTask();
              task.execute();
              progressDialog.setOnCancelListener(new CancelListener(task));       
       }

       private class CapitalsRetrieverAsyncTask extends AsyncTask<Void, Void, Response> {
              @Override
              protected Response doInBackground(Void... params) {
                     String url = "http://api.androidsmith.com/capitals.php";
                     Reader inputStreamReader = getInputStreamReader(url);
                     return parseResponse(inputStreamReader);
              }

              @Override
              protected void onPostExecute(Response myresponse) {
                     super.onPostExecute(myresponse);
                     StringBuilder builder = new StringBuilder();

                     for (Country country : myresponse.data) {
                           builder.append(String.format("<br>Country: <b>%s</b><br>Capital: <b>%s</b><br><br>", country.getCountry(), country.getCapital()));
                     }
                     capitalTextView.setText(Html.fromHtml(builder.toString()));
                     progressDialog.cancel();
              }
       }

       private class CancelListener implements OnCancelListener {
              AsyncTask<?, ?, ?> cancellableTask;
              public CancelListener(AsyncTask<?, ?, ?> task) {
                     cancellableTask = task;
              }

              @Override
              public void onCancel(DialogInterface dialog) {
                     cancellableTask.cancel(true);
              }
       }

       private Reader getInputStreamReader(String url) {
              Reader inputStreamReader = null;
              HttpGet getRequest = new HttpGet(url);
              try {
                     DefaultHttpClient httpClient = new DefaultHttpClient();
                     HttpResponse getResponse = httpClient.execute(getRequest);
                     final int statusCode = getResponse.getStatusLine().getStatusCode();

                     if (statusCode != HttpStatus.SC_OK) {
                           Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url);
                           return null;
                     }

                     HttpEntity getResponseEntity = getResponse.getEntity();
                     InputStream httpResponseStream = getResponseEntity.getContent();
                     inputStreamReader = new InputStreamReader(httpResponseStream);
              }
              catch (IOException e) {
                     getRequest.abort();
                     Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
              }
              return inputStreamReader;
       }

       private Response parseResponse(Reader inputStreamReader){
              Gson gson = new Gson();
              Response response = gson.fromJson(inputStreamReader, Response.class);
              return response;
       }
}

Country.Java:-

package com.sample;

public class Country {

       String country;
       String capital;
      
       public Country() {
              this.country = "";
              this.capital = "";
       }

       public String getCountry() {
              return this.country;
       }

       public String getCapital() {
              return this.capital;
       }
}


Response.Java:-
package com.sample;
import java.util.ArrayList;
import com.androidsmith.sacc.model.Country;
public class Response {
       ArrayList<Country> data;
       public Response() {
              data = new ArrayList<Country>();
       }
}