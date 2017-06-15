
public class TwitterHelper
{

	public static final String TWEET_SUCESS="tweet_sucess";
	public static final String TWEET_DUPLICATE="tweet_duplicate";
	public static final String TWEET_FAIL="tweet_fail";
	public static final String TWEET_LOGIN_NOTFOUND="twitter_login_notfound";

	private ProgressDialog progressDialog;
	private boolean isPromotingMsg;

	private Context mContext;


	static final String CALLBACK_URL ="http://www.sunshineinfotech.com/";
	//private static final String CONSUMER_KEY = "1HzDGi7HdwOXz3TRdboj32hyl";
	//private static final String CONSUMER_SECRET = "aBv4IqzHM2wQKiKlblMEqMxbZ5rvysyjpRj2ItPt2BPGP41VjU";

	private static final String TWITPIC_KEY = "please enter key";

	private OAuthProvider provider = null;

	private CommonsHttpOAuthConsumer consumer = null;

	private HttpClient client = null;

	private String message = "";
	private File fileUpload = null;

	private static String PREFERENCE_NAME = "twitter_oauth";

	private static final String PREF_KEY_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_TOKEN = "oauth_token";

	private SharedPreferences mTwitterPreferences;


	private TwHelperListener mTwHelperListener;

	public interface TwDialogListener
	{
		public void onComplete(String value);

		public void onComplete(String value, String data);

		public void onError(String value);
	}

	public interface TwHelperListener
	{
		public void onComplete(String result);

		//		public void onError(String value,int countMerge);

	}

	private TwDialogListener dialogListerner = new TwDialogListener()
	{

		@Override
		public void onError(String value)
		{
			// mListener.onError(value);
			mTwHelperListener.onComplete(Global.LOGIN_ABORT_ERROR);
		}

		@Override
		public void onComplete(String value)
		{
			processToken(value);
		}

		@Override
		public void onComplete(String value, String data)
		{
		}
	};

	private void showLoginDialog(String url)
	{
		new TwitterDialog(mContext, url, dialogListerner).show();
	}

	@SuppressWarnings("static-access")
	public TwitterHelper(Context context)
	{
		this.mContext = context;
		// if (!appInstalledOrNot())
		// {
		mTwitterPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, mContext.MODE_PRIVATE);
		setUpOAuth();
		// }

	}

	public void setmTwHelperListener(TwHelperListener mTwHelperListener)
	{
		this.mTwHelperListener = mTwHelperListener;
	}

	private void setUpOAuth()
	{

		consumer = new CommonsHttpOAuthConsumer(mContext.getString(R.string.twiiter_con_key), mContext.getString(R.string.twitter_con_secret));

		provider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token",
				"https://api.twitter.com/oauth/authorize");

		client = new DefaultHttpClient();
	}

	public void login(boolean isPromotingMsg)
	{
		this.isPromotingMsg=isPromotingMsg;
		LoginRequestTask tweetTask = new LoginRequestTask();
		tweetTask.execute();
	}

	public void logout()
	{
		
		new TwitterLogoutTask().execute();
		
	}

	
	class TwitterLogoutTask extends AsyncTask<Void, Void, Boolean>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				 HttpClient httpclient = new DefaultHttpClient();
				 HttpPost httppost = new HttpPost("http://api.twitter.com/1/account/end_session.format");
				 HttpResponse response = httpclient.execute(httppost);
				 System.out.println("Response Code: "+response.getStatusLine().getStatusCode());
				 Logger.i(EntityUtils.toString(response.getEntity()));
				 return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mTwitterPreferences != null && result)
			{
				mTwitterPreferences.edit().clear().commit();
				mTwHelperListener.onComplete(Global.LOGOUT_SUCESS);
			}
		}
	}
	
	public boolean isLogin()
	{
		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
		if (token != null && tokenSecret != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean appInstalledOrNot()
	{
		PackageManager localPackageManager = mContext.getPackageManager();
		try
		{
			localPackageManager.getPackageInfo("com.twitter.android", 1);
			return true;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException)
		{
			return false;
		}
	}

	// private void startIntentForTwitter()
	// {
	// // TODO Auto-generated method stub
	// try
	// {
	// Intent localIntent = new Intent("android.intent.action.SEND");
	// localIntent.setType("image/jpeg");
	// localIntent.putExtra("android.intent.extra.SUBJECT", mContext.getResources().getString(R.string.app_name));
	// localIntent.setPackage("com.twitter.android");
	//
	// localIntent.putExtra("android.intent.extra.TEXT", message);
	// if (fileUpload != null)
	// {
	// localIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileUpload));
	// }
	//
	// mContext.startActivity(localIntent);
	//
	// } catch (Exception localException)
	// {
	// localException.printStackTrace();
	// }
	// }

	//	public void loginAndTweet(String msg)
	//	{
	//		this.message = msg;
	//		this.fileUpload = null;
	//		friendId=0;
	//		// if (appInstalledOrNot())
	//		// {
	//		// startIntentForTwitter();
	//		// }
	//		// else
	//		// {
	//		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
	//		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
	//		if (token != null && tokenSecret != null)
	//		{
	//			consumer.setTokenWithSecret(token, tokenSecret);
	//			afterLoginSucess();
	//		}
	//		else
	//		{
	//			LoginRequestTask tweetTask = new LoginRequestTask();
	//			tweetTask.execute();
	//		}
	//		// }
	//
	//	}

	private long friendId = 0;
	/**
	 * 
	 * @param msg pass message which you want to post
	 * @param friendId pass friendId for post on friend's tag or pass 0.
	 * @param fileUpload pass File object of image to post
	 */
	public void loginAndTweetOnFriendWall(String msg, long friendId, File fileUpload)
	{
		this.friendId = friendId;
		this.message = msg;
		this.fileUpload = fileUpload;
		// if (appInstalledOrNot())
		// {
		// startIntentForTwitter();
		// }
		// else
		// {
		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
		if (token != null && tokenSecret != null)
		{
			consumer.setTokenWithSecret(token, tokenSecret);
			tweetOnWall(msg, friendId, null);
		}
		else
		{
			mTwHelperListener.onComplete(TWEET_LOGIN_NOTFOUND);
		}

		// }

	}

	/**
	 * For tweet on wall
	 * 
	 * @param msg pass message which you want to post
	 * @param friendId pass friendId for post on friend's tag or pass 0.
	 * @param fileUpload pass File object of image to post
	 */
	public void tweetOnWall(String msg, long friendId, File fileUpload)
	{
		this.message = msg;
		this.friendId=friendId;
		this.fileUpload = fileUpload;

		if (fileUpload == null)
		{
			new TweetRequestTask().execute();
		}
		else
		{
			//ImageSender imageSender = new ImageSender();
			//imageSender.execute();
		}

	}


	//	public void loginAndGetFollowers()
	//	{
	//		isGetFollowers = true;
	//
	//		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
	//		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
	//		if (token != null && tokenSecret != null)
	//		{
	//			consumer.setTokenWithSecret(token, tokenSecret);
	//			afterLoginSucess();
	//		}
	//		else
	//		{
	//			login();
	//		}
	//
	//	}

	//	public void loginAndTweetImage(String msg, File fileUpload)
	//	{
	//		this.message = msg;
	//		this.fileUpload = fileUpload;
	//		// if (appInstalledOrNot())
	//		// {
	//		// startIntentForTwitter();
	//		// }
	//		// else
	//		// {
	//		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
	//		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
	//		if (token != null && tokenSecret != null)
	//		{
	//			consumer.setTokenWithSecret(token, tokenSecret);
	//			afterLoginSucess();
	//		}
	//		else
	//		{
	//			LoginRequestTask tweetTask = new LoginRequestTask();
	//			tweetTask.execute();
	//		}
	//		// }
	//
	//	}

	private class LoginRequestTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected void onPreExecute()
		{

			dismissProgressDialog();
			progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true);

			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					LoginRequestTask.this.cancel(true);
				}
			});
			progressDialog.show();

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params)
		{

			String authUrl = null;
			try
			{
				authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
				return authUrl;
			} catch (Exception ex)
			{
				return null;
			}

			// Note the singleInstance setting in the manifest.xml

		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			dismissProgressDialog();
			if (result != null)
			{
				showLoginDialog(result);
				// mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
				// .parse(result)));
			}
			else
			{
				showToast("Please check your device time/date and try again!");
			}

		}
	}

	private void dismissProgressDialog()
	{
		try
		{
			progressDialog.dismiss();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void showToast(String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	protected void processToken(String value)
	{
		Uri uri = Uri.parse(value);
		if (uri != null && uri.toString().startsWith(CALLBACK_URL))
		{

			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			// this will populate token and token_secret in consumer

			if (verifier != null)
			{
				RetriveAccessTokenTask accessTokenTask = new RetriveAccessTokenTask();
				accessTokenTask.execute(verifier);
			}
			else
			{
				mTwHelperListener.onComplete(null);
			}
			// At this point you can call consumer#getToken() and
			// consumer#getTokenSecret() to get and save token/secret for
			// subsequent calls. According to Twitter � access token will not
			// expire unless revoked by user
			
		}
		else
		{
			mTwHelperListener.onComplete(null);
		}
	}

	private void saveTokesForFuture()
	{
		mTwitterPreferences.edit().putString(PREF_KEY_TOKEN, consumer.getToken()).commit();
		mTwitterPreferences.edit().putString(PREF_KEY_SECRET, consumer.getTokenSecret()).commit();
	}

	//	private void afterLoginSucess()
	//	{
	//
	//		if (fileUpload == null)
	//		{
	//			new TweetRequestTask().execute();
	//		}
	//		else
	//		{
	//			ImageSender imageSender = new ImageSender();
	//			imageSender.execute();
	//		}
	//	}

	/*private class GetAllFollowersTask extends AsyncTask<Void, Void, String>
	{
		private Twitter twitter;
		private ContactsDB contactsDB;

		protected int countMerge=0;

		private final String PROFILE_INITIAL_URL="https://twitter.com/";

		protected void onPreExecute()
		{
			contactsDB = new ContactsDB(mContext);
			contactsDB.openDataBase();

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET).setOAuthAccessToken(consumer.getToken())
					.setOAuthAccessTokenSecret(consumer.getTokenSecret()).build();
			twitter = new TwitterFactory(conf).getInstance();

			dismissProgressDialog();

			if(notificationHelper!=null)
			{
				notificationHelper.setContextTitle("Twitter");
				notificationHelper.setContextText(NotificationHelper.PREPARING_MSG);
				notificationHelper.setProgress(0);
				notificationHelper.setOnProgressCancelListerner(new OnProgressCancelListerner()
				{

					@Override
					public void OnCancel()
					{
						cancel(true);
					}
				});
			}
			else
			{
				progressDialog = new ProgressDialog(mContext);
				progressDialog.setTitle("Twitter");
				progressDialog.setMessage("Fetching...");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setOnCancelListener(new OnCancelListener()
				{

					@Override
					public void onCancel(DialogInterface dialog)
					{
						GetAllFollowersTask.this.cancel(true);
					}
				});
				progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						GetAllFollowersTask.this.cancel(true);
					}
				});
				progressDialog.show();
			}



		}

		private int totalContacts = 0;
		private int countContact = 0;

		private void updateProgressDialog()
		{
			float per=((float)countContact/totalContacts)*100;
			String msg=Math.round(per)+"% contacts imported";
			if(notificationHelper!=null)
			{

				notificationHelper.setContextText(msg);
				notificationHelper.setProgress(Math.round(per));
			}
			else
			{
				if(progressDialog!=null)progressDialog.setMessage(msg);
			}

		}

		@Override
		protected String doInBackground(Void... params)
		{
			try
			{
				IDs ids = twitter.getFriendsIDs(-1);// followersIDs(-1);
				long[] idsLong = ids.getIDs();

				totalContacts = idsLong.length;

				for (int i = 0; i < idsLong.length; i++)
				{
					// DetailClassContact detailClassContact = new DetailClassContact();
					// User user = twitter.showUser(idsLong[i]);
					// detailClassContact.twitter_id = "" + idsLong[i];
					// detailClassContact.name = user.getName();
					// detailClassContact.image_url = user.getProfileImageUrlHttps().toString();
					// detailClassContact.user_description = user.getDescription();
					// detailClassContact.id = i;
					// Global.arrayListForContectList.add(detailClassContact);

					countContact++;
					publishProgress();
					if (isCancelled())
						return "1";


					User user = twitter.showUser(idsLong[i]);
					// LogM.e(idsLong[i]+" : "+user.toString());
					ClassContact classContact = new ClassContact();
					classContact.setContact_id("" + idsLong[i]);
					classContact.setContact_type(Global.CONTACTTYPE_TWITTER);
					classContact.setFull_name(user.getName());

					ClassProfilePicturePath cppp=new ClassProfilePicturePath();
					cppp.setContact_type(classContact.getContact_type());
					cppp.setProfilePicturePath(user.getOriginalProfileImageURLHttps());
					classContact.addprofilePicturePath(cppp);

					classContact.setNotes(user.getDescription());

					ClassProfileLink classProfileLink=new ClassProfileLink();
					classProfileLink.setContact_type(Global.CONTACTTYPE_TWITTER);
					classProfileLink.setProfile_link(PROFILE_INITIAL_URL+user.getScreenName());
					classContact.addProfileLink(classProfileLink);

					classContact.setPrimary_contact_id(contactsDB.getIdForAutoMerge(classContact.getFull_name(),Global.CONTACTTYPE_TWITTER));

					long rowId=contactsDB.insertContact(true, classContact);
					if(rowId>0 && classContact.getPrimary_contact_id()>0)
					{
						countMerge++;
					}
//					LogM.e(user.getProfileImageURLHttps()+" : "+user.getOriginalProfileImageURLHttps()+" : "+user.getBiggerProfileImageURLHttps()+" : "+user.getMiniProfileImageURLHttps());

				}

				return "1";
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
			updateProgressDialog();
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();
			closeDb();

			LogM.e("Canceled");
			mTwHelperListener.onComplete(Global.FRIENDLIST_ABORT_ERROR,countMerge);
		}

		private void closeDb()
		{
			try
			{
				contactsDB.close();
			} catch (Exception e)
			{
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			closeDb();
			dismissProgressDialog();

			if (result != null && !result.equals(""))
			{
				mTwHelperListener.onComplete(Global.FRIENDLIST_SUCESS,countMerge);
			}
			else
			{
				mTwHelperListener.onComplete(Global.FRIENDLIST_ABORT_ERROR,countMerge);
			}

		}

	}
	 */
	/*public void getAllFollowers()
	{
		String token = mTwitterPreferences.getString(PREF_KEY_TOKEN, null);
		String tokenSecret = mTwitterPreferences.getString(PREF_KEY_SECRET, null);
		if (token != null && tokenSecret != null)
		{
			consumer.setTokenWithSecret(token, tokenSecret);

			GetAllFollowersTask allFollowers = new GetAllFollowersTask();
			allFollowers.execute();
		}
		else
		{
			mTwHelperListener.onComplete(null, 0);
		}

	}*/

	private class RetriveAccessTokenTask extends AsyncTask<String, Void, String>
	{

		protected void onPreExecute()
		{
			dismissProgressDialog();
			progressDialog = ProgressDialog.show(mContext, "", "Retrieve login...", true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					RetriveAccessTokenTask.this.cancel(true);
				}
			});
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				provider.retrieveAccessToken(consumer, params[0]);
				saveTokesForFuture();

				return "1";

			} catch (Exception ex)
			{
				//LogM.e("Unable to retrieveAccessToken, exception  " + ex);
				ex.printStackTrace();

				// throw new RuntimeException(ex);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			dismissProgressDialog();

			if(result!=null)
			{
					mTwHelperListener.onComplete(Global.LOGIN_SUCESS);

			}
			else
			{
				mTwHelperListener.onComplete(Global.LOGIN_ABORT_ERROR);
			}
			//			afterLoginSucess();
		}

	}

	private class TweetRequestTask extends AsyncTask<Void, Void, Integer>
	{
		protected void onPreExecute()
		{
			dismissProgressDialog();
			progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					TweetRequestTask.this.cancel(true);
				}
			});
			progressDialog.show();

			//	LogM.e("msg: "+message);
			//.e("msg length: "+message.length());
		}

		@Override
		protected Integer doInBackground(Void... params)
		{
			HttpPost post = new HttpPost("https://api.twitter.com/1.1/statuses/update.json");

			try
			{
				String screenName;
				if (friendId > 0)
				{
					ConfigurationBuilder confbuilder = new ConfigurationBuilder();
					Configuration conf = confbuilder.setOAuthConsumerKey(mContext.getString(R.string.twiiter_con_key)).setOAuthConsumerSecret( mContext.getString(R.string.twitter_con_secret)).setOAuthAccessToken(consumer.getToken())
							.setOAuthAccessTokenSecret(consumer.getTokenSecret()).build();
					Twitter twitter = new TwitterFactory(conf).getInstance();

					User user = twitter.showUser(friendId);
					screenName = user.getScreenName();

					if (screenName != null)
					{
						message = "@" + screenName + " " + message;
					}
				}

				// Set up the tweet contents
				final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
				nvps.add(new BasicNameValuePair("status", message));
				nvps.add(new BasicNameValuePair("image", message));

				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				// set this to avoid 417 error (Expectation Failed)
				post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
				// sign the request
				consumer.sign(post);

				// send the request
				final HttpResponse response = client.execute(post);
				// response status should be 200 OK
				int statusCode = response.getStatusLine().getStatusCode();
				final String reason = response.getStatusLine().getReasonPhrase();

				// release connection
				response.getEntity().consumeContent();
				// Bit weird order, statusCode *can* be checked earlier, is
				// there a special reason?

				if (statusCode != 200)
				{
					//	LogM.e("TwitterConnector failed, statusCode not 200 but " + statusCode + ", reason = " + reason);
				}
				return statusCode;

				// Toast.makeText(context, "Succssfully tweeted!",
				// Toast.LENGTH_SHORT).show();

			} catch (Exception ex)
			{
				// Toast.makeText(context, ex.getMessage(),
				// Toast.LENGTH_SHORT).show();
				ex.printStackTrace();

			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer statusCode)
		{
			super.onPostExecute(statusCode);
			dismissProgressDialog();

			if(isPromotingMsg)
			{
				mTwHelperListener.onComplete(Global.LOGIN_SUCESS);
			}
			else
			{
				if (statusCode == 200)
				{
					mTwHelperListener.onComplete(TWEET_SUCESS);
				}
				else if (statusCode == 403)
				{
					mTwHelperListener.onComplete(TWEET_DUPLICATE);
					return;
				}
				else
				{
					mTwHelperListener.onComplete(TWEET_FAIL);
				}
			}

		}

	}

	private class ImageSender extends AsyncTask<URL, Integer, Integer>
	{
		private String url = "";

		private int result;

		protected void onPreExecute()
		{
			dismissProgressDialog();
			progressDialog = ProgressDialog.show(mContext, "", "Sending image...", true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					ImageSender.this.cancel(true);
				}
			});
			progressDialog.show();
		}

		private Configuration getConfiguration(String apiKey)
		{

			String oauthAccessToken = consumer.getToken();
			String oAuthAccessTokenSecret = consumer.getTokenSecret();

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			confbuilder.setOAuthConsumerKey(mContext.getString(R.string.twiiter_con_key));
			confbuilder.setOAuthConsumerSecret( mContext.getString(R.string.twitter_con_secret));
			confbuilder.setOAuthAccessToken(oauthAccessToken);
			confbuilder.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);
			confbuilder.setMediaProviderAPIKey(apiKey);

			return confbuilder.build();
		}

		protected Integer doInBackground(URL... urls)
		{
			result = 0;

			try
			{

				ImageUploadFactory factory = new ImageUploadFactory(getConfiguration(TWITPIC_KEY));
				ImageUpload upload = factory.getInstance(MediaProvider.TWITPIC);

				url = upload.upload(fileUpload);
				result = 1;

				//LogM.e("Image uploaded, Twitpic url is " + url);

			} catch (Exception e)
			{
				//LogM.e("Failed to send image");

				e.printStackTrace();
			}
			return result;
		}

		protected void onProgressUpdate(Integer... progress)
		{
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();
			result = 0;
		}

		protected void onPostExecute(Integer lon)
		{
			dismissProgressDialog();

			message = message + " " + url;
			new TweetRequestTask().execute();
		}
	}

	
	/**
	 * Post Text on Twitter.
	 * @param message
	 */
	public void postTextTweet(String message)
	{

		new PostTwitterTextTask().execute(message);

	}

	private class PostTwitterTextTask extends AsyncTask<String,Void,Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					PostTwitterTextTask.this.cancel(true);
				}
			});
			progressDialog.show();


		}

		@Override
		protected Void doInBackground(String... params) {

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder.setOAuthConsumerKey(mContext.getString(R.string.twiiter_con_key)).setOAuthConsumerSecret( mContext.getString(R.string.twitter_con_secret)).setOAuthAccessToken(consumer.getToken())
					.setOAuthAccessTokenSecret(consumer.getTokenSecret()).build();
			OAuthAuthorization auth = new OAuthAuthorization(conf);
			Twitter twitter = new TwitterFactory(conf).getInstance(auth);
			
			String post_message=params[0];
			try {
				
				post_message = post_message + " test "+new Random().nextInt();
				
				twitter4j.Status status = twitter.updateStatus(post_message);
				
				long id = status.getId();
				
				if (id != 0) {
					logout();
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissProgressDialog();
		}

	}
	

	/**
	 * Post Image on Twitter.
	 * @param message, fileUrl
	 */
	public void postImageTweet(String message, String fileUrl)
	{
		new PostTwitterImageTask().execute(message, fileUrl);
	}

	private class PostTwitterImageTask extends AsyncTask<String,Void,Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					PostTwitterImageTask.this.cancel(true);
				}
			});
			progressDialog.show();


		}

		@Override
		protected Void doInBackground(String... params) {

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder.setOAuthConsumerKey(mContext.getString(R.string.twiiter_con_key))
					.setOAuthConsumerSecret( mContext.getString(R.string.twitter_con_secret))
					.setOAuthAccessToken(consumer.getToken())
					.setOAuthAccessTokenSecret(consumer.getTokenSecret()).build();
			OAuthAuthorization auth = new OAuthAuthorization(conf);
			ImageUpload upload = new ImageUploadFactory(conf).getInstance(auth);
			
			String post_message=params[0];
			String imagePath = params[1];
			String url = "";
			try {
				if (imagePath.length() > 0) {
					url = upload.upload(new File(imagePath), post_message);
				}else{
					url = upload.upload(null, post_message);
				}
				
				Log.d("Successfully uploaded image to Twitpic at " , url);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissProgressDialog();
		}

	}
	
}
