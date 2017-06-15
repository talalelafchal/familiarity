public Void getid(){
        String result="";
        InputStream isr=null;
        JSONObject jObj = null;
        try{

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://10.0.2.2:8080/my%20project/first.php");
            List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(1);

            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,

            // HttpResponse httpResponse=httpClient.execute(httpPost1);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
            //Execute HTTP Post Request
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity entity=httpResponse.getEntity();
            isr=entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Error in http conection" + e.toString());
            t.setText("couldnt connect");
        }
        try{
            BufferedReader reader=new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while ((line=reader.readLine())!=null){

                sb.append(line+ "\n");
            }
            isr.close();
            result=sb.toString();

        }catch (Exception e){
            Log.e("log_tag","Error convert"+e.toString());
            t.setText(" connect no");
        }
        try{

            JSONArray jsonArray=new JSONArray(result);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject json = jsonArray.getJSONObject(i);

                s=s+  "Name : "+json.getString("_id")+"\n";
            }
            t.setText(s);






        }catch (Exception e){
            Log.e("log_tag","Error convert"+e.toString());
            t.setText("json r connect");
        }

        return null;

    }