//出力カラムの情報
HashMap<String,String> outputInfo = new HashMap<String,String>();
outputInfo.put("COLUMNS","ID,NAME,AGE");
outputInfo.put("FROM","USER_INFO");


//検索条件の情報
HashMap<String,String> searchInfo = new HashMap<String,String>();
searchInfo.put("WHERE","ID>30 AND ID<50");

//検索
HashMap<String,String> resultMap = selectList(activty,outputInfo,searchInfo);

