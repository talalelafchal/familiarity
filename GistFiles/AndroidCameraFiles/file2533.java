package de.vogella.android.listview3d;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
	final static int ELEMENT_COUNT = 400;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        ListView list = (ListView) findViewById(R.id.listViewStandart);
        String[] elements = new String[ELEMENT_COUNT];
        for (int i = 0; i< ELEMENT_COUNT; i++) {
        	elements[i] = String.valueOf(i);
        }

        AdapterAnimation main_adapter = new AdapterAnimation(this,elements);
        list.setAdapter(main_adapter);
        /*MyAdapter adapter = new MyAdapter(this,elements);
        list.setAdapter(adapter);*/
    }
}

