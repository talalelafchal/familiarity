package com.example.miguel.hechos_curiosos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;


public class HechosCuriosos extends Activity {
    final static String TAG = HechosCuriosos.class.getSimpleName();
    FactBook fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hechos_curiosos);
        fb = new FactBook();
        Log.v(TAG, "Hoy aprenderás cosas nuevas");

    }
    public void factButtonAction(View v){
        final TextView factLabel = (TextView) findViewById(R.id.HechoCuriosoTextView);
        String fact = "";
        fact= fb.getRandomFact(v.getContext());
        factLabel.setText(fact);

        changeBackgroundColor(v);

    }

    public int ColorWheel(Context cont){
        Random randomGenerator = new Random();
        int col;
        String color;
        String[] hechos = cont.getResources().getStringArray(
                R.array.color);
        int randomNumber = randomGenerator.nextInt(hechos.length);
        color = hechos[randomNumber];
        col= Color.parseColor(color);
        return col;
    }

    public void changeBackgroundColor(View v){
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeMain);
        Button btn = (Button)findViewById(R.id.button);

        rl.setBackgroundColor(ColorWheel(v.getContext()));
        btn.setTextColor(ColorWheel(v.getContext()));
    }
}
