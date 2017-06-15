package com.tikitoo.android.http.activity;

// ... import

import java.io.*;

public class MyActivity extends Activity {

    private Button httpBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*
        HttpClient lib
        不可以直接在主线程处理网络, Android 4.0 之后，
        因为手机的网络很慢，连接网络，会阻塞，影响程序运行
        要在 Androidmanifest.xml 中添加访问网络权限
        <uses-permission android:name="android.permission.INTERNET" />

         */


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
            // HttpGet httpGet = new HttpGet("http://www.google.com.hk");
            HttpGet httpGet = new HttpGet("http://www.baidu.com");
            // 请求获取服务器返回的相应对象

            try {
                HttpResponse resp = httpClient.execute(httpGet);
                // 检查相应的状态是否正常，状态码为200，则为正常
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
