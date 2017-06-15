
    private void ChangeListView(JSONObject response) {

        try {

            //Jsonデータを取得
            JSONArray count=  response.getJSONArray("SQL_TEST");
            adapter.clear();

            //Jsonデータからリストを作成
            for (int i=0;i<count.length();i++){
                JSONObject data=count.getJSONObject(i);
                adapter.add(data.getString("name")+"\n"+data.get("text"));
            }

            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
                e.printStackTrace();
        }
    }

