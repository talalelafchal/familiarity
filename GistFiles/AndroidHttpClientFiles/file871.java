/**
* วิธีใช้นะจ๊ะ
* File file = new File("sdcard/DH5_Sweeps_DETAIL_460x304.jpg");
*  ServerCommunication server = new ServerCommunication();
*  server.uploadUserPhoto(file);
*  อย่าลืมขอ Permission Internet ด้วยล่ะเตง
*/

/**
* Libraries for used
* http://hc.apache.org/downloads.cgi httpclient-4.1.jar, httpcore-4.1.jar and httpmime-4.1.jar
* http://james.apache.org/download.cgi#Apache_Mime4J apache-mime4j-0.6.1.jar
*/


package com.uploadfile;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class ServerCommunication {

  private DefaultHttpClient mHttpClient;

  public ServerCommunication() {
	    HttpParams params = new BasicHttpParams();
	    params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	    mHttpClient = new DefaultHttpClient(params);
	}


	@SuppressWarnings("unchecked")
	public void uploadUserPhoto(File image) {

	    try {

	        HttpPost httppost = new HttpPost("http://192.168.1.41/Bookish.MobileService/MemberRegister.ashx");

	        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	        multipartEntity.addPart("Title", new StringBody("Title"));
	        multipartEntity.addPart("Nick", new StringBody("Nick"));
	        multipartEntity.addPart("Email", new StringBody("Email"));
	        multipartEntity.addPart("Description", new StringBody("This is caption"));
	        multipartEntity.addPart("Image", new FileBody(image));
	        httppost.setEntity(multipartEntity);

	        mHttpClient.execute(httppost, new PhotoUploadResponseHandler());

	    } catch (Exception e) {
	        Log.e(ServerCommunication.class.getName(), e.getLocalizedMessage(), e);
	    }
	}

	@SuppressWarnings("rawtypes")
	private class PhotoUploadResponseHandler implements ResponseHandler {

	    @Override
	    public Object handleResponse(HttpResponse response)
	            throws ClientProtocolException, IOException {

	        HttpEntity r_entity = response.getEntity();
	        String responseString = EntityUtils.toString(r_entity);
	        Log.d("UPLOAD", responseString);

	        return null;
	    }

	}
}
