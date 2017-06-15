    HttpClient httpClient = new DefaultHttpClient();
    String urlString = "https://api.zaim.net/v2/home/user/verify";

    HttpGet httpGet = new HttpGet(urlString);
    // HTTP GETリクエストを署名する
    mConsumer.sign(httpGet);

    // HTTP GETリクエストを送信
    ...
