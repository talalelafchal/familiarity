package com.fbalashov.persistableannotations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
  private int persistedCount = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final TextView textView = (TextView) findViewById(R.id.text_view);
    Button button = (Button) findViewById(R.id.increment_button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        persistedCount++;
        textView.setText("Button clicked " + persistedCount + " times");
      }
    });
    textView.setText("Button clicked " + persistedCount + " times");
  }
}