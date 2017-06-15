public class MainActivity extends ActionBarActivity {

	private EditText nameText;
	private EditText pwdText;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nameText = (EditText) findViewById(R.id.nameText);
		pwdText = (EditText) findViewById(R.id.pwdText);
		button = (Button) findViewById(R.id.submitButton);

		button.setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String name = nameText.getText().toString();
			String pwd = pwdText.getText().toString();

			//使用GET方法向服务器发送请求
//			GetThread gt = new GetThread(name, pwd);
//			gt.start();
			
			//使用POST方法向服务器发送请求
			PostThread pt = new PostThread(name, pwd);
			pt.start();
		}
	}

	//该线程使用POST方法向服务器发送请求
	class PostThread extends Thread {

		String name;
		String pwd;

		public PostThread(String name, String pwd) {
			this.name = name;
			this.pwd = pwd;
		}

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://192.168.1.103:8080/s02e14.jsp";
			//生成使用POST方法的请求对象
			HttpPost httpPost = new HttpPost(url);
			//NameValuePair对象代表了一个需要发往服务器的键值对
			NameValuePair pair1 = new BasicNameValuePair("name", name);
			NameValuePair pair2 = new BasicNameValuePair("password", pwd);
			//将准备好的键值对对象放置在一个List当中
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(pair1);
			pairs.add(pair2);
			try {
				//创建代表请求体的对象
				HttpEntity requestEntity = new UrlEncodedFormEntity(pairs);
				//将请求体放置在请求对象当中
				httpPost.setEntity(requestEntity);
				//执行请求对象
				try {
					HttpResponse response = httpClient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(entity.getContent()));
						String result = reader.readLine();
						Log.d("HTTP", "POST:" + result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetThread extends Thread {

		String name;
		String pwd;

		public GetThread(String name, String pwd) {
			this.name = name;
			this.pwd = pwd;
		}

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://192.168.1.103:8080/s02e14.jsp?name=" + name+ "&password=" + pwd;
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent()));
					String result = reader.readLine();
					Log.d("HTTP", "GET:" + result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}