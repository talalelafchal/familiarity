package com.tuguldur.z.ex7arr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            EditText name = (EditText) findViewById(R.id.name);
            EditText pass = (EditText) findViewById(R.id.pass);
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("pass", pass.getText().toString());
                startActivityForResult(intent, 0);

            }
        });
    }
}
