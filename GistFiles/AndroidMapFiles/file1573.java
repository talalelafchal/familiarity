public void addTableRow(final String tableName, List<Pair<String,String>> tableRowData) {
	//Create a new json object with the key value pairs
	JsonObject newRow = new JsonObject();
	for (Pair<String,String> pair : tableRowData) {
		newRow.addProperty(pair.first, pair.second);
	}
	//Pass the table name over in parameters
	List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
	parameters.add(new Pair<String, String>("table", tableName));		
	mTableTableRows.insert(newRow, parameters, new TableJsonOperationCallback() {			
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the table rows from the server
			getTableRows(tableName);
		}
	});
}