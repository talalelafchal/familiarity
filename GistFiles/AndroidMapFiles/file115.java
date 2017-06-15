public void uriProperties(String uri, Activity act)
  {
		HashMap<Integer, String> e = new HashMap<Integer, String>();
		String nullType = "Null type";
		e.put(0, nullType);
		e.put(1, "Integer type");
		e.put(2, "Float type");
		e.put(3, "String type");
		e.put(4, "Blob type");
		
		HashMap<String, String> cols = new HashMap<String, String>();
		
		Uri u = Uri.parse(uri);
		Cursor c = act.getContentResolver().query(u, null, null, null, null);
		
    	while(c.moveToNext())
    	{
            for (int i = 0; i < c.getColumnCount(); i++)
            {
            	String colName = c.getColumnName(i).toString();
            	String colType = e.get(c.getType(i));
            	
            	if (cols.get(colName)==null){cols.put(colName, colType);}
            	else if (cols.get(colName)==nullType && colType != nullType){cols.put(colName, colType);}
            }
    	}
    	int i = 0;
    	for (Map.Entry<String, String> entry : cols.entrySet()) 
    	{
    	    String key = entry.getKey();
    	    Object value = entry.getValue();
    	    Log.v(uri+" Column-Type "+i,key+" - "+value);
    	    i++;
    	}  	
    	c.close();
		
}