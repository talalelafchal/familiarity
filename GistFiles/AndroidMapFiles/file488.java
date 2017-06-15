public void getBlobsForContainer(String containerName) {
	//Pass the container name as a parameter
	//We have to do it in this way for it to show up properly on the server
	mTableBlobs.execute(mTableBlobs.parameter("container", containerName), new TableJsonQueryCallback() {			
		@Override
		public void onCompleted(JsonElement result, int count, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			JsonArray results = result.getAsJsonArray();
			//Store a local array of both the JsonElements and the blob names
			mBlobNames = new ArrayList<Map<String, String>>();				
			mBlobObjects = new ArrayList<JsonElement>();												
			for (int i = 0; i < results.size(); i ++) {
				JsonElement item = results.get(i);
				mBlobObjects.add(item);
				Map<String, String> map = new HashMap<String, String>();
				map.put("BlobName", item.getAsJsonObject().getAsJsonPrimitive("name").getAsString());					
				mBlobNames.add(map);
			}
			//Broadcast that blobs are loaded
			Intent broadcast = new Intent();
			broadcast.setAction("blobs.loaded");
			mContext.sendBroadcast(broadcast);
		}
	});		
}