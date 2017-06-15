package com.tikitoo.android.http.activity;

// ... import

public class ReqActivity extends Activity {

    private EditText nameText, pwdText;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.req_layout);

        nameText = (EditText) findViewById(R.id.edit_text_req_name_id);
        pwdText = (EditText) findViewById(R.id.edit_text_req_pwd_id);
        button = (Button) findViewById(R.id.button_req_id);

        // 按钮绑定监听器
        button.setOnClickListener(new MyBtnListener());
    }

    // 定义监听器
    class MyBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // 获取文本输入框的内容，并将数据传入GetThread
            String name = nameText.getText().toString();
            String pwd = pwdText.getText().toString();

            System.out.println("name = " + name + "; pwd = " + pwd);

            GetThread gt = new GetThread(name, pwd);
            gt.start();
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
            // 获取到数据，使用Get 方式请求，并将两个字段传入
            // http://192.168.56.1:8081/MyWeb/mail.jsp?name=tikitoo&pwd=dfa
            String url = "http://192.168.56.1:8081/MyWeb/mail.jsp?name=" + name + "&pwd=" + pwd;

            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    HttpEntity httpEntity = response.getEntity();
                    InputStream in = httpEntity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String line = null; //br.readLine();

                    // 为了避免第一行为空，读取不到数据，所有使用while 读取所有数据
                    while ((line = br.readLine()) != null) {
                        Log.e("Resp", line);
                    }

                    in.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}