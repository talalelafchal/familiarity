    public static Bitmap getGoogleMapThumbnail(double lati, double longi){
        String URL = "http://maps.google.com/maps/api/staticmap?center=" +lati + "," + longi + "&zoom=15&size=200x200&sensor=false";
        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();   
        HttpGet request = new HttpGet(URL); 

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;
    }