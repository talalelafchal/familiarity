/**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
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
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
