package com.example.andrescelis.thisis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TAMESIS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamesis);
//HACER
        Button hacer = (Button) findViewById(R.id.hacert);
        assert hacer != null;
        hacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, HACERT.class);
                startActivity(i);
            }
        });
//HACER


//INFO
        Button info = (Button) findViewById(R.id.infot);
        assert info != null;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, INFOT.class);
                startActivity(i);
            }
        });
//INFO

//DESTINOS
        Button destinos = (Button) findViewById(R.id.destinost);
        assert destinos != null;
        destinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, DESTINOST.class);
                startActivity(i);
            }
        });
//DESTINOS

//GALERIA
        Button galeria = (Button) findViewById(R.id.galeriat);
        assert galeria != null;
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, GALERIAT.class);
                startActivity(i);
            }
        });
//GALERIA

//FESTIVIDADES
        Button festividades = (Button) findViewById(R.id.festividadest);
        assert festividades != null;
        festividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, FESTIVIDADEST.class);
                startActivity(i);
            }
        });
//FESTIVIDADES

//ALIADOS
        Button aliados = (Button) findViewById(R.id.aliadost);
        assert aliados != null;
        aliados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, ALIADOST.class);
                startActivity(i);
            }
        });
//ALIADOS

//COMPARTE
        Button comparte = (Button) findViewById(R.id.compartet);
        assert destinos != null;
        comparte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TAMESIS.this, COMPARTET.class);
                startActivity(i);
            }
        });
//COMPARTE
    }
}
