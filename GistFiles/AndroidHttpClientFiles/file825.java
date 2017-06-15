    /** 
     * 画像用POSTリクエスト
     * @param url リクエスト先
     * @param image 送信する画像(バイト配列)
     * @param params 一緒に送るパラメータ
     * @return
     */

    public String postMultipart(String url, byte[] image, String params) {
        HttpClient client = new DefaultHttpClient();
        String str = "";

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setCharset(Charset.forName("UTF-8"));
        try {
            // 画像をセット
            // 第一引数：パラメータ名
            // 第二引数：画像データ
            // 第三引数：画像のタイプ。jpegかpngかは自由
            // 第四引数：画像ファイル名。ホントはContentProvider経由とかで取って来るべきなんだろうけど、今回は見えない部分なのでパス
            entity.addBinaryBody("avater", image, ContentType.create("image/png"), "hoge.png");
            url = "http://example.com/image.json";

            // 画像以外のデータを送る場合はaddTextBodyを使う
            ContentType textContentType = ContentType.create("application/json","UTF-8");
            entity.addTextBody("auth_token", params, textContentType);

            HttpPost post = new HttpPost(url);
            post.setEntity(entity.build());

            HttpResponse httpResponse = client.execute(post);
            str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            Log.i("HTTP status Line", httpResponse.getStatusLine().toString());
            Log.i("HTTP response", new String(str));
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
