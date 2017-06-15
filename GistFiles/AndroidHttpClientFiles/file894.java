/*
 In build.gradle under dependencies add
    compile group: 'cz.msebera.android' , name: 'httpclient', version: '4.4.1.1'
*/

class addUser extends AsyncTask<JSONObject, Void, String> {

    @Override
    protected String doInBackground(JSONObject... params) {

        HttpClient httpClient = HttpClients.createDefault();

        try {
            URI uri = new URIBuilder("https://automatic-report.herokuapp.com/db/new").build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            
            StringRequestEntity json = new StringRequestEntity(params[0].toString());
            request.setEntity(json);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                    //You can maybe do a Toast here or do that in onPostExecute()
                    Log.d("addUser", EntityUtils.toString(entity));
                    return EntityUtils.toString(entity);
            }
         } catch (Exception e) {
            Log.d("addUserError", e.toString());
         }

         return null;
    }
}

/*  Example use
    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("name", name);
        jsonObject.put("uuid", uuid);
        jsonObject.put("address", address);
        jsonObject.put("car", carmodel);

        new addUser().execute(jsonObject);

    } catch (JSONException e) {
        // JSON creation failed
    }
    
 */