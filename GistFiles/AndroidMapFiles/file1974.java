        String[] texts = {"sometext 1", "sometext 2", "sometext 3"};

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(texts.length);
        Map<String, Object> m;
        for (int i = 0; i < texts.length; i++) {
            m = new HashMap<String, Object>();
            m.put("TEXT", texts[i]);
            data.add(m);
        }

        String[] from = {"TEXT"};
        int[] to = { android.R.id.text1};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_1, from, to);

        ListView mListView = (ListView) findViewById(R.id.participants);
        mListView.setAdapter(sAdapter);