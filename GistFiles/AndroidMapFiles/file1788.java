private void uploadToServer(String imagePath, String imageName) {
        File file = new File(imagePath);
        String url = Config.BASE_UTL+"ReportSystemServices.asmx/postImage";
        if (file.exists()) {
            AndroidNetworking.upload(url)
                    .addMultipartFile("imageFile", file)
                    .addMultipartParameter("imageName", imageName)
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response

                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                        }
                    });
        }
    }