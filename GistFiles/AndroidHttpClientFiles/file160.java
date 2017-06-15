JSONObject jsonobject = new JSONObject();
HttpClient httpclient = new DefaultHttpClient();
HttpPost httppost = new HttpPost("http://*******/apirestfull/******/");		    

httppost.setHeader("Content-Type","application/json");
httppost.setHeader("Authentication", "Token 0d0c898a*****e8bd");

jsonobject.put("nome", "Guilherme");
jsonobject.put("pais", "Brasil");
jsonobject.put("chave", Key);

httppost.setEntity(new StringEntity(jsonobject.toString(), "UTF-8"));

// Execute HTTP Post Request
HttpResponse response = httpclient.execute(httppost);	
Log.i("Resposta", response.getStatusLine().toString());