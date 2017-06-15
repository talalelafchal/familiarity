public void addTable(String tableName) {
	JsonObject newTable = new JsonObject();
	newTable.addProperty("tableName", tableName);		
	mTableTables.insert(newTable, new TableJsonOperationCallback() {			
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception,
				ServiceFilterResponse response) {
			if (exception != null) {
				Log.e(TAG, exception.getCause().getMessage());
				return;
			}
			//Refetch the tables from the server
			getTables();
		}
	});
}