package cube.metasix.com.br.cube.util;

import android.support.annotation.NonNull;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceUtil {

    private static final int UM_MINUTO = 30000;

	public static HttpResponse executarPost(String path, List<HttpEntity> httpEntity, Map<String, String> headers, Map<String, ContentBody> contents) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		httpPost.setParams(setTimeout(UM_MINUTO));
        if(headers != null && !headers.isEmpty()){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
		if (httpEntity != null) {
            for(HttpEntity entity : httpEntity){
                httpPost.setEntity(entity);
            }
		}
        if(contents != null && !contents.isEmpty()){
            MultipartEntity multipartContent = new MultipartEntity();
            for (Map.Entry<String, ContentBody> entry : contents.entrySet()) {
                multipartContent.addPart(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(multipartContent);
        }
        return httpclient.execute(httpPost);
	}

	public static HttpResponse executarGet(String path, @NonNull Map<String, String> headers) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(path);
        if(headers != null && !headers.isEmpty()){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        //httpGet.setParams(setTimeout(UM_MINUTO));
        return httpclient.execute(httpGet);
	}

    public static HttpResponse executarPut(String path, Map<String, String> params,
                                           List<HttpEntity> httpEntity, String authorization) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(path);
        httpPut.setParams(setTimeout(UM_MINUTO));
        if (authorization != null) {
            if (!authorization.equals("")) {
                httpPut.addHeader("Authorization", "Token " + authorization);
            }
        }
        if (httpEntity != null) {
            httpPut.addHeader("Content-Type", "application/json");
            for(HttpEntity entity : httpEntity){
                httpPut.setEntity(entity);
            }
        }
        if(params != null){
            httpPut.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for(Map.Entry<String, String> item : params.entrySet()){
                nameValuePairs.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }
        return httpclient.execute(httpPut);
    }

    public static HttpParams setTimeout(int tempo) {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = tempo;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = tempo;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        return httpParameters;
    }
}