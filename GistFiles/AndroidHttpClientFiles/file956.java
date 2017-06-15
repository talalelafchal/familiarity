/**
 * Based on: http://stackoverflow.com/questions/3505930
 */
private class QueryGooglePlaces extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... args) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(args[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.e("ERROR", e.getMessage());
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }
        return responseString;
    }