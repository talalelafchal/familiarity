public void deleteBlob(final String containerName, String blobName) {
	//Create the json Object we'll send over and fill it with the required
	//id property - otherwise we'll get kicked back
	JsonObject blob = new JsonObject();		
	blob.addProperty("id", 0);
	//Create parameters to pass in the blob details.  We do this with params
	//because it would be stripped out if we put it on the blob object
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("containerName", containerName));
	parameters.add(new Pair<String, String>("blobName", blobName));		
	mTableBlobs.delete(blob, parameters, new TableDeleteCallback() {			
		@Override
		public void onCompleted(Exception exception, ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the blobs from the server
			getBlobsForContainer(containerName);
		}
	});
}