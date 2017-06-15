package com.example.android.myapplication;

import android.content.Intent;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMap(View view) {
        openMap();
    }

    public void booking(View view) {
        reservation();

    }

    public void showMenu (View view){
        openMenu();
    }

    /* This method opens Google Maps.
 */
    private void openMap() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=51.465211,-0.129212"));
        startActivity(intent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /*This method opens Menu.
     */
    private void openMenu() {
        Uri openMenu = Uri.parse("https://drive.google.com/file/d/0B5QyC7O2F8ikS0lCS1c0dVQyS0k/view?usp=sharing");
        Intent intent = new Intent(Intent.ACTION_VIEW, openMenu);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /* This method opens reservation page.
     */
    private void reservation() {
        Uri reservation = Uri.parse("http://www.mendozasquare.co.uk/reservations");
        Intent intent = new Intent(Intent.ACTION_VIEW, reservation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
