public void deleteTable(String tableName) {
	//Create the json Object we'll send over and fill it with the required
	//id property - otherwise we'll get kicked back
	JsonObject table = new JsonObject();		
	table.addProperty("id", 0);
	//Create parameters to pass in the table name.  We do this with params
	//because it would be stripped out if we put it on the table object
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("tableName", tableName));		
	mTableTables.delete(table, parameters, new TableDeleteCallback() {			
		@Override
		public void onCompleted(Exception exception, ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the tables from the server
			getTables();
		}
	});
}