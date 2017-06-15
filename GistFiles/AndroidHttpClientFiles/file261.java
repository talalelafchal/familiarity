import android.content.Context;
import com.xfinity.ceylon_steel.util.InternetObserver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import java.io.*;
import java.net.UnknownHostException;
import java.util.HashMap;
 
/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
abstract class AbstractController {
 
    protected AbstractController() {
    }
 
    protected final static JSONObject getJsonObject(String url, HashMap<String, Object> parameters, Context context) throws IOException, JSONException, UnknownHostException {
        if (InternetObserver.isConnectedToInternet(context)) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            if (parameters != null) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (String parameter : parameters.keySet()) {
                    Object paramValue = parameters.get(parameter);
                    if (paramValue instanceof File) {
                        FileBody fileContent = new FileBody((File) paramValue, ContentType.MULTIPART_FORM_DATA);
                        multipartEntityBuilder.addPart(parameter, fileContent);
                    } else if (paramValue instanceof JSONObject || paramValue instanceof JSONArray) {
                        StringBody json = new StringBody(paramValue.toString(), ContentType.APPLICATION_JSON);
                        multipartEntityBuilder.addPart(parameter, json);
                    } else {
                        StringBody param = new StringBody(paramValue.toString(), ContentType.DEFAULT_TEXT);
                        multipartEntityBuilder.addPart(parameter, param);
                    }
                }
                HttpEntity httpPostParameters = multipartEntityBuilder.build();
                postRequest.setEntity(httpPostParameters);
            }
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader bufferedReader = null;
            String lineSeparator = System.getProperty("line.separator");
            String responseString = "";
            try {
                InputStream content;
                if ((content = response.getEntity().getContent()) == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(content));
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    responseString = responseString + currentLine + lineSeparator;
                }
                return new JSONObject(responseString);
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
        return null;
    }
 
    protected final static JSONArray getJsonArray(String url, HashMap<String, Object> parameters, Context context) throws IOException, JSONException, UnknownHostException {
        if (InternetObserver.isConnectedToInternet(context)) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            if (parameters != null) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (String parameter : parameters.keySet()) {
                    Object paramValue = parameters.get(parameter);
                    if (paramValue instanceof File) {
                        FileBody fileContent = new FileBody((File) paramValue, ContentType.MULTIPART_FORM_DATA);
                        multipartEntityBuilder.addPart(parameter, fileContent);
                    } else if (paramValue instanceof JSONObject || paramValue instanceof JSONArray) {
                        StringBody json = new StringBody(paramValue.toString(), ContentType.APPLICATION_JSON);
                        multipartEntityBuilder.addPart(parameter, json);
                    } else {
                        StringBody param = new StringBody(paramValue.toString(), ContentType.DEFAULT_TEXT);
                        multipartEntityBuilder.addPart(parameter, param);
                    }
                }
                HttpEntity httpPostParameters = multipartEntityBuilder.build();
                postRequest.setEntity(httpPostParameters);
            }
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader bufferedReader = null;
            String lineSeparator = System.getProperty("line.separator");
            String responseString = "";
            try {
                InputStream content;
                if ((content = response.getEntity().getContent()) == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(content));
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    responseString = responseString + currentLine + lineSeparator;
                }
                System.out.println(responseString);
                return new JSONArray(responseString);
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
        return null;
    }
 
}