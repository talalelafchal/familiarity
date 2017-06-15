package dave.demo.Utility;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by Dave on 2015/4/17.
 */
public class UtilityHttpGet extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... urls) {
        try
        {
            HttpClient _Client = new DefaultHttpClient();
            HttpGet _Get = new HttpGet(urls[0]);
            _Get.setHeader("User-Agent", "DaveX");
            HttpResponse _Response = null;
            try
            {
                _Response = _Client.execute(_Get);
            }
            catch (Exception e)
            {
                Log.i("UtilityHttpGet", "Error: " + e);
                e.printStackTrace();
            }
            if (_Response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                String _Result = null;
                try
                {
                    _Result = EntityUtils.toString(_Response
                            .getEntity());
                } catch (Exception e)
                {
                    Log.i("UtilityHttpGet", "Error: " + e);
                    e.printStackTrace();
                }
                return _Result;
            }
            else
            {
                return "";
            }
        }
        catch (Exception e)
        {
            Log.i("UtilityHttpGet", "Error! " + e);
            return "";
        }
    }
    // onPostExecute displays the results of the AsyncTask.

    @Override
    protected void onPreExecute() {
        //progressDialog.show();
        Log.i("UtilityHttpGet", "onPreExecute" );
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("UtilityHttpGet", "onProgressUpdate Process: "  + values[0] + "%");
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("UtilityHttpGet", "onPostExecute Result: " + result);
    }
}
