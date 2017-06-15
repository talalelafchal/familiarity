HashMap<String, String> data = new HashMap<String, String>();
data.put("key1", "value1");
data.put("key2", "value2");
AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
asyncHttpPost.setListener(new AsyncHttpPost.Listener(){
    @Override
    public void onResult(String result) {
        // do something, using return value from network
        JSONObject jObject = new JSONObject(result);
        //String from JSON
        String aJsonString = jObject.getString("STRINGNAME");
        
        //Array from JSON
        JSONArray jArray = jObject.getJSONArray("ARRAYNAME");
        
        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String oneObjectsItem = oneObject.getString("STRINGNAMEinTHEarray");
                String oneObjectsItem2 = oneObject.getString("anotherSTRINGNAMEINtheARRAY");
            } catch (JSONException e) {
                // Oops
            }
        }
    }
});
asyncHttpPost.execute("http://example.com");