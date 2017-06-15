listView = (ListView) findViewById(R.id.listView);

List<HashMap<String , String>> list = new ArrayList<>();

String[] eng = new String[]{"Apple" , "Banana" , "Cat" , "Dog"};
String[] ch  = new String[]{"蘋果" , "香蕉" , "貓" , "狗"};
for(int i = 0 ; i < eng.length ; i++){
    HashMap<String , String> hashMap = new HashMap<>();
    hashMap.put("eng" , eng[i]);
    hashMap.put("ch" , ch[i]);
    list.add(hashMap);
}

ListAdapter listAdapter = new SimpleAdapter(
        this,
        list,
        android.R.layout.simple_list_item_2 ,
        new String[]{"eng" , "ch"} ,
        new int[]{android.R.id.text1 , android.R.id.text2});

listView.setAdapter(listAdapter);