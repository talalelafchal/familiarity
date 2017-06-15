public void getTables() {
    mTableTables.where().execute(new TableJsonQueryCallback() {			
		@Override
		public void onCompleted(JsonElement result, int count, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			JsonArray results = result.getAsJsonArray();
			
			mTables = new ArrayList<Map<String, String>>();
			//Loop through the results and get the name of each table
			for (int i = 0; i < results.size(); i ++) {
				JsonElement item = results.get(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("TableName", item.getAsJsonObject().getAsJsonPrimitive("TableName").getAsString());					
				mTables.add(map);
			}
			//Broadcast that tables have been loaded
			Intent broadcast = new Intent();
			broadcast.setAction("tables.loaded");
			mContext.sendBroadcast(broadcast);
		}
	});		
}