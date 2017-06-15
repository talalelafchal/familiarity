//import stuff

public void sendPOST(final String url, final ArrayList<NameValuePair> params){
    Thread t = new Thread(new Runnable(){
        @Override
        public void run(){
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = url;
                HttpPost post = new HttpPost(postURL);
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    Log.i("POST RESPONSE",EntityUtils.toString(resEntity));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    t.start();
}

public void sendGET(final String url){
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                HttpClient client = new DefaultHttpClient();
                String getURL = url;
                HttpGet get = new HttpGet(getURL);
                HttpResponse responseGet = client.execute(get);
                HttpEntity resEntityGet = responseGet.getEntity();
                if (resEntityGet != null) {
                    Log.i("GET RESPONSE", EntityUtils.toString(resEntityGet));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    t.start();
}