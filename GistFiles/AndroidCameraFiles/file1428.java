package com.riot.projetoriotboothrfid;

import android.util.Log;

import java.util.List;

/**
 * Created by joaopalacio on 03/07/13.
 */
public class MySingleton {

    private int myInt;
    private String myString;
    private boolean internet = false;
    private NetworkTask network; //New instance of NetworkTask
    public List<User> users;
    private static MySingleton instance ;

    private MySingleton() {
        myInt = 0;
        network =  new NetworkTask();
        myString = "";
    }

    public static MySingleton getInstance () {
        if ( MySingleton.instance == null ) {
            MySingleton.instance = new MySingleton();
        }
        return MySingleton.instance;
    }

    public void setInternet(boolean status){
        this.internet = status;
    }

    public NetworkTask getNetwork(){
        return this.network;
    }

    public boolean getInternet(){
        return this.internet;
    }

}