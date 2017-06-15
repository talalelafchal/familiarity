package com.PracticaSQLite.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



/**
 * Created by eder on 29/06/13.
 */
public class ListAdapter extends ArrayAdapter<Todo> {

    private final Context context;
    private final List<Todo> todoList;

    public ListAdapter(Context context, List<Todo> todoList){
        super(context, R.layout.activity_main, todoList);
        this.context = context;
        this.todoList=todoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView=inflater.inflate(R.layout.activity_main, parent, false);

        TextView todoText=(TextView) rowView.findViewById(R.id.todoText);
        todoText.setText(todoList.get(position).getText());
        return rowView;
    }
}

