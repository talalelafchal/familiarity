import org.json.JSONException;
import org.json.JSONObject;

public class Response {
    private String url;
    private int responseCode;
    private String message;
    private String response;
    
    public String getUrl() {
        return url;
    }
	
    public void setUrl(String url) {
        this.url = url;
    }
	
    public int getResponseCode() {
        return responseCode;
    }
	
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public JSONObject getJSONResponse() throws JSONException {
        return new JSONObject(response);
    }

    public void setResponse(String response) {
        this.response = response;
    }
}