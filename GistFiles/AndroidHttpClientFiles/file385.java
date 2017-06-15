package com.tikitoo.android.http.activity;

// ... import

public class MyActivity extends Activity {

    private Button httpBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        httpBtn = (Button) findViewById(R.id.button_http_id);
        httpBtn.setOnClickListener(new MyBtnListener());

    }

    // 在主线程中调用Network Thread
    class MyBtnListener implements View.OnClickListener {
        public void onClick(View v) {
            new NetWorkThread().start();
        }
    }

    // 访问网络的线程
    class NetWorkThread extends Thread {
        @Override
        public void run() {
            // 创建HttpClient 对象
            HttpClient httpClient = new DefaultHttpClient();

            // 创建代表请求的对象，参数是访问服务器地址，
            HttpGet httpGet = new HttpGet("http://www.marschen.com/data1.html");

            // 自定义请求内容，如果不设置，请求头获取的内容为空
            httpGet.setHeader("name", "Tikitoo");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6,en;q=0.4");


            // 获取请求头（Request Headers）的内容
            Header[] reqHeaders = httpGet.getAllHeaders(); 
            // 请求获取服务器返回的相应对象

            for(Header hd : reqHeaders) {
                String name = hd.getName();
                String value = hd.getValue();
                System.out.println(name + ": " + value);
            }

            try {
                HttpResponse resp = httpClient.execute(httpGet);
                // 检查相应的状态是否正常，状态码为200，则为正常

                // 获取响应头（Response Headers）的内容
                Header[] respHeanders = resp.getAllHeaders();
                for(Header hd : respHeanders) {
                    String name = hd.getName();
                    String value = hd.getValue();
                    System.out.println(name + ": " + value);
                }

                int code = resp.getStatusLine().getStatusCode();
                if (code == 200) {
                    // 从相应对象取出数据
                    HttpEntity entity = resp.getEntity();
                    InputStream in = entity.getContent();

                    // 使用缓存流读取数据
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String line = br.readLine();
                    Log.e("Http", "Data: " + line);

                    // 关闭流资源
                    in.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}