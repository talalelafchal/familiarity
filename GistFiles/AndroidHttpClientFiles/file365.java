package com.example.name;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class Socket_cli {
	public void Socket_cli(){}
	
	static InputStream order_tmp=null;
	
	/*
	 * @ read all txt from url  
	 */
	public String read_TEXT(String URLBOT_T){	
		try{
			Config_App configapp=new Config_App();
			
			DefaultHttpClient def_client=new DefaultHttpClient();
			HttpGet httpget=new HttpGet(configapp.HttpUrl+"?"+URLBOT_T);
			HttpResponse httpres=def_client.execute(httpget);
			HttpEntity httpentry=httpres.getEntity();
			order_tmp=httpentry.getContent();
			
			BufferedReader bf=new BufferedReader(new InputStreamReader(order_tmp));
			StringBuilder sb=new StringBuilder();
			String url_code=null;
			while((url_code=bf.readLine())!=null){
				sb.append(url_code);
			}
			order_tmp.close();
			url_code=sb.toString();	
			
			return url_code;
		}catch(Exception e){return null;}
	}
	/*
	 * @ send post text 
	 */
	public boolean send_TEXT_POST(String URLBOT_S,List<NameValuePair> param){
		try{
			Config_App configapp=new Config_App();
			
			DefaultHttpClient def_cli_p=new DefaultHttpClient();
			HttpPost httppost=new HttpPost(configapp.HttpUrl+"?"+URLBOT_S);
			httppost.setEntity(new UrlEncodedFormEntity(param));
			HttpResponse httpres=def_cli_p.execute(httppost);
			return true;
		}catch(Exception e){return false;}
	}
	/*
	 * @ send  get txt
	 */
	public boolean send_TEXT_GET(String URLBOT_G){
		try{
			Config_App configapp=new Config_App();
			
			DefaultHttpClient def_cli=new DefaultHttpClient();
			HttpGet httpget=new HttpGet(configapp.HttpUrl+"?"+URLBOT_G);
			HttpResponse httpres=def_cli.execute(httpget);
			return true;
		}catch(Exception e){return false;}
	}
	/*
	 * @ send file to 
	 */
	public boolean send_FILE_TO(String URLBOT_U,String FILE_PATH,String FILE_NAME){
		try{
			Config_App configapp=new Config_App();
			
			HttpURLConnection conn=null;
			DataOutputStream dos=null;
			String lineEND="\r\n";
			String hyphnes="--";
			String boundary="*****";
			
			int byteRead,byteAvilable,bufferSize;
			byte[] buffer;
			int maxbufferSize=1*1024*1024;
			File sourcefile=new File(FILE_PATH);
			
			if(sourcefile.isFile()){
				
				try{
					
					FileInputStream fileinputstream=new FileInputStream(sourcefile);
					URL url=new URL(configapp.HttpUrl+"?"+URLBOT_U);
					
					conn=(HttpURLConnection) url.openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("ENCTYPE", "multipart/from-data");
					conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
					conn.setRequestProperty(FILE_NAME, FILE_PATH);
					
					dos=new DataOutputStream(conn.getOutputStream());
					
					dos.writeBytes(hyphnes+boundary+lineEND);
					dos.writeBytes("Content-Disposition: form-data; name=\""+FILE_NAME+"\";filename=\""+FILE_PATH+"\""+lineEND);
					dos.writeBytes(lineEND);
					
					byteAvilable=fileinputstream.available();
					bufferSize=Math.min(byteAvilable, maxbufferSize);
					buffer=new byte[bufferSize];
					
					byteRead=fileinputstream.read(buffer,0,bufferSize);
					
					while(byteRead>0){
						dos.write(buffer,0,bufferSize);
						byteAvilable=fileinputstream.available();
						bufferSize=Math.min(byteAvilable, maxbufferSize);
						byteRead=fileinputstream.read(buffer,0,bufferSize);
		
					}
					
					dos.writeBytes(lineEND);
					dos.writeBytes(hyphnes+boundary+hyphnes+lineEND);
					
					
					int serverResponseCode=conn.getResponseCode();
					String serverResponseMessage=conn.getResponseMessage();
					Log.e("server", serverResponseMessage);
					
					fileinputstream.close();
					dos.flush();
					dos.close();
					
				}catch(Exception e){Log.e("error connect", "error");}
				
			}
			
			
			return true;
		}catch(Exception e){return false;}
	}
}
