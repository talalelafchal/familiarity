public class Activity1 extends Activity {
        @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
                JSONClient client = new JSONClient(this, l);
		String url = "url that will return JSON";
		
	        client.execute(url);
         }

         GetJSONListener l = new GetJSONListener(){

		@Override
		public void onRemoteCallComplete(JSONObject jsonFromNet) {
			// add code to act on the JSON object that is returned			
		}
		
	};
}