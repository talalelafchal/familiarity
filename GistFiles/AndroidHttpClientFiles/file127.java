import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Aneh Thakur 3 on 23-04-2015.
 */
public class AsyncReuse extends AsyncTask<Void, Void, Void> {
    public GetResponse getResponse = null;
    HttpClient client;
    HttpPost method;
    HttpResponse Server_response;
    String response = "{\"status\":\"0\",\"msg\":\"Sorry something went wrong try again\"}";
    JSONObject jsonObject;
    String URLs;
    boolean dialogE = true;
    Activity activity;
    // Dialog builder
    private ProgressDialog Dialog;

    public AsyncReuse(String url, boolean dialog, Activity activity1) {
        URLs = url;
        dialogE = dialog;
        activity = activity1;
    }

    public AsyncReuse(String url, boolean dialog) {
        URLs = url;
        dialogE = dialog;
    }

    public void getObjectQ(JSONObject object) {
        jsonObject = object;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialogE) {
            Dialog = new ProgressDialog(activity);
            Dialog.setMessage("Please Wait..");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        /*try {
            //Dialog.dismiss();
            client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URLs);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            List<NameValuePair> nVP = new ArrayList<NameValuePair>();
            nVP.add(new BasicNameValuePair("r", jsonObject.toString()));
            reqEntity.addPart("r", new StringBody(jsonObject.toString()));
            post.setEntity(reqEntity);
            Server_response = client.execute(post);

            if (Server_response != null) {
                InputStream in = Server_response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
                response = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            // set the connection timeout value to 30 seconds (30000 milliseconds)
            client = new DefaultHttpClient();
            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
            HttpConnectionParams.setSoTimeout(httpParams, 60000);

            client = new DefaultHttpClient();
            method = new HttpPost(URLs);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("r", new StringBody(jsonObject.toString()));
            method.setEntity(reqEntity);
            Server_response = client.execute(method);
            StatusLine statusLine = Server_response.getStatusLine();

            if (Server_response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Server_response.getEntity().writeTo(out);
                response = out.toString();
                out.close();
                Dialog.dismiss();
            } else if (Server_response.getStatusLine().getStatusCode() != HttpStatus.SC_BAD_REQUEST) {
                Dialog.dismiss();
            } else if (Server_response.getStatusLine().getStatusCode() != HttpStatus.SC_REQUEST_TIMEOUT) {
                response = "{\"status\":\"0\",\"msg\":\"Connection timeout.\"}";
                Dialog.dismiss();
            } else {
                Server_response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (UnsupportedEncodingException e) {
            // Unsporting error
        } catch (ClientProtocolException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (Dialog != null) {
            Dialog.dismiss();
        }
        getResponse.getData(response);
    }
}