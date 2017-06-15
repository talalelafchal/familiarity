public void deleteTableRow(final String tableName, String partitionKey, String rowKey) {
	//Create the json Object we'll send over and fill it with the required
	//id property - otherwise we'll get kicked back
	JsonObject row = new JsonObject();		
	row.addProperty("id", 0);
	//Create parameters to pass in the table row details.  We do this with params
	//because it would be stripped out if we put it on the row object
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("tableName", tableName));
	parameters.add(new Pair<String, String>("rowKey", rowKey));
	parameters.add(new Pair<String, String>("partitionKey", partitionKey));		
	mTableTableRows.delete(row, parameters, new TableDeleteCallback() {			
		@Override
		public void onCompleted(Exception exception, ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the table rows for the table
			getTableRows(tableName);
		}
	});
}