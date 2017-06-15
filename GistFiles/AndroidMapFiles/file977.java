package com.PracticaSQLite.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by eder on 29/06/13.
 */
public class AddTodoActivity extends Activity implements OnClickListener {

    private EditText todoText;
    private Button addNewButton;
    private Button backButton;

    private TodoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        dao=new TodoDAO(this);

        todoText=(EditText)findViewById(R.id.newTodoText);
        addNewButton=(Button)findViewById(R.id.addNewTodoButton);
        backButton=(Button)findViewById(R.id.menuGoBackButton);

        addNewButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (addNewButton.isPressed()){
            String todoTextValue=todoText.getText().toString();
            todoText.setText("");

            dao.createTodo(todoTextValue);

            Toast.makeText(getApplicationContext(), "Se ha Agregado una nueva nota!", Toast.LENGTH_LONG).show();
        }else if(backButton.isPressed()){
            Intent intent=new Intent(this, agenda.class);
            startActivity(intent);

            this.finish();

            dao.close();
        }

    }
}
