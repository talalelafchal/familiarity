package com.example.ed.todolist;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MyActivity extends Activity {
    ListView lvItems;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    File fileDir;
    File todoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        lvItems = (ListView) findViewById(R.id.lView);
        readItems();

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

    }

    private void writeItems() {
        createFile();

        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void readItems() {
        createFile();

        try {
            items = new ArrayList<String>(FileUtils.readLines((todoFile)));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }


    private void createFile() {
        fileDir = getFilesDir();
        todoFile = new File(fileDir, "todo.txt");
    }


    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
    }

    public void onAddItem(View v) {

        EditText etNewItem = (EditText) findViewById(R.id.etTodoList);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        writeItems();
        etNewItem.setText("");
    }


}
