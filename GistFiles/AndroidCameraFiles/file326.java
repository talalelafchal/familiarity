package com.example.administrator.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/27.
 */
public class EX0409 extends Activity {
    private Button mButton_add;
    private Button mButton_del;
    private Spinner mSpinner;
    private EditText mEditText;
    private ArrayAdapter<String> adapter;
    private List<String> allCountries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex0409);

        mButton_add = (Button) findViewById(R.id.myButton_add);
        mButton_del = (Button) findViewById(R.id.myButton_del);
        mSpinner = (Spinner) findViewById(R.id.mySpinner);
        mEditText = (EditText) findViewById(R.id.myEditText);
        allCountries = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allCountries);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mButton_add.setOnClickListener(myButton);
        mButton_del.setOnClickListener(myButton);

        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mEditText.setText(arg0.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    Button.OnClickListener myButton = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            String newCountry = mEditText.getText().toString();

            /* 新增時, 作的事 */
            if (view.getId() == mButton_add.getId()) {
                for (int i = 0 ; i < adapter.getCount() ; i++) {
                    if (newCountry.equals(adapter.getItem(i))) {
                        /* 跳出重複名稱的提示*/
                        new AlertDialog.Builder(EX0409.this)
                            .setIcon(R.drawable.ic_launcher_camera)
                            .setMessage("名稱重複了")
                            .setPositiveButton("確定", null)
                            .show();

                        return;
                    }
                }

                if (!mEditText.equals("")) {
                    adapter.add(newCountry);
                    /* 取得添加的新城市的位置 */
                    int positon = adapter.getPosition(newCountry);
                    /* 將Spinner選擇在新添加值的位置 */
                    mSpinner.setSelection(positon);
                    mEditText.setText("");
                }

            /* 刪除時, 作的事 */
            } else if (view.getId() == mButton_del.getId()) {
                adapter.remove(mSpinner.getSelectedItem().toString());
                mEditText.setText("");
            }
        }
    };
}
