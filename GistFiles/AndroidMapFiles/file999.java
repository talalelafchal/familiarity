public void updateTableRow(final String tableName, List<Pair<String,String>> tableRowData) {
	//Create a new json object with the key value pairs
	JsonObject newRow = new JsonObject();
	for (Pair<String,String> pair : tableRowData) {
		newRow.addProperty(pair.first, pair.second);
	}
	//Add ID Parameter since it's required on the server side
	newRow.addProperty("id", 1);
	//Pass the table name over in parameters
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("table", tableName));		
	mTableTableRows.update(newRow, parameters, new TableJsonOperationCallback() {			
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the table rows
			getTableRows(tableName);
		}
	});
}