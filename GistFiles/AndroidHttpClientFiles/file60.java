package com.amtera.crudapp;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amtera.dao.UserDAO;
import com.amtera.domain.User;

import java.util.ArrayList;


/**
 * Created by frodrigues on 3/21/14.
 */
public class ListUserActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        UserDAO u = new UserDAO(this.getApplicationContext());
        ArrayList<User> users = u.listAllUser();
        ArrayList<String> users_login = new ArrayList<String>();

        for (int i=0; i < users.size()-1; i++){
            users_login.add(users.get(i).getLogin());
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users_login));


    }

}