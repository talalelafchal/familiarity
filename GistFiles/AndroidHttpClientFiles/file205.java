interface IHttpResponseListener {
    public void onRemoteCallComplete(int statusCode, String json);
}

class RequestGet extends AsyncTask<Void, Void, Boolean> {
    private IHttpResponseListener listener = null;
    private Context context;
    private int statusCode = 0;
    private String json = "";
    public RequestGet(Context context, IHttpResponseListener listener){
        this.context = context;
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        // handle pre execution
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            statusCode = statusLine.getStatusCode();
            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(entity);
            if (status_code < HttpStatus.SC_BAD_REQUEST) {
                return true;
            } else {
                return false;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        // hide if progress dialog is showing
        // do something if < 400 occurs
        listener.onRemoteCallComplete(statusCode, json);
    }
}
    
// now call from activity like
/*new RequestGet(ActivityClass.this, new IHttpResponseListener(){
    @Override
    public void onRemoteCallComplete(int statusCode, String json){
        // change the textview text from here
    }
}).execute();*/