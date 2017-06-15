import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Kurokami on 18/12/2015.
 */
public class HttpRequest {

    private String requestURL;
    private HttpURLConnection connection;

    public HttpRequest(String requestURL) throws IOException {
        this.requestURL = requestURL;
    }

    public String sendPost(HashMap<String, String> params) throws IOException {
        URL url = new URL(requestURL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(UrlDataString(params));

        writer.flush();
        writer.close();
        os.close();

        String response = getResponse();
        return response;
    }

    public String sendGet(HashMap<String, String> params) throws IOException {
        String formattedParams = UrlDataString(params);
        URL url = new URL(requestURL + "?" + formattedParams);
        connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        String response = getResponse();
        return response;
    }

    private String getResponse() throws IOException {
        String response = "";

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        else {
            response = "";
        }

        return response;
    }

    private String UrlDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

/*
  Ejemplo:
  
  HttpRequest httpRequest = new HttpRequest("http://192.168.0.102/test.php");
  HashMap requestParams = new HashMap<String, String>();
  requestParams.put("nombre", "Jorge Fernando");
  requestParams.put("apellido", "Zabala Rueda");

  if(sendMethod == "GET")
      return httpRequest.sendGet(requestParams);
  else{
      return httpRequest.sendPost(requestParams);
  }
*/