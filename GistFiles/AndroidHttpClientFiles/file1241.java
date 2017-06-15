/**
     * Uploading the file to server
     * Created by Mostafa Anter on 13/05/16.
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private DonutProgress donutProgress;
        private String filePath;

        public UploadFileToServer(DonutProgress donutProgress, String filePath) {
            this.donutProgress = donutProgress;
            this.filePath = filePath;
        }

        long totalSize = 0;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            donutProgress.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            donutProgress.setVisibility(View.VISIBLE);

            // updating progress bar value
            donutProgress.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://makonway.com/MOW.Service/api/Files/upload");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("file", new FileBody(sourceFile));



                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("upload image", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);
        }

    }