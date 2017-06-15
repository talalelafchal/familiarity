public void deleteContainer(String containerName) {
	//Create the json Object we'll send over and fill it with the required
	//id property - otherwise we'll get kicked back
	JsonObject container = new JsonObject();		
	container.addProperty("id", 0);
	//Create parameters to pass in the container details.  We do this with params
	//because it would be stripped out if we put it on the container object
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("containerName", containerName));		
	mTableContainers.delete(container, parameters, new TableDeleteCallback() {			
		@Override
		public void onCompleted(Exception exception, ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch containers from the server
			getContainers();
		}
	});
}