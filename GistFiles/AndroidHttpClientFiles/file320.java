import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import android.net.Uri;
import android.database.Cursor;
import android.provider.MediaStore;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

...
...
	
private buttonChoose;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.register_layout);
	
	buttonChoose = (Button) findViewById(R.id.buttonChoose);
	buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        })
}
	
public String getPath(Uri uri)
{
	String[] projection = {MediaStore.MediaColumns.DATA};
	Cursor cursor = managedQuery(uri, projection, null, null, null);
	int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
	cursor.moveToFirst();
	String imagePath = cursor.getString(column_index);

	return cursor.getString(column_index);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
	super.onActivityResult(requestCode, resultCode, data);
	if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
	{
		Uri filePath = data.getData();
		
		final String pathImage = getPath(filePath);
		
		/* Set new shared pref for application with file-key RegistrationData */
		SharedPreferences sP = getSharedPreferences("RegistrationData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sP.edit();
		editor.putString("fileUrl", pathImage);
		editor.commit();
		new myTaskUploadImage().execute();
	}
	
}

private class myTaskUploadImage extends AsyncTask<String, String, String> 
{
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Toast.makeText(RegisterThirdStepActivity.this, "Please wait, we process your image first...", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		 /* Getting values from SharedPref */
		SharedPreferences mySp  = getSharedPreferences("RegistrationData", Context.MODE_PRIVATE);
		String fileImg = mySp.getString("fileUrl", "NULL");
		final File file = new File(fileImg) ;

		HttpClient hCli     = new DefaultHttpClient();
		HttpContext hCon    = new BasicHttpContext() ;
		HttpPost hP     = new HttpPost("YOUR API");

		String result = null;
		try{
			MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
			mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			mpEntity.addTextBody("name", "SOME NAME");
			mpEntity.addBinaryBody("photo", file);

			HttpEntity entity = mpEntity.build();
			hP.setEntity(entity);

			HttpResponse response = hCli.execute(hP);
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);
			Log.d("result ", result.toString());

		}catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d("result on done ", result.toString());

	}
}