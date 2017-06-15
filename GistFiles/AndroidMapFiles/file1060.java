package de.databasetemplate.db;

import android.content.ContentValues;
import android.content.Context;

public class IdentitiesDb extends AbstractDb{

	public static final String TABLE_NAME = "identities";
	public static final String COL_USER   = "user";
	public static final String COL_PW     = "pw";
	
	public static final String[] COLUMNS = {
		COL_ID, COL_USER, COL_PW
	};
	
	public IdentitiesDb(Context context) {
		super(context);
	}
	
	protected String[] getColumns() {
		return COLUMNS;
	}
	
	protected String getTableName() {
		return TABLE_NAME;
	}
	
	protected String getLogTag() {
		return "MY_APP";
	}
	
    protected boolean isStringColumn(String c) {
    	return (c == COL_USER || c == COL_PW);
    }
    
    protected boolean isIntegerColumn(String c) {
    	return (c == COL_ID);
    }
    
    public void createTestIdentities() {
    	
    	ContentValues v1 = new ContentValues();
    	v1.put(COL_USER, "hugo");
    	v1.put(COL_PW, "foobar");
    	database.insert(TABLE_NAME, null, v1);
    	
    	ContentValues v2 = new ContentValues();
    	v2.put(COL_USER, "testuser");
    	v2.put(COL_PW, "123456");
    	database.insert(TABLE_NAME, null, v2);
    }
	
}
