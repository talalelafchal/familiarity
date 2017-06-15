// GET
try {  
    //得到HttpClient对象  
    HttpClient getClient = new DefaultHttpClient();  
    //得到HttpGet对象  
    HttpGet request = new HttpGet(uri);  
    //客户端使用GET方式执行请教，获得服务器端的回应response  
    HttpResponse response = getClient.execute(request);  
    //判断请求是否成功    
    if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){  
        Log.i(TAG_STRING, "请求服务器端成功");  
        //获得输入流  
        InputStream  inStrem = response.getEntity().getContent();  
        int result = inStrem.read();  
        while (result != -1){  
            System.out.print((char)result);  
            result = inStrem.read();  
        }  
        //关闭输入流  
        inStrem.close();      
    }else {  
        Log.i(TAG_STRING, "请求服务器端失败");  
    }             
} catch (Exception e) {  
    // TODO Auto-generated catch block  
    e.printStackTrace();  
}  

// POST
try {  
  HttpClient client = new DefaultHttpClient();  
  HttpPost request = new HttpPost("http://code.google.com/android/");  
  //使用NameValuePair来保存要传递的Post参数  
  List<NameValuePair> postParameters = new ArrayList<NameValuePair>();  
  //添加要传递的参数    
  postParameters.add(new BasicNameValuePair("id", "12345"));  
  postParameters.add(new BasicNameValuePair("username", "dave"));  
  //实例化UrlEncodedFormEntity对象  
  UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(  
          postParameters);  

  //使用HttpPost对象来设置UrlEncodedFormEntity的Entity  
  request.setEntity(formEntity);  
  HttpResponse response = client.execute(request);  
  in = new BufferedReader(  
          new InputStreamReader(  
                  response.getEntity().getContent()));  

  StringBuffer string = new StringBuffer("");  
  String lineStr = "";  
  while ((lineStr = in.readLine()) != null) {  
      string.append(lineStr + "\n");  
  }  
  in.close();  

  String resultStr = string.toString();  
  System.out.println(resultStr);  
} catch(Exception e) {  
  // Do something about exceptions  
} finally {  
  if (in != null) {  
      try {  
          in.close();  
      } catch (IOException e) {  
          e.printStackTrace();  
      }  
  }  
}  