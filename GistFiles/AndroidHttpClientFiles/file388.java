package silver_bullet.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class SilverBulletActivity extends Activity {
	public static final String PREFS = "SilverBulletPrefs";
	public RESTAPICall apiCall;
	public static final String TAG = "Silver_BulletActivity";
	public static AlertDialog alert = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	final SharedPreferences settings = getSharedPreferences(PREFS, 0);
    	if(!settings.contains("UserName")){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AccountManager mgr = AccountManager.get(this);
            Account[] accts = mgr.getAccountsByType("com.google");
            final String[] items = new String[accts.length];
            for(int i = 0; i < accts.length; i++){
            	items[i] = accts[i].name;
            }
    		builder.setTitle("Choose Account To Log Into SilverBullet");
    		builder.setItems(items,  new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int item) {
    		    	SharedPreferences.Editor editor = settings.edit();
    		    	editor.putString("UserName", items[item]);
    		    	editor.commit();
    		        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    		    }
    		});
    		alert = builder.create();
    		alert.show();
    	}
    	
    	this.apiCall = new RESTAPICall((Context)this, settings);
    	new CheckUserProfileTask().execute(this);    	
    }
    
    public void onPause(){
    	if(alert != null){
    		alert.dismiss();
    	}
    }
    
    private class ResponseObject{
    	public String failure = null;
    	public String error = null;
    	public String playerid = null;
    }
    
    private class CheckUserProfileTask extends AsyncTask<Activity, Void, String>{
    	private Activity activity;
		@Override
		protected String doInBackground(Activity... params) {
			this.activity = params[0];
			String res = null;
			try {
				URI uri = new URI(RESTAPICall.BASE_URL+"player");
				Log.w(TAG, uri.getRawPath());
				HttpPost post = new HttpPost(uri);
				HttpClient client = new DefaultHttpClient();
				List<NameValuePair> nameValueParams = new ArrayList<NameValuePair>();
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValueParams, "UTF-8");
				post.setEntity(entity);
				post.setHeader("Cookie", apiCall.getAuthCookie());
				post.setHeader("X-Same-Domain", "1");
				ResponseHandler<String> responseHandler=new BasicResponseHandler();
				res = client.execute(post, responseHandler);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, e.getClass().toString()+e.getMessage());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, e.getClass().toString()+e.getMessage());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, e.getClass().toString()+e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, e.getClass().toString()+e.getMessage());
			}
			
			return res;
			
		}
    	
		protected void onPostExecute(String res){
			
			Intent myIntent;
			Gson gson = new Gson();
			//Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
			Log.w("CheckUserProfileTask", res);
			ResponseObject obj = gson.fromJson(res, ResponseObject.class);
			if(obj.failure != null){
				Toast.makeText(getApplicationContext(), obj.failure, Toast.LENGTH_SHORT).show();
			}else if(obj.error != null){
				myIntent = new Intent(activity, UserPageEditActivity.class);
		    	startActivityForResult(myIntent, 0);
			}else{
				SharedPreferences.Editor editor = getSharedPreferences(PREFS, 0).edit();
		    	editor.putString("PlayerId", obj.playerid);
		    	editor.commit();
		    	myIntent = new Intent(activity, MainActivity.class);
		    	startActivityForResult(myIntent, 0);
			}
		}
    }
}