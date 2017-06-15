package com.example.wertalp.sensortester;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.example.wertalp.sensortester.CellButton;

import java.util.ArrayList;

/**
 * Created by wertalp on 26.08.15.
 */
public class ChessBoard {

    int heigth   ;
    int width    ;
    Activity activity  ;

    GridLayout board ;
    Display display;
    int laenge ;
    int breite ;
    LayoutInflater inflater;
    PopupWindow pw;
    static int i=0;


    Ship[] ships = new Ship[50];

    Button Butty;
    CellButton[] liCellBut;


    public ChessBoard(Activity activity) {
      this.activity = activity;
        this.display = activity.getWindowManager().getDefaultDisplay();

        this.heigth = display.getHeight();
        this.width  = display.getWidth() ;

    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public void create() {

        int width = display.getWidth();
        int height =display.getHeight();

        board = (GridLayout) activity.findViewById(R.id.board);
        board.removeAllViews();
        Button buttonSet = (Button) activity.findViewById(R.id.button23);
        EditText edit1 = (EditText) activity.findViewById(R.id.editText);

        //board.setColumnCount(Integer.parseInt(edit1.getText().toString()));
        //board.setRowCount(Integer.parseInt(edit1.getText().toString()));
        board.setColumnCount(10);
        board.setRowCount(10);


        laenge = (int) (this.getWidth()-300)/board.getColumnCount();
        breite = laenge ;


        for (int i = 0; i <= board.getRowCount() - 1; i++) {
            for (int j = 0; j <= board.getColumnCount() - 1; j++) {


                CellButton button = new CellButton(activity);
                button.setBackgroundResource(R.drawable.rectangle);
                button.setId(i + j);
                button.setyField(j);
                button.setxField(i);


                //button.setText(""+i+j);

                button.setLayoutParams(new LinearLayout.LayoutParams(laenge, breite));
                board.addView(button);
                //anz++;

                if (i == 0) {
                    button.setText("" + j);
                    button.setBackgroundResource(R.drawable.rectangle_second);

                }
                if (j == 0) {
                    button.setText("" + i);
                    button.setBackgroundResource(R.drawable.rectangle_second);

                }
                if ((i != 0) &&(j !=0)) {

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CellButton b = (CellButton) v;
                        Toast.makeText(getActivity().getBaseContext(), "" + b.getPositionText(), Toast.LENGTH_SHORT).show();
                        b.setBackgroundResource(R.drawable.cross);
                        Butty = b;

                        //Ship cur_Ship = new Ship(board);

                       // show_popup(b);

                    }

                })

                ;};

            }
        }     //  create Methode Fine

    }  // End Create

    public CellButton[] getallcells(){
        int l =0;

        for (int i =0;i<= board.getChildCount();i++){
             View  tmpView = board.getChildAt(i);
           if (tmpView instanceof CellButton){


                 liCellBut[l] = (CellButton) tmpView;
                 l++;
             }

         }

        return liCellBut;



    }
    //private  void show_popup(Button b) {

        //inflater = LayoutInflater.from(activity);
        //pw = new PopupWindow(inflater.inflate(R.layout.ship_choose2, null, false),1200,1600, true);
        //View viewPopup =(View) pw.getContentView();
        //pw.showAtLocation(activity.findViewById(R.id.rel), Gravity.CENTER, 0, 0);


        //add_listener((ViewGroup) viewPopup);

        //ImageButton imgB1 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1a);
        //ImageButton imgB2 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1b);
        //ImageButton imgB3 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1c);
        //ImageButton imgB4 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1d);
        //ImageButton imgB5 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1e);

    //}

    private void add_listener(ViewGroup viewPopup) {

        ViewGroup v1 = (ViewGroup) viewPopup.findViewById(R.id.linView);
        for(int i=0; i < v1.getChildCount(); i++)
        {
            View v = v1.getChildAt(i);
            if (v instanceof ImageButton) {

                ImageButton b = (ImageButton) v;

                b.setOnClickListener(myClickListener);
            }
        }

    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {
        public void onClick(View v) {


            Drawable shippicture = v.getBackground();


            Ship cur_Ship = new Ship(board);
            cur_Ship.add_ship_to_view(Butty, shippicture);
            cur_Ship.name = "testboot_"+i;
            ships[i]= cur_Ship;
            pw.dismiss();
            Toast.makeText(getActivity().getBaseContext(), "" + ships[i].name, Toast.LENGTH_SHORT).show();
            i++;

        }

        ;

    };
// new Board create
//
//

        public Activity getActivity () {
            return activity;
        }

        public void setActivity (Activity activity){
            this.activity = activity;
        };

        }