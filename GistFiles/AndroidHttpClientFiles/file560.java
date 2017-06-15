import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Request {
    private String resource;
    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;
    
    public Request(String resource) {
	this.resource = resource;
	params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public String getResource() {
        return resource;
    }

    public ArrayList<NameValuePair> getParams() {
        return params;
    }

    public ArrayList<NameValuePair> getHeaders() {
        return headers;
    }
}