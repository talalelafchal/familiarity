package com.PracticaSQLite.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.PracticaSQLite.agenda.Todo;
import  com.PracticaSQLite.agenda.TodoSQLiteHelper;


/**
 * Created by eder on 29/06/13.
 */
//nos permite manipular los elementos de la base de datos
public class TodoDAO {
    private SQLiteDatabase db;
    private TodoSQLiteHelper dbHelper;

    public TodoDAO(Context context){
        dbHelper=new TodoSQLiteHelper(context);
        db=dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    //con este realizamos la insercion
    public void createTodo(String todoText){
        ContentValues contentValues=new ContentValues();
        contentValues.put("todo", todoText);
        db.insert("todos", null, contentValues);
    }

    //con este realizamos la eliminacion
    public void deleteTodo(int todoId){
        db.delete("todos", "_id=" + todoId, null);
    }

    //realizamos el listado de los elementos de la base de datos
    public List getTodos(){
        List todoList=new ArrayList();
        String[] tableColumns=new String[]{"_id","todo"};

        Cursor cursor= db.query("todos", tableColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Todo todo=new Todo();
            todo.setId(cursor.getInt(0));
            todo.setText(cursor.getString(1));

            todoList.add(todo);

            cursor.moveToNext();
        }

        return todoList;
    }
}
