String url = "http://yourserver";
File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
        "yourfile");
try {
    HttpClient httpclient = new DefaultHttpClient();

    HttpPost httppost = new HttpPost(url);

    InputStreamEntity reqEntity = new InputStreamEntity(
            new FileInputStream(file), -1);
    reqEntity.setContentType("binary/octet-stream");
    reqEntity.setChunked(true); // Send in multiple parts if needed
    httppost.setEntity(reqEntity);
    HttpResponse response = httpclient.execute(httppost);
    //Do something with response...

} catch (Exception e) {
    // show error
}