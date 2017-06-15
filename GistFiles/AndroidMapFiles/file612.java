public void addContainer(String containerName, boolean isPublic) {
	//Creating a json object with the container name
	JsonObject newContainer = new JsonObject();
	newContainer.addProperty("containerName", containerName);
	//Passing over the public flag as a parameter
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("isPublic", isPublic ? "1" : "0"));		
	mTableContainers.insert(newContainer, parameters, new TableJsonOperationCallback() {			
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the containers from the server
			getContainers();
		}
	});		
}