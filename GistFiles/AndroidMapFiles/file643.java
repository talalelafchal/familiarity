public void getContainers() {
	mTableContainers.where().execute(new TableJsonQueryCallback() {			
		@Override
		public void onCompleted(JsonElement result, int count, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Loop through and build an array of container names
			JsonArray results = result.getAsJsonArray();				
			mContainers = new ArrayList<Map<String, String>>();				
			for (int i = 0; i < results.size(); i ++) {
				JsonElement item = results.get(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("ContainerName", item.getAsJsonObject().getAsJsonPrimitive("name").getAsString());					
				mContainers.add(map);
			}
			//Broadcast that the containers have been loaded
			Intent broadcast = new Intent();
			broadcast.setAction("containers.loaded");
			mContext.sendBroadcast(broadcast);
		}
	});		
}