public static String connectGET(String url)
    {
 
        HttpClient httpclient = new DefaultHttpClient();
        String sRes = "";
 
        // Prepare a request object
        HttpGet httpget = new HttpGet(url);
 
        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
 
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                sRes = result;
                // now you have the string representation of the HTML request
                instream.close();
            }
 
 
        } catch (Exception e)
        {
            Log.w("[CHECK]", e.toString());
        }
        return(sRes);
    }
 

public static String connectGETHTTPURLConnection(String purl) throws IOException
    {
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";
        try{
            HttpURLConnection httpurlconnection = (HttpURLConnection) urlConnection;
            httpurlconnection.setRequestMethod("GET");
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();
 
            if(httpurlconnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                // A Simple JSON Response Read
                InputStream instream = httpurlconnection.getInputStream();
                String result= convertStreamToString(instream);
                Log.w("[CHECK]",result);
                sRes = result;
                // now you have the string representation of the HTML request
                instream.close();
            }
        }
        catch(Exception e)
        {
            Log.w("[CHECK]", e.toString());
        }
 
        return sRes;
    }