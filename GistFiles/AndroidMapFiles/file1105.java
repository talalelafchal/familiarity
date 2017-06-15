AjaxCallback<String> callback = new AjaxCallback<String>();        
callback.url("http://www.google.com").type(String.class);
callback.header("Referer", "http://code.google.com/p/android-query/");
callback.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
aQuery.ajax(callback);