public class FeedTask extends AsyncTask<Void, Void, String> {
    double lat;
    double lng;

    public FeedTask(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected String doInBackground(Void... myLocation) {
        try {
            // Create a new HttpClient
            OkHttpClient client = new OkHttpClient();
            // Define request being sent to the server
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
            String currentDateAndTime = sdf.format(new Date());
            RequestBody postData = new FormBody.Builder()
                    .add("time", currentDateAndTime)
                    .add("lat", Double.toString(this.lat))
                    .add("lng", Double.toString(this.lng))
                    .build();

            Request request = new Request.Builder()
                    //.url("http://codemobiles.com/adhoc/feed/youtube_feed.php")
                    .url("http://192.168.1.3/test.php")
                    .post(postData)
                    .build();

            // Transport the request and wait for response to process next
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            finish();
            System.exit(0);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        TextView textView = (TextView) findViewById(R.id.httptest);
        textView.setText(s);
    }
}