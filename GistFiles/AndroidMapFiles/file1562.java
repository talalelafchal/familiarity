package com.example.suelen.search;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
    private ListView mlistView;
    ArrayAdapter<String> adapter;
    EditText Search;
    ArrayList<HashMap<String, String>> List;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String programmingLanguages[] = {"Android", "Java", "PHP", "IOS", "Python", "Ruby"};
        mlistView = (ListView) findViewById(R.id.List);
        Search = (EditText) findViewById(R.id.Search);
        adapter = new ArrayAdapter<String>(this, R.layout.activity_list_view, R.id.Languages, programmingLanguages);
        mlistView.setAdapter(adapter);
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                MainActivity.this.adapter.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }
}