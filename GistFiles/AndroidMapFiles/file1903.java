package com.example.manu.camgraf; 
 
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity; 
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
 
 
public class MainActivity extends AppCompatActivity { 
 
    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // quitar subrayado
        TextView web = (TextView) findViewById(R.id.web);
 
        // TextView instanciado
        if (web != null) {
            StringUtil.removeUnderlines((Spannable) web.getText());
        } 
 
        // quitar subrayado de los numeros
        TextView number = (TextView) findViewById(R.id.number);
        if (number != null) {
            StringUtil.removeUnderlines((Spannable) number.getText());
        } 
 
        // quitar subrayado de los email
        TextView email = (TextView) findViewById(R.id.email);
        if (email != null) {
            StringUtil.removeUnderlines((Spannable) email.getText());
        } 
    } 
 
    public void openMap(View v) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.at/maps/place/Madrid,+Espa%C3%B1a/@40.4381311,-3.81962,11z/data=!3m1!4b1!4m5!3m4!1s0xd422997800a3c81:0xc436dec1618c2269!8m2!3d40.4167754!4d-3.7037902"));
        startActivity(intent);
    } 
 
 
} 
 