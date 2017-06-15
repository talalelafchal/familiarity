/**
* Sends a multipart request to the server, uploading the user's profile picture.
*/
public void uploadCardImage(File file, Context context, Callback callback) {
    String authorizationPrefix = "Bearer ";
    String url = accountService.buildUri(URL_UPLOAD_CARD_IMAGE);
    
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.getName(),
            RequestBody.create(MEDIA_TYPE_IMAGE, file))
                .addFormDataPart("login", userLogin)
                .build();
    
    Request request = new Request.Builder()
        .addHeader("Authorization", authorizationPrefix + accessToken)
        .url(url)
        .post(requestBody)
        .build();
    // Response response = httpClient.newCall(request).execute();
    httpClient.newCall(request).enqueue(callback);
}