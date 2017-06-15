package com.PracticaSQLite.agenda;

/**
 * Created by eder on 29/06/13.
 */
public class Todo {

    //parametros para la bd
    private int id;
    private String text;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text=text;
    }
}
