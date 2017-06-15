package com.athlete.com.api.clients.api_java_client;

import java.io.File;
import java.io.IOException;

//http://developer.android.com/reference/org/apache/http/HttpEntity.html
import org.apache.http.HttpEntity;
// New headers
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Hello world!
 *
 */
public class App{
    public static void main(String[] args){
    	System.out.println("Starting client...");
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	System.out.println("Fetching file...");
    	File file = new File("/home/santiago/Escritorio/chet.jpg");
    	//FileEntity entity = new FileEntity(file);
        Header content_type = new BasicHeader("Content-Type", "image/jpeg");
    	entity.setContentType(content_type);
    	HttpPost httppost = new HttpPost("http://localhost:8000/api/v1/user/picture/");
    	httppost.setEntity(entity);
    	try {
    		System.out.println("Starting request...");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseentity = response.getEntity();
			System.out.println("Finished. Status:");
			System.out.println("==================");
			System.out.println(response.getStatusLine());
			System.out.println("==================");
			System.out.println();
			System.out.println("==================");
			System.out.println(EntityUtils.toString(responseentity));
			System.out.println("==================");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}