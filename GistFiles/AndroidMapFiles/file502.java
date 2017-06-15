public void getTableRows(String tableName) {
	//Executes a read request with parameters
	//We have to do it in this way to ensure it shows up correctly on the server
	mTableTableRows.execute(mTableTableRows.parameter("table", tableName), new TableJsonQueryCallback() {			
		@Override
		public void onCompleted(JsonElement result, int count, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Loop through the results and add them to our local collection
			JsonArray results = result.getAsJsonArray();				
			mTableRows = new ArrayList<JsonElement>();				
			for (int i = 0; i < results.size(); i ++) {
				JsonElement item = results.get(i);
				mTableRows.add(item);
			}
			//Broadcast that table rows have been loaded
			Intent broadcast = new Intent();
			broadcast.setAction("tablerows.loaded");
			mContext.sendBroadcast(broadcast);
		}
	});		
}