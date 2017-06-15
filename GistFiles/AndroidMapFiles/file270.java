public class ServerRequest {

  public static boolean isNetworkConnected(Context ctx) {
    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    return ni != null;
  }
  
  private static String getUrlEncodeData(HashMap<String, String> params) {
    if(params == null){
      return "";
    }
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for(Map.Entry<String, String> entry : params.entrySet()){
      try {
        if (first)
          first = false;
        else
          result.append("&");
        
        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
      }
      catch(Exception e){e.printStackTrace();}
    }
    return result.toString();
  }
  
  public String httpGetData(String url){
    return httpGetData(url, null);
    }
    public static String httpGetData(String url, HashMap<String, String> params){
    Log.d("ServerRequest", "GET :: url = " + url + "?" + getUrlEncodeData(params));
    StringBuffer response = new StringBuffer();
    try {
      URLConnection conn = new URL(url).openConnection();
      conn.setDoOutput(true);
      conn.setDoInput(true);
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(getUrlEncodeData(params));
      writer.flush();
      writer.close();
      
      BufferedReader reader = new BufferedReader(new
      InputStreamReader(conn.getInputStream()));
      String inputLine;
    
      while ((inputLine = reader.readLine()) != null) {
      response.append(inputLine);
      }
      reader.close();
    }
    catch(Exception e){e.printStackTrace();}
    Log.d("ServerRequest", "GET :: Response : " + response.toString());
    return response.toString();
  }
  
  public static String httpPostData(String url, HashMap<String, String> params){
    Log.d("ServerRequest", "POST :: url = " + url + "?" + getUrlEncodeData(params));
    StringBuffer response = new StringBuffer();
    try {
      URL u = new URL(url);
      HttpURLConnection conn = (HttpURLConnection)u.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      
      writer.write(getUrlEncodeData(params));
      writer.flush();
      writer.close();
      
      BufferedReader reader = new BufferedReader(new
      InputStreamReader(conn.getInputStream()));
      String inputLine;
      
      while ((inputLine = reader.readLine()) != null) {
        response.append(inputLine);
      }
      reader.close();
    }
    catch(Exception e){e.printStackTrace();}
    Log.d("ServerRequest", "POST :: Response = " + response.toString());
    return response.toString();
  }
}