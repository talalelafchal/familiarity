package net.nessness.android.sample.listview;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListViewSampleActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        String[] from = new String[]{"test"};
        int[] to = new int[]{android.R.id.text1};

        for(int i = 0; i < 10; i++){
            HashMap<String, String> m = new HashMap<String, String>();
            m.put(from[0], "data " + i);
            data.add(m);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_1, from, to);

        // @id/android:list (android.R.id.list) の ListViewにアダプターをセット
        setListAdapter(adapter);

        // R.id.mylist のListViewにアダプターをセット
        ((ListView)findViewById(R.id.mylist)).setAdapter(adapter);
    }
}