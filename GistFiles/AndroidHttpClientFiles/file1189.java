HttpResponse response = httpClient.execute(httpPost);
String responseString = EntityUtils.toString(response.getEntity());
JSONObject responseJSON = new JSONObject(responseString);
int workoutId = responseJSON.getInt("id");
