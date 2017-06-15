

public class TwitterApiClient {
	
	private TwitterApiClient() {}
	
	public abstract class TwitterApiResponseHandler {
		
		private Handler mHandler;
		
		public TwitterApiResponseHandler() {}
		
		public TwitterApiResponseHandler(Looper looper) {
			mHandler = new Handler(looper);
		}

		public void notify(final String responseBody) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						onFinish(responseBody);
					}
				});
			} else {
				onFinish(responseBody);
			}
		}
		public abstract void onFinish(String responseBody);
	}

	private static final HandlerThread WORKER_THREAD = new HandlerThread("TwitterApiClient-Thread");
	private static final Handler WORKER;
	static {
		WORKER_THREAD.start();
		WORKER = new Handler(WORKER_THREAD.getLooper());
	}

	public abstract class GetLinkedTwitterFriendsResponseHandler extends TwitterApiResponseHandler {
		public GetLinkedTwitterFriendsResponseHandler() {
			super();
		}
		public GetLinkedTwitterFriendsResponseHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void onFinish(String responseBody) {
			TwitterFriends friends = TwitterFriends.from(responseBody);
			
			// Get linked friends from twitter friends.
			List<AppUser> appUserList = AppUser.findByIds(friends.getIdList());
			handler.onFinish(appUserList);
		}
		public abstract void onFinish(List<AppUser> appUserList);
	}
	
	public static void getAllLinkedTwitterFriend(final GetLinkedTwitterFriendsResponseHandler handler) {
		get(handler);
	}
	
	private static void get(final TwitterApiResponseHandler handler) {
		WORKER.post(new Runnable() {
			@Override
			public void run() {
				final String responseBody = syncGetAllFriends();
				handler.onFinish(appUserList);
			}
		});
	}

	private static String syncGetAllFriends() {

		String twitterId = ParseTwitterUtils.getTwitter().getId();
		String url = "https://api.twitter.com/1.1/friends/ids.json?user_id=" + twitterId;

		HttpGet verifyGet = new HttpGet(url);
		ParseTwitterUtils.getTwitter().signRequest(verifyGet);

		return execute(verifyGet);
	}
	
	private String execute(HttpGet verifyGet) {
		HttpClient client = new DefaultHttpClient();
		try {
			return client.execute(verifyGet, new ResponseHandler<String>() {
		        @Override
		        public String handleResponse(HttpResponse response)
		                throws ClientProtocolException, IOException {
		            // response.getStatusLine().getStatusCode()でレスポンスコードを判定する。
		            // 正常に通信できた場合、HttpStatus.SC_OK（HTTP 200）となる。
		            switch (response.getStatusLine().getStatusCode()) {
		            case HttpStatus.SC_OK:
		                // レスポンスデータを文字列として取得する。
		                // byte[]として読み出したいときはEntityUtils.toByteArray()を使う。
		                return EntityUtils.toString(response.getEntity(), "UTF-8");
		            
		            case HttpStatus.SC_NOT_FOUND:
		                throw new RuntimeException("データないよ！"); //FIXME
		            
		            default:
		                throw new RuntimeException("なんか通信エラーでた"); //FIXME
		            }
		        }
			});
		} catch (ClientProtocolException e) {
		    throw new RuntimeException(e); //FIXME
		} catch (IOException e) {
		    throw new RuntimeException(e); //FIXME
		} finally {
		    // ここではfinallyでshutdown()しているが、HttpClientを使い回す場合は、
		    // 適切なところで行うこと。当然だがshutdown()したインスタンスは通信できなくなる。
		    httpClient.getConnectionManager().shutdown();
		}
	}
}

