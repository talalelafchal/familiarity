package com.example.wertalp.sensortester;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

/**
 * Created by wertalp on 26.08.15.
 */
public class Ship {
    Context ctx;
    Button button;
    String name;
    Point position;
    Shape  shape;
    Integer hSpan = 1;
    Integer vSpan = 1;
    GridLayout board;

    public Ship(Button button, String name, Point position, Shape shape) {
        this.button = button;
        this.name = name;
        this.position = position;
        this.shape = shape;
    }

    public Ship(GridLayout board)
    {
        this.board = board;
    }

    public void add_ship_to_view(Button but,Drawable shippicture){

        but.setBackground(shippicture);
    }






}
// new Ship.