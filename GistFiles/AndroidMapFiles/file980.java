        listView = (ListView) findViewById(R.id.listView);

        List<HashMap<String , String>> list = new ArrayList<>();
        //使用List存入HashMap，用來顯示ListView上面的文字。

        String[] title = new String[]{"Apple" , "Banana" , "Cat" , "Dog"};
        String[] text  = new String[]{"蘋果" , "香蕉" , "貓" , "狗"};
        for(int i = 0 ; i < title.length ; i++){
            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("title" , title[i]);
            hashMap.put("text" , text[i]);
            //把title , text存入HashMap之中
            list.add(hashMap);
            //把HashMap存入list之中
        }

        ListAdapter listAdapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2 ,
                new String[]{"title" , "text"} ,
                new int[]{android.R.id.text1 , android.R.id.text2});
        // 5個參數 : context , List , layout , key1 & key2 , text1 & text2

        listView.setAdapter(listAdapter);