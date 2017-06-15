	/**
	 * Generates a ddl statement to create a table and executes it
	 * 
	 * @param db
	 * @param tablename
	 * @param columnToType - a mapping of column names to their SQL types in the table
	 */
	private void createTable(SQLiteDatabase db, String tablename, Map<String,String> columnToType){
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE " + tablename +" (");
		
		Iterator<String> iter = columnToType.keySet().iterator();
		while(iter.hasNext()){
			String column = iter.next();
			String type = columnToType.get(column);
			
			builder.append(column).append(" ").append(type);
			if(iter.hasNext()) builder.append(",");
		}
		
		builder.append(");");
		db.execSQL(builder.toString());
		
	}