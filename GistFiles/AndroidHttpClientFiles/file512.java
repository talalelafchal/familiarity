HttpClient httpClient = new DefaultHttpClient();
HttpPost httpPost = new HttpPost(
    "https://api.dailymile.com/entries.json?oauth_token=" 
    + token);
 
httpPost.setHeader("content-type", "application/json");
JSONObject data = new JSONObject();
 
data.put("message", dailyMilePost.getMessage());
JSONObject workoutData = new JSONObject();
data.put("workout", workoutData);
workoutData.put("activity_type", dailyMilePost.getActivityType());
workoutData.put("completed_at", dailyMilePost.getCompletedAt());
JSONObject distanceData = new JSONObject();
workoutData.put("distance", distanceData);
distanceData.put("value", dailyMilePost.getDistanceValue());
distanceData.put("units", dailyMilePost.getDistanceUnits());
workoutData.put("duration", dailyMilePost.getDurationInSeconds());
workoutData.put("title", dailyMilePost.getTitle());
workoutData.put("felt", dailyMilePost.getFelt());
 
StringEntity entity = new StringEntity(data.toString());
httpPost.setEntity(entity);
 
HttpResponse response = httpClient.execute(httpPost);
