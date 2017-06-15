private MyHttpPost poster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poster = new MyHttpPost("http://shop.surinrobot.com/web_service/shop_login.php");
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("user_username","demo"));
        data.add(new BasicNameValuePair("user_userpassword","surinrobot"));

        poster.doPost(data,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MyHttpPost.HTTP_POST_OK:
                        Log.d("result",msg.obj.toString());
                        break;
                    case MyHttpPost.HTTP_POST_ERROR:
                        break;
                }
            }
        });
