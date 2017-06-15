package com.example.andrescelis.thisis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VALPARAISO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valparaiso);
//HACER
        Button hacer = (Button) findViewById(R.id.hacerv);
        assert hacer != null;
        hacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, HACERV.class);
                startActivity(i);
            }
        });
//HACER


//INFO
        Button info = (Button) findViewById(R.id.infov);
        assert info != null;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, INFOV.class);
                startActivity(i);
            }
        });
//INFO

//DESTINOS
        Button destinos = (Button) findViewById(R.id.destinosv);
        assert destinos != null;
        destinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, DESTINOSV.class);
                startActivity(i);
            }
        });
//DESTINOS

//GALERIA
        Button galeria = (Button) findViewById(R.id.galeriav);
        assert galeria != null;
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, GALERIAV.class);
                startActivity(i);
            }
        });
//GALERIA

//FESTIVIDADES
        Button festividades = (Button) findViewById(R.id.festividadesv);
        assert festividades != null;
        festividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, FESTIVIDADESV.class);
                startActivity(i);
            }
        });
//FESTIVIDADES

//ALIADOS
        Button aliados = (Button) findViewById(R.id.aliadosv);
        assert aliados != null;
        aliados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, ALIADOSV.class);
                startActivity(i);
            }
        });
//ALIADOS

//COMPARTE
        Button comparte = (Button) findViewById(R.id.compartev);
        assert destinos != null;
        comparte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VALPARAISO.this, COMPARTEV.class);
                startActivity(i);
            }
        });
//COMPARTE

    }
}
