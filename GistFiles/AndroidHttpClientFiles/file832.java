/*for the usecase where the files size > 20 MB need to be uploaded to server android throws oom exception, 
The below gist works for files on 25 mb from galaxy nexus . I could see lot of GC when this  code is executed. 
need to go direct via socket to get optimum performance
*/

new AsyncTask<Object, Long, Void>(){

@Override protected Void doInBackground(Object... params) {

 try { 
	URI url = new URI("http://192.168.1.104:8080/25MBfile.dmg"); 
	File file = new File("/sdcard/Download/installgoogledrive.dmg");

 	HttpClient client = new DefaultHttpClient();

 	HttpPost httppost = new HttpPost(url);

 	InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1); 
	reqEntity.setContentType("binary/octet-stream"); reqEntity.setChunked(true);
	httppost.setEntity(reqEntity); HttpResponse response = client.execute(httppost);

 	HttpEntity resEntity =response.getEntity(); 
	if(resEntity !=null){ EntityUtils.toString(resEntity); }

 } catch (Exception e) { e.printStackTrace(); }

 return null; }

 }.execute(1);