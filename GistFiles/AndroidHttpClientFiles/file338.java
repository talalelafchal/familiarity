import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class postData extends Activity {
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText login = (EditText) findViewById(R.id.login);
        final EditText passwd = (EditText) findViewById(R.id.passwd);
        Button register = (Button) findViewById(R.id.register);
        
        register.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				String loginS = login.getText().toString();
				String passwdS = passwd.getText().toString();
				postData(loginS, passwdS);
			}
        	
        });
    }
    public void postData(String mail, String passwd) {
        try {
	    HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://your_website.com/api/register?email="+mail+"&passwd="+passwd);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
}
}