/*
 * App crashes if you don't make a separate thread to send your http requests. 
 * This example uses a new thread to send http requests and hence won't crash the application.
 */
Random generator = new Random();
            int i = generator.nextInt(6) + 1;


            List<NameValuePair> pairs = new ArrayList<NameValuePair>();

            if(i==1){
                pairs.add(new BasicNameValuePair("busno", "356C"));
            }

            if(i==2){
                pairs.add(new BasicNameValuePair("busno", "KBS3E"));
            }

            if(i==3){
                pairs.add(new BasicNameValuePair("busno", "402B"));
            }
            if(i==4){
                pairs.add(new BasicNameValuePair("busno", "G-9"));
            }

            if(i==5){
                pairs.add(new BasicNameValuePair("busno", "KBS3A"));
            }

            if(i==6){
                pairs.add(new BasicNameValuePair("busno", "284D"));
            }

            pairs.add(new BasicNameValuePair("temp", Float.toString(value)));
            pairs.add(new BasicNameValuePair("pressure", "30"));
            pairs.add(new BasicNameValuePair("stop", "1"));
            pairs.add(new BasicNameValuePair("humidity", "2"));
            pairs.add(new BasicNameValuePair("key2", "value2"));

            class OneShotTask implements Runnable {
                List<NameValuePair> ls;
                OneShotTask(List<NameValuePair> s) { ls = s;};
                public void run() {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.43.8/wiced/wic/submitdata");
                    try{
                        post.setEntity(new UrlEncodedFormEntity(ls));
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                    try {
                        HttpResponse response = client.execute(post);
                    } catch (IOException e) {
                        e.printStackTrace();
                        String tag = new String("custom");
                        Log.e(tag, "Exception till here ");
                    }
                }
            }
            Thread t = new Thread(new OneShotTask(pairs));
            t.start();