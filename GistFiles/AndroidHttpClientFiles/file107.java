HttpPost httpost = new HttpPost(ZENOSS_INSTANCE + "/zport/dmd/Events/evconsole_router");
httpost.addHeader("Content-type", "application/json; charset=utf-8");
httpost.setHeader("Accept", "application/json");
    	
JSONObject dataContents = new JSONObject();
dataContents.put("evid",_EventID);

JSONArray data = new JSONArray();
data.put(dataContents);
        
JSONObject reqData = new JSONObject();
reqData.put("action", "EventsRouter");
reqData.put("method", "detail");
reqData.put("data", data);
reqData.put("type", "rpc");
reqData.put("tid", String.valueOf(this.reqCount++));
        
httpost.setEntity(new StringEntity(reqData.toString()));

HttpResponse response = httpclient.execute(httpost);
String zenossJSON = EntityUtils.toString(response.getEntity());
response.getEntity().consumeContent();
        
JSONObject ZenossObject = new JSONObject(zenossJSON );