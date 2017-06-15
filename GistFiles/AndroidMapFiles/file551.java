AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>();           
callback.url("http://www.androidquery.com/p/doNothing").type(JSONObject.class);
callback.cookie("hello", "world").cookie("foo", "bar");
aQuery.ajax(callback);