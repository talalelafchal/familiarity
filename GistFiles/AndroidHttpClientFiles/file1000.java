package com.tikitoo.android.httpurl.activity;

// ... import

/**
 * 学习HttpURLConnection
 * 使用HttpURLConnection 访问网络连接，并将返回的对象打印输出
 */
public class ConnActivity extends Activity {

    private static final String TAG = "ConnActivity";

    private Button getHttpURLBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getHttpURLBtn = (Button) findViewById(R.id.button_conn_network_vai_http_url_id);
        getHttpURLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用实现网络的连接的线程
                // 由于Android 4.0 之后，主线程内不能访问网络，所以一个线程实现网络连接
                new MyNetworkThread().start();
            }
        });
    }

    /**
     * 网络连接线程
     */
    class MyNetworkThread extends Thread {

        public MyNetworkThread() {
        }

        @Override
        public void run() {
            // 调用访问URL 的方法
            getHttpURLConn();
        }
    }

    /**
     * 使用HttpURLConnection 访问网络
     */
    public void getHttpURLConn() {
        InputStream in = null;
        HttpURLConnection urlConn = null;
        try {
            // 实例化URL 对象，传入字符串URL
            URL url = new URL("http://www.baidu.com");
            // 通过URL.openConnection() 方法获取URLConnection 对象
            urlConn = (HttpURLConnection) url.openConnection();

            if ( isNetworkAvailable() == true) {
                Log.i(TAG, "Network conn successful");

                // 获取输入流
                in = urlConn.getInputStream();
                // 读取流方法
                readStream(in);
            } else {
                Log.i(TAG, "Network conn error");
            }

            // 关闭连接
            // 
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                urlConn.disconnect();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取流对象，并输出
     * @param in    从HttpURLConnection 对象获取的输入流
     * @throws IOException
     */
    public void readStream(InputStream in) {
        // 读取流对象
        BufferedReader bis = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            while ((line = bis.readLine()) != null) {
                Log.i(TAG, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断网络是否连接
     * @return
     */
    public boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // 如果不为Null，且连接成功，则返回True
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}