package cn.zhaochunqi.testadapter.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 6/26/14.
 */
public class TestSimpleAdapter extends ActionBarActivity {

    private ListView lv_;
    private List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();
    private ListView lv_simpleAdaptr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_simpleadapter);
        lv_simpleAdaptr = (ListView) findViewById(R.id.lv_testsimpleadapter);
        createList();
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listdata, R.layout.view_listview_simple, new String[]{"name", "number", "hello"}, new int[]{R.id.tv_name, R.id.tv_number, R.id.tv_hello});
        lv_simpleAdaptr.setAdapter(simpleAdapter);
    }

    /**
     * 创建需要的相应的list表
     */
    private void createList() {

        for (int i = 0; i < 100; i++) {
//            for(int j=0;j<5;j++) {
            Map<String, String> tempmap = new HashMap<String, String>();
            String s = "My name is " + i + "th";
            String num = i + "th";
            tempmap.put("name", s);
            tempmap.put("number", num);
            tempmap.put("hello", "hello");
            listdata.add(tempmap);
//            }
        }

    }


}
