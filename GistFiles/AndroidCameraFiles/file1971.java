package com.example.andrescelis.thisis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class thisis extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thisis);
//PINTADA
        Button pintada = (Button) findViewById(R.id.pintada);
        assert pintada != null;
        pintada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisis.this, PINTADA.class);
                startActivity(i);
            }
        });

//PINTADA

//TAMESIS
        Button tamesis = (Button) findViewById(R.id.tamesis);
        assert tamesis != null;
        tamesis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisis.this, TAMESIS.class);
                startActivity(i);
            }
        });

//TAMESIS

//JERICO
        Button jerico = (Button) findViewById(R.id.jerico);
        assert jerico != null;
        jerico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisis.this, JERICO.class);
                startActivity(i);
            }
        });

//JERICO

        //VALPARAISO
        Button valparaiso = (Button) findViewById(R.id.valparaiso);
        assert valparaiso != null;
        valparaiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisis.this, VALPARAISO.class);
                startActivity(i);
            }
        });

//VALPARAISO


    }
}
