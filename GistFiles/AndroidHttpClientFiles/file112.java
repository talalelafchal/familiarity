

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity {
	
	Integer newVer;
	Integer curVer;
	String downloadUrl;
	ProgressDialog pBar;
	TextView ver_error;
	
	/*
	 *  WARNING: change DEBUG to 0 for release build!!!
	 */
	//TODO: DEBUG setting
	Integer DEBUG = 0;
	
	private Handler mHttpHandler=new Handler(){
		public void handleMessage(Message m){
			String respond;
			JSONObject obj;
			int x,y,ret;
			switch(m.what){
			case 1:
				respond=m.getData().getString("return");
				try{
					obj = new JSONObject(respond);
					if (DEBUG == 1){
						newVer = obj.getInt("dver");
						downloadUrl = obj.getString("dUrl");
						
					}else if(DEBUG == 0){
						newVer = obj.getInt("ver");
						downloadUrl = obj.getString("downloadUrl");
					}
					
					curVer = Home.getVerCode(getApplicationContext());
					
					if(newVer > curVer)
						doNewVersionUpdate();
					else
						notNewVersionShow();
				}catch (JSONException e){
					ver_error.setText("无法获取版本信息");
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		ver_error = (TextView) findViewById(R.id.txt_ver_error);
		ver_error.setText("");
		
		pBar = new ProgressDialog(this.getParent());
		
		// 查询版本
		String httpUrl="http://yourserver/version.html";
		HttpThread mHttpThread = new HttpThread(mHttpHandler,1,httpUrl);
		mHttpThread.start();
		
	}
	
	public void update() {  
	    Intent intent = new Intent(Intent.ACTION_VIEW);  
	    intent.setDataAndType(Uri.fromFile(new File(Environment  
	            .getExternalStorageDirectory(), "ppk-lastest.apk")),  
	            "application/vnd.android.package-archive");  
	    startActivity(intent);  
	}  
	
	void downFile(final String url) {  
	    pBar.show();  
	    
	    new Thread() {  
	        public void run() {  
	            HttpClient client = new DefaultHttpClient();  
	            HttpGet get = new HttpGet(url);  
	            HttpResponse response;  
	            try {  
	                response = client.execute(get);  
	                HttpEntity entity = response.getEntity();  
	                long length = entity.getContentLength();  
	                InputStream is = entity.getContent();  
	                FileOutputStream fileOutputStream = null;  
	                if (is != null) {  
	                    File file = new File(  
	                            Environment.getExternalStorageDirectory(),  
	                            "ppk-lastest.apk");  
	                    fileOutputStream = new FileOutputStream(file);  
	                    byte[] buf = new byte[1024];  
	                    int ch = -1;  
	                    int count = 0;  
	                    while ((ch = is.read(buf)) != -1) {  
	                        fileOutputStream.write(buf, 0, ch);  
	                        count += ch;  
	                        if (length > 0) {  
	                        }  
	                    }  
	                }  
	                fileOutputStream.flush();  
	                if (fileOutputStream != null) {  
	                    fileOutputStream.close();  
	                }  
	                down();  
	            } catch (ClientProtocolException e) {  
	            	Log.i("down", "ClientProtocolException");
	                e.printStackTrace();  
	            } catch (IOException e) {  
	            	Log.i("down","IOException");
	                e.printStackTrace();  
	            }  
	        }  
	    }.start(); 
	} 
	
	private void down() {
		pBar.cancel();
		update();
	}
	
	private void notNewVersionShow() {
		int verCode = Home.getVerCode(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本: ");
		sb.append(verCode);
		sb.append(" 已是最新版 \n无需更新!");
		Dialog dialog = new AlertDialog.Builder(this.getParent()).setTitle("软件更新")
				.setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						notNew).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	private OnClickListener notNew = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(UpdateActivity.this, SettingActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//把一个Activity转换成一个View  
	        Window w = ExchangeGroup.group.getLocalActivityManager()  
	                .startActivity("SettingActivity",intent);  
	        View view = w.getDecorView();  
	        //把View添加大ActivityGroup中  
	        ExchangeGroup.group.setContentView(view);
		}
	};
	
	private void doNewVersionUpdate() {
		int verCode = Home.getVerCode(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verCode);
		sb.append(" 发现新版本:");
		sb.append(newVer);
		sb.append("\n是否更新?");
		Dialog dialog = new AlertDialog.Builder(this.getParent())
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新", progressDown)
				.setNegativeButton("暂不更新", notNew).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	private DialogInterface.OnClickListener progressDown = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			pBar.setTitle("正在下载");
			pBar.setMessage("请稍候...");
			pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			downFile(downloadUrl);
		}
	};

}
