JSONArray position = new JSONArray();
position.put(32L);
 
System.out.println("before pub: " + position);
pubnub.publish()
    .message(toList(position))
    .channel("my_channel")
    .async(new PNCallback<PNPublishResult>() {
        @Override
        public void onResponse(PNPublishResult result, PNStatus status) {
            //  handle publish result, status always present, result if successful
            //  status.isError to see if error happened
            if(!status.isError()) {
                System.out.println("pub timetoken: " + result.getTimetoken());
            }
            System.out.println("pub status code: " + status.getStatusCode());
        }
    });

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }