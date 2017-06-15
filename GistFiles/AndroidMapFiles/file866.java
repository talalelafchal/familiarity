package hw1.cardexc.com.less01_homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List messages = new ArrayList<>();
    private ArrayAdapter adapter;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");

        ListView listView = (ListView) findViewById(R.id.ListView);
        final EditText sendText = (EditText) findViewById(R.id.sendText);
        Button send = (Button) findViewById(R.id.send);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = sendText.getText().toString();

                if ("".equals(message))
                    return;

                messages.add(login + ": " + message);
                sendText.setText("");

                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            messages.clear();
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
