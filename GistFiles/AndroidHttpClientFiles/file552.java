    public class HttpHelper {  
        public static String doGet(String url) {        
            HttpClient httpclient = new DefaultHttpClient();   
            
            try {         
                HttpResponse response = httpclient.execute(new HttpGet(url));  
                InputStream content = response.getEntity().getContent(); 
                return IOUtils.toString(content, "UTF-8");    
            } catch (IOException e) {}
            
            return "";    
        }
        
        public static void doPost(String url, String json) {    
            HttpClient httpclient = new DefaultHttpClient();  
            
            try {  
                HttpPost post = new HttpPost(url);   
                post.addHeader("Content-Type", "application/json");  
                post.setEntity(new StringEntity(json, HTTP.UTF_8)); 
                httpclient.execute(post);     
            } catch (IOException e) {}  
        }
    }