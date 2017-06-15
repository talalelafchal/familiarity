private void UploadData()
	{
		String Data;//你要上傳的資料
		String IP = "http://140.XXX.XX.XX/yourphp.php";//資料庫的IP位址+你要執行的php名稱
		httppost = new HttpPost(IP);
	
		Data = "Hello";
		
		UploadList.add(new BasicNameValuePair("Name_in_android", Data));
		
		Upload = new Thread(SendData);//設定執行緒
		Upload.start();//執行執行緒
	}
	
	//定義連線,傳data的任務
	private Runnable SendData = new Runnable()
	{
        public void run()
        {
        	try
    		{
    			HttpClient httpclient = new DefaultHttpClient();
    			httppost.setEntity(new UrlEncodedFormEntity(UploadList,HTTP.UTF_8));
    			HttpResponse response = httpclient.execute(httppost);
    			HttpEntity entity = response.getEntity();
    			IS = entity.getContent();
    		}
    		catch(Exception e)
    		{
    			Log.e("log_tag", "Error in http connection"+e.toString());//出現error會在logcat產生
    		}
        }
    };