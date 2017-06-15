package com.example.administrator.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/9/26.
 */
public class MyListView extends ListActivity {

    //預先定義順序參數
    protected static final int MyListView_camera = 0;
    protected static final int MyListView_album = 1;
    protected static final int MyListView_map = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist);

        /*設定ListView未取得內容時, 顯示的view, empty建構在list.xml中*/
        getListView().setEmptyView(findViewById(R.id.empty));

        /*自訂方法載入ListView值*/
        fillDate();
    }

    /*當ListView的項目被按下*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //由觸發的View物件v(按下的那一刻), 取得adapter的checkbox
        CheckBox cbx = (CheckBox) v.findViewById(R.id.myCheckBox);
        //取得adapter的textView
        TextView title = (TextView) v.findViewById(R.id.myTextView);

        if (cbx.isChecked()) {
            cbx.setChecked(false);
            Toast.makeText(this, title.getText().toString() + "已取消核取", Toast.LENGTH_SHORT).show();
        } else {
            cbx.setChecked(true);
            Toast.makeText(this, title.getText().toString() + "已核取", Toast.LENGTH_SHORT).show();
        }

        super.onListItemClick(l, v, position, id);
    }

    void fillDate() {
        /*從res string.xml取出所需的字串陣列*/
        CharSequence[] list = getResources().getStringArray(R.array.adapter_list);
        setListAdapter(new MyAdapter(this, list));
    }
}
