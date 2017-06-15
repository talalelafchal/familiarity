package com.PracticaSQLite.agenda;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class agenda extends ListActivity {

    private TodoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        dao=new TodoDAO(this);

        setListAdapter(new com.PracticaSQLite.agenda.ListAdapter(this, dao.getTodos()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Todo todo = (Todo)getListAdapter().getItem(position);

        dao.deleteTodo(todo.getId());

        setListAdapter(new com.PracticaSQLite.agenda.ListAdapter(this, dao.getTodos()));

        Toast.makeText(getApplicationContext(), "Nota Eliminada!", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.agenda, menu);
        return true;
    }
    //abrimos la nueva activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent();

        //utilizando un switch esto es util en caso de tener mas elementos en el menu
        switch (item.getItemId()){
            case R.id.menu:
                intent.setClass(this, AddTodoActivity.class);
                startActivity(intent);
                finish();
                dao.close();
                return true;

            default:
                return false;
        }

    }

}
