package com.example.wertalp.sensortester;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity implements SensorEventListener{

    private TextView txt1,txt2,txt3;
    private ProgressBar progb1, progb2, progb3;
    private ListView listview;
    LayoutInflater   inflater;
    Context    ctx        ;
    Button     buttonSet  ;
    EditText   edit1      ;

    ChessBoard  chessboard  ;
    Ship[]      ships       ;
    PopupWindow pw;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);
            ctx = this.getBaseContext();


        chessboard = new ChessBoard(Main.this) ;
        chessboard.create();


        Display display = getWindowManager().getDefaultDisplay();
          int width = display.getWidth();
          int height =display.getHeight();
        //Toast.makeText(getBaseContext(), "WIDTH HEIGHT: " + width+" : "+height, Toast.LENGTH_LONG).show();

           buttonSet = (Button)   findViewById(R.id.button23);
           edit1     = (EditText) findViewById(R.id.editText);
           Button buttonShoot = (Button) findViewById(R.id.button2) ;

            buttonShoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shootIntent = new Intent(Main.this,ShooterActivity.class);

                    Main.this.startActivity(shootIntent);


                }
            });

           buttonSet.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 chessboard = new ChessBoard(Main.this) ;
                                                 chessboard.create();
                                             }
                                         }

           );

        // inflater = LayoutInflater.from(ctx);
        //createBoard();

    }

    private  void show_popup(Button b) {

        inflater = LayoutInflater.from(this);
        pw = new PopupWindow(inflater.inflate(R.layout.ship_choose2, null, false),1200,1600, true);
        View viewPopup =(View) pw.getContentView();
        pw.showAtLocation(this.findViewById(R.id.rel), Gravity.CENTER, 0, 0);


        add_listener((ViewGroup) viewPopup);

        ImageButton imgB1 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1a);
        ImageButton imgB2 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1b);
        ImageButton imgB3 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1c);
        ImageButton imgB4 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1d);
        ImageButton imgB5 = (ImageButton) viewPopup.findViewById(R.id.imgBShip1e);

    }





    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
