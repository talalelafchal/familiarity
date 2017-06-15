package com.example.andrescelis.thisis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JERICO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jerico);

//HACER
        Button hacer = (Button) findViewById(R.id.hacerj);
        assert hacer != null;
        hacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, HACERJ.class);
                startActivity(i);
            }
        });
//HACER


//INFO
        Button info = (Button) findViewById(R.id.infoj);
        assert info != null;
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, INFOJ.class);
                startActivity(i);
            }
        });
//INFO

//DESTINOS
        Button destinos = (Button) findViewById(R.id.destinosj);
        assert destinos != null;
        destinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, DESTINOSJ.class);
                startActivity(i);
            }
        });
//DESTINOS

//GALERIA
        Button galeria = (Button) findViewById(R.id.galeriaj);
        assert galeria != null;
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, GALERIAJ.class);
                startActivity(i);
            }
        });
//GALERIA

//FESTIVIDADES
        Button festividades = (Button) findViewById(R.id.festividadesj);
        assert festividades != null;
        festividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, FESTIVIDADESJ.class);
                startActivity(i);
            }
        });
//FESTIVIDADES

//ALIADOS
        Button aliados = (Button) findViewById(R.id.aliadosj);
        assert aliados != null;
        aliados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, ALIADOSJ.class);
                startActivity(i);
            }
        });
//ALIADOS

//COMPARTE
        Button comparte = (Button) findViewById(R.id.compartej);
        assert destinos != null;
        comparte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JERICO.this, COMPARTEJ.class);
                startActivity(i);
            }
        });
//COMPARTE
    }
}
