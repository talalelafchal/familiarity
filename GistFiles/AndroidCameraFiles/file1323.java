package com.example.andrescelis.thisis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PINTADA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintad);

 //HACER
        Button hacer = (Button) findViewById(R.id.hacerp);
        assert hacer != null;
        hacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, HACERP.class);
                startActivity(i);
            }
        });
//HACER


//INFO
        Button info = (Button) findViewById(R.id.infop);
        assert info != null;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, INFOP.class);
                startActivity(i);
            }
        });
//INFO

//DESTINOS
        Button destinos = (Button) findViewById(R.id.destinosp);
        assert destinos != null;
        destinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, DESTINOSP.class);
                startActivity(i);
            }
        });
//DESTINOS

//GALERIA
        Button galeria = (Button) findViewById(R.id.galeriap);
        assert galeria != null;
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, GALERIAP.class);
                startActivity(i);
            }
        });
//GALERIA

//FESTIVIDADES
        Button festividades = (Button) findViewById(R.id.festividadesp);
        assert festividades != null;
        festividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, FESTIVIDADESP.class);
                startActivity(i);
            }
        });
//FESTIVIDADES

//ALIADOS
        Button aliados = (Button) findViewById(R.id.aliadosp);
        assert aliados != null;
        aliados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, ALIADOSP.class);
                startActivity(i);
            }
        });
//ALIADOS

//COMPARTE
        Button comparte = (Button) findViewById(R.id.compartep);
        assert destinos != null;
        comparte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PINTADA.this, COMPARTEP.class);
                startActivity(i);
            }
        });
//COMPARTE
    }
}
