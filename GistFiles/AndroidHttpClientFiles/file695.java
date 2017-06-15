public static boolean putImage(Bitmap image) {
        ByteArrayOutputStream baos = null;
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, APP_UA); //UserAgent
        URI uri = null;
        try {
            uri = new URI("http://your.url.to.upload/the/file/via/PUT/123");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpPut put = new HttpPut(uri);

        baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos); //change this according your filetype
        byte[] data = baos.toByteArray();

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayEntity image_byteArray = new ByteArrayEntity(data);
        image_byteArray.setContentType("image/png"); //change this according your filetype

        put.setEntity(image_byteArray);

        HttpResponse response = null;
        try {
            response = httpclient.execute(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            Log.v("MyApplication", "PUT was successful");
            return true;
        } else {
            Log.e("MyApplication", "PUT failed");
            return false;
        }
    }