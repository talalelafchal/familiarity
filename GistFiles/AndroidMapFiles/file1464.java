AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>();           
callback.url("http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0").type(JSONObject.class);
aQuery.sync(callback);
JSONObject result = callback.getResult();
AjaxStatus status = callback.getStatus();