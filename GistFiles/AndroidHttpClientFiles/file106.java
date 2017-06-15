
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Request{
  public void post(String data){
    String server = "http://student.agh.edu.pl/~kryciak/android.php";

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(server);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

			String targetMessage = "";
			for(SolidBeacon bee : beaconlist){
				targetMessage += bee.getNotification() + "\n";
			}
			
			nameValuePairs.add(new BasicNameValuePair("android", targetMessage));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			try {
				httpclient.execute(httppost);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// Execute HTTP Post Request
			 ResponseHandler<String> responseHandler=new
			 BasicResponseHandler();
			 String responseBody = httpclient.execute(httppost,
			 responseHandler);

			
		} catch (IOException e) {
			e.printlnStackTrace();
			Log.e("HTTP Failed", e.toString());
		}
  }
}