
public static List<HashMap<String,String>> selectList(Context con , HashMap<String,String> outMap,HashMap<String,String> inMap) {

        String SELECT_BASIC = " select "+ outMap.get("COLUMNS") + " from " + inMap.get("FROM");
        if(inMap.get("WHERE") != null) {
            SELECT_BASIC = SELECT_BASIC + " WHERE " + inMap.get("WHERE");
        }
        if(inMap.get("ORDERASC") != null) {
            SELECT_BASIC = SELECT_BASIC + " ORDER BY " + inMap.get("ORDERASC") + " ASC";
        }
        if(inMap.get("ORDERDESC") != null) {
            SELECT_BASIC = SELECT_BASIC +" ORDER BY " +  inMap.get("ORDERDESC") + " DESC";
        }
        Log.d(Systems.GAME_TITLE,""+SELECT_BASIC);


        DBAdapter dba = new DBAdapter(con);
        SQLiteDatabase db = dba.getWritableDatabase();

        String sql = SELECT_BASIC;
        Cursor cursor = null;
        cursor = db.rawQuery(sql,null);

        int count = cursor.getCount();
        Log.d(Systems.GAME_TITLE,"SQL_RECORD_COUNT:"+count);
        //取得できなかった場合、nullを返す。
        if(count == 0) {
            return null;
        }
        //結果を格納
        List<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
        while(cursor.moveToNext()){
            //Log.d(Systems.GAME_TITLE,"SQL_COLUMNS_COUNT:"+cursor.getColumnCount());
            count = cursor.getColumnCount();
            HashMap<String,String> tmp = new HashMap<String,String>();
            int i = 0;
            while(true){
                if((count) > i){
                    tmp.put(cursor.getColumnName(i),cursor.getString(i));
                    i++;
                }else{
                    break;
                }
            }
            result.add(tmp);
        }
        //db.commit();
        db.close();

        return result;
    }

