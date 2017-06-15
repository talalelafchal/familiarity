Map<String, ArrayList<File>> fileMaps = new HashMap<>();

for (QueryParam queryParam : queryParams) {
    if(queryParam == null) continue;
    String type = queryParam.type;
    if (type.equalsIgnoreCase("string")) {
        requestParams.add(queryParam.key, queryParam.val);
    } else if (type.equalsIgnoreCase("file")) {
        try {
            if(!fileMaps.containsKey(queryParam.key)){
                fileMaps.put(queryParam.key, new ArrayList<File>());
            }
            fileMaps.get(queryParam.key).add(new File(queryParam.val));
            //requestParams.put(queryParam.key, new File(queryParam.val));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

for (String key :
        fileMaps.keySet()) {
    ArrayList<File> files = fileMaps.get(key);
    try {
        requestParams.put(key, files.toArray(new File[files.size()]));
    }catch (Exception e){
        e.printStackTrace();
    }
}