// 使用Post 方式提交
class PostThread extends Thread {
    String name;
    String pwd;

    public PostThread(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    @Override
    public void run() {
        // 构造请求对象
        HttpClient httpClient = new DefaultHttpClient();
        // 获取到数据，使用Post 方式请求
        String url = "http://192.168.56.1:8081/MyWeb/mail.jsp";

        HttpPost httpPost = new HttpPost(url);
        // NameValuePair 对象代表一个需要啊发往服务器的键值对
        NameValuePair namePair = new BasicNameValuePair("name", name);
        NameValuePair pwdPair = new BasicNameValuePair("pwd", pwd);

        // 将准备好的键值对对象 添加到List
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(namePair);
        pairs.add(pwdPair);

        try {
            // 创建代表请求体的对象
            HttpEntity reqEntity = new UrlEncodedFormEntity(pairs); // 可能会出现编码错误异常 
            // 将请求体放在在请求对象当中
            httpPost.setEntity(reqEntity);

            try {
                HttpResponse response = httpClient.execute(httpPost);
                // 获取状态码，200 表示成功
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }// run end
}