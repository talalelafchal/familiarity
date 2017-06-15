package your_packagename_here;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class EasyHttpClient {

    protected String url;
    protected HttpClient httpClient;
    protected List<NameValuePair> params = new ArrayList<NameValuePair>(1);

    public EasyHttpClient(String url) {
        this.httpClient = new DefaultHttpClient();
        this.url        = url;
    }

    public void addParam(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
    }

    public String doGet() throws ClientProtocolException, IOException {
        String queries = URLEncodedUtils.format(this.params, "UTF-8");
        HttpGet httpGet = new HttpGet(this.url + "?" + queries);

        return this.doHttpRequest(httpGet);
    }

    public String doPost() throws UnsupportedEncodingException, ClientProtocolException, IOException {
        UrlEncodedFormEntity entry = new UrlEncodedFormEntity(this.params);
        HttpPost httpPost = new HttpPost(this.url);
        httpPost.setEntity(entry);
        return this.doHttpRequest(httpPost);
    }

    protected String doHttpRequest(HttpUriRequest request) throws ClientProtocolException, IOException {
        String responseText = null;
        HttpResponse response = this.httpClient.execute(request);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(byteArrayOutputStream);

        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            responseText = byteArrayOutputStream.toString();
        }
        byteArrayOutputStream.close();

        return responseText;
    }
}