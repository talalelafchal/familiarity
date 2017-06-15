import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.usergrid.android.client.Client;
import org.usergrid.android.client.entities.User;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import co.ke.pyxis.authenticator.AccountGeneral;
import co.ke.pyxis.fashion.models.StreamItem;
import co.ke.pyxis.fashion.util.Constants;
import co.ke.pyxis.fashion.util.Utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

@SuppressLint("SdCardPath")
public class UploadActivity extends Activity {
	// API client
	Client client;
	// Account Manager
	private AccountManager mAccountManager;
	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
					Constants.SECRET_KEY));
	// data
	private static final String TAG = "MainActivity";
	private static final int TAKE_PICTURE = 1;
	private static final int CROP_PICTURE = 2;
	private static final int CHOOSE_PICTURE = 3;
	
	private static String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;// to store the big bitmap
	ProgressDialog dialog;
	// views
	private ImageView imageView;
//Buttons
	Button upload;
//Caption Edit text
	EditText caption;

	/** Activity life cycle */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		s3Client.setEndpoint("s3-eu-west-1.amazonaws.com");
		setContentView(R.layout.activity_upload);
		mAccountManager = AccountManager.get(this);
		getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
				AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
		// views
		imageView = (ImageView) findViewById(R.id.imageView);
		upload = (Button)findViewById(R.id.upload);
		caption = (EditText)findViewById(R.id.caption);
		client = new Client(AccountGeneral.ORGNAME + "/"
				+ AccountGeneral.APPNAME).withApiUrl(AccountGeneral.APIURL);
		// instantiate
		System.out.println(getFilesDir().getAbsolutePath()+"/temp.jpg");
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
		upload.setEnabled(false);
	
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {// result is not correct
			Log.e(TAG, "requestCode = " + requestCode);
			Log.e(TAG, "resultCode = " + resultCode);
			Log.e(TAG, "data = " + data);
			return;
		} else {
			switch (requestCode) {
			case TAKE_PICTURE:
				Log.d(TAG, "TAKE_PICTURE: data = " + data);// it seems to be														// null
				cropImageUri(imageUri, 600, 600, CROP_PICTURE);
				break;
			case CROP_PICTURE:// from crop_big_picture
				Log.d(TAG, "CROP_PICTURE: data = " + data);// it seems to be
																// null
				if (imageUri != null) {
					Bitmap bitmap = decodeUriAsBitmap(imageUri);
					imageView.setImageBitmap(bitmap);
					File myFile = new File(IMAGE_FILE_LOCATION);
					if(myFile.exists())
					    myFile.delete();
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse
							("file://"+ Environment.getExternalStorageDirectory())));
					upload.setEnabled(true);
				}
				break;
			case CHOOSE_PICTURE:
				Log.d(TAG, "CHOOSE_PICTURE: data = " + data);// it seems to
																	// be null
				if (imageUri != null) {
					Bitmap bitmap = decodeUriAsBitmap(imageUri);
					imageView.setImageBitmap(bitmap); 
					File myFile = new File(IMAGE_FILE_LOCATION);
					if(myFile.exists())
					    myFile.delete();
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse
							("file://"+ Environment.getExternalStorageDirectory())));
					upload.setEnabled(true);
				}
				break;
			default:
				break;
			}
		}
	}



	// Launch camera
	public void launchCamera(View view) {
		startDialog();
	}

	// Upload photo
	public void uploadPhoto(View view) {
		// The file

		BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView
				.getDrawable());
		if (bitmapDrawable != null) {
			new S3PutObjectTask().execute();
		} else {
			// No photo in image view
			Toast.makeText(this, "Please select and image", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private class S3PutObjectTask extends AsyncTask<Void, Void, S3TaskResult> {

		protected void onPreExecute() {
			dialog = new ProgressDialog(UploadActivity.this);
			dialog.setMessage("Uploading.......");
			dialog.setCancelable(false);
			dialog.show();
		}

		protected S3TaskResult doInBackground(Void... params) {
			String imagename = UUID.randomUUID().toString() + ".jpeg";
			BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView
					.getDrawable());
			Bitmap bitmap = bitmapDrawable.getBitmap();
			File file = new File(getCacheDir(), imagename);
			try {
				file.createNewFile();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] bitmapdata = bos.toByteArray();
				// write the bytes in file
				FileOutputStream fos = null;
				fos = new FileOutputStream(file);
				fos.write(bitmapdata);
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				PutObjectRequest por = new PutObjectRequest(
						Constants.PICTURE_BUCKET,imagename ,
						new java.io.File(file.getAbsolutePath()));
				por.setAccessControlList(acl);
				s3Client.putObject(por);
				result.setUri("https://fashion254.s3-eu-west-1.amazonaws.com/"+imagename);
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {
			if (result.getErrorMessage() != null) {
				if (dialog.isShowing())
					dialog.dismiss();
				displayErrorAlert("Upload Failed", result.getErrorMessage());
			} else {
				System.out.println(result.getUri());
				// Call appservices
				// post properties
				Map<String, Object> data = new HashMap<String, Object>();
				Map<String, Object> actor = new HashMap<String, Object>();
				Map<String, Object> image = new HashMap<String, Object>();
				User user = new Utils().getUser(getBaseContext());
				// add image url, height, and width of image
				image.put("url",
						user.getPicture());
				image.put("height", 80);
				image.put("width", 80);

				// add username, image, and email
				actor.put("uuid", user.getUuid());
				actor.put("displayName", user.getName());
				actor.put("username", user.getUsername());
				actor.put("image", image);
				actor.put("email",user.getEmail());
				// add actor, set action to post, and add message
				data.put("actor", actor);
				data.put("type", "activity");
				data.put("name", "client");
				data.put("verb", "post");
				data.put("content", caption.getText().toString().length()>0? caption.getText().toString() : "");
				data.put("post_photo", result.getUri());
				saveActivity(data);
			}
		}
	}

	private class S3TaskResult {
		String errorMessage = null;
		String uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}
	}
	public void saveActivity(final Map<String, Object> data) {
		@SuppressWarnings("unused")
		final AccountManagerFuture<Bundle> future = mAccountManager
				.getAuthTokenByFeatures(AccountGeneral.ACCOUNT_TYPE,
						AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, this,
						null, null, new AccountManagerCallback<Bundle>() {
							@Override
							public void run(AccountManagerFuture<Bundle> future) {
								Bundle bnd = null;
								try {
									bnd = future.getResult();
									final String authtoken = bnd
											.getString(AccountManager.KEY_AUTHTOKEN);
									if (authtoken != null) {
										
										User user = new Utils().getUser(getBaseContext());
										String url = AccountGeneral.APIURL+"/"+AccountGeneral.ORGNAME+"/"
										+AccountGeneral.APPNAME+"/"+"users/"+user.getUsername()+"/activities";
										
										AsyncHttpClient cli = new AsyncHttpClient();
										
										Gson gson = new Gson();
								        String json = gson.toJson(data);
								        StringEntity entity = new StringEntity(json);
										Header[] headers = {
											    new BasicHeader("Authorization", "Bearer " +authtoken)
											};
										
										System.out.println(authtoken);
										
										cli.post(getBaseContext(),url,headers, entity,"application/json",new AsyncHttpResponseHandler() {
										    @Override
										    public void onSuccess(String response) {
										    	if (dialog.isShowing())
													dialog.dismiss();
										    	JSONObject obj;
										    	JSONArray array=null;
										    	JSONObject entity=null;
										    	try {
										    		obj= new JSONObject(response);
										    		array = obj.getJSONArray("entities");
										    		entity= array.getJSONObject(0);
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
										    	Gson gson = new Gson();
										    	StreamItem streamItem = gson.fromJson(entity.toString(), StreamItem.class);
										      
										        Intent intent=new Intent();
										        //intent.putExtra("StreamItem", streamItem);
								                setResult(RESULT_OK, intent);
								                finish();
										    }

											@Override
											public void onFailure(
													Throwable arg0, String error) {
												super.onFailure(arg0, error);
												if (dialog.isShowing())
													dialog.dismiss();
											}
										   
										});

										System.out.println(url);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, null);

	}
	private void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");
		myAlertDialog.setMessage("How do you want to set your picture?");
		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = null;
						intent = new Intent(Intent.ACTION_GET_CONTENT, null);
						intent.setType("image/*");
						intent.putExtra("crop", "true");
						intent.putExtra("aspectX", 1);
						intent.putExtra("aspectY", 1);
						intent.putExtra("outputX", 600);
						intent.putExtra("outputY", 600);
						intent.putExtra("scale", true);
						intent.putExtra("return-data", false);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						intent.putExtra("outputFormat",
								Bitmap.CompressFormat.JPEG.toString());
						intent.putExtra("noFaceDetection", false); // no face
																	// detection
						startActivityForResult(intent, CHOOSE_PICTURE);
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = null;
						Log.e(TAG, "image uri can't be null");
						// capture a big bitmap and store it in Uri
						intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action
																				// is
																				// capture
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(intent, TAKE_PICTURE);
					}
				});
		myAlertDialog.show();
	}
	
	// Display an Alert message for an error or failure.
	protected void displayAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		confirm.show().show();
	}

	protected void displayErrorAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton("Okay",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						UploadActivity.this.finish();
					}
				});

		confirm.show().show();
	}
	
	
	
	
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	@Override
	protected void onDestroy() {
		// closing Entire Application
		trimCache(this);
		super.onDestroy();
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// <uses-permission
		// android:name="android.permission.CLEAR_APP_CACHE"></uses-permission>
		// The directory is now empty so delete it

		return dir.delete();
	}
	/**
	 * Get an auth token for the account. If not exist - add it and then return
	 * its auth token. If one exist - return its auth token. If more than one
	 * exists - show a picker and return the select account's auth token.
	 * 
	 * @param accountType
	 * @param authTokenType
	 */
	public void getTokenForAccountCreateIfNeeded(String accountType,
			String authTokenType) {
		@SuppressWarnings("unused")
		final AccountManagerFuture<Bundle> future = mAccountManager
				.getAuthTokenByFeatures(accountType, authTokenType, null, this,
						null, null, new AccountManagerCallback<Bundle>() {
							@Override
							public void run(AccountManagerFuture<Bundle> future) {
								Bundle bnd = null;
								try {
									bnd = future.getResult();
									final String authtoken = bnd
											.getString(AccountManager.KEY_AUTHTOKEN);
									Log.d("Pyxis","GetTokenForAccount Bundle is "+ bnd);
								} catch (Exception e) {
									e.printStackTrace();

								}
							}
						}, null);
	}
}