package com.siyeol.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

		public TestTask login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button loginBtn = (Button)findViewById(R.id.button1);
		Button check = (Button)findViewById(R.id.button2);
		
		if(!netCheck()){
			AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
			alert.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	android.os.Process.killProcess(android.os.Process.myPid());
			    }
			});
			alert.setMessage("��Ʈ��ũ�� ����Ǿ� ���� �ʽ��ϴ�.\n��Ʈ��ũ ������ Ȯ�� �� �ٽ� �õ��� �ּ���.");
			alert.setTitle("��Ʈ��ũ ����");
			alert.show();
		}
		else{
			Log.d("Network","Network Connected");
		}
		
		loginBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				EditText idTxt = (EditText)findViewById(R.id.editText1);
				EditText pwTxt = (EditText)findViewById(R.id.editText2);
				
				String id = idTxt.getText().toString();
				String pw = pwTxt.getText().toString();
				
				if(id.length() == 0 || pw.length() == 0){
					Toast.makeText(MainActivity.this, "���̵�� ��й�ȣ�� ��� �Է��� �ּ���.", Toast.LENGTH_SHORT).show();
				}
				else{
					login = new TestTask();
					login.execute(id,pw);
				}
				
			}
			
		});
		
		check.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(login == null){
					Toast.makeText(MainActivity.this, "�ش� �����尡 �������� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
				}
				else{
					if(login.getStatus() == AsyncTask.Status.RUNNING){
						Toast.makeText(MainActivity.this, "Running", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(MainActivity.this, "Not Running", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		});
		
	}
	
	public boolean netCheck(){
		
		ConnectivityManager cm;
		NetworkInfo info = null;
		
		try{
			cm =  (ConnectivityManager)getSystemService(MainActivity.CONNECTIVITY_SERVICE);
			info = cm.getActiveNetworkInfo();
		}catch(Exception e){
			Log.e("Connectivity",e.toString());
		}
		
		if(info != null){
			return info.isConnected();
		}
		else{
			return false;


		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class TestTask extends AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... arg0) {
			
			String result = "";
			
			try
			{
			        HttpClient client = new DefaultHttpClient();
			        String postURL = "http://springvil.com/api3";
			        HttpPost post = new HttpPost(postURL);
			        List<NameValuePair> params = new ArrayList<NameValuePair>();
			        params.add(new BasicNameValuePair("email", arg0[0]));
			        params.add(new BasicNameValuePair("password", arg0[1]));
			        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			        post.setEntity(ent);
			        HttpResponse responsePOST = client.execute(post);
			        HttpEntity resEntity = responsePOST.getEntity();

			        result = EntityUtils.toString(resEntity);


//               

            }
			catch (Exception e)
			{
				Toast.makeText(MainActivity.this, "Exception Error",Toast.LENGTH_SHORT).show();
			}
			
			return result;
		}
		
		protected void onPostExecute(String result){
			
			int length = result.length();
			System.out.println(result);
			
			Log.d("Length",""+length);
			
			if(length == 3){
				Log.d("status", "fail");
				Toast.makeText(MainActivity.this, "�α��� ����", Toast.LENGTH_SHORT).show();
			}
			else if(length == 7){
				Log.d("status","success");
				Toast.makeText(MainActivity.this, "�α��� ����", Toast.LENGTH_SHORT).show();
			}
			else{
				Log.d("status","NoneFail");
				Toast.makeText(MainActivity.this, "Query Error", Toast.LENGTH_SHORT).show();
			}
			
		}

	}

}
