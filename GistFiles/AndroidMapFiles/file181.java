public void getSasForNewBlob(String containerName, String blobName) {
	//Create the json Object we'll send over and fill it with the required
	//id property - otherwise we'll get kicked back
	JsonObject blob = new JsonObject();		
	blob.addProperty("id", 0);
	//Create parameters to pass in the blob details.  We do this with params
	//because it would be stripped out if we put it on the blob object
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("containerName", containerName));
	parameters.add(new Pair<String, String>("blobName", blobName));		
	mTableBlobs.insert(blob, parameters, new TableJsonOperationCallback() {			
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Set the loaded blob
			mLoadedBlob = jsonObject;
			//Broadcast that we are ready to upload the blob data
			Intent broadcast = new Intent();
			broadcast.setAction("blob.created");
			mContext.sendBroadcast(broadcast);
		}
	});
}