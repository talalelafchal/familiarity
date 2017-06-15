package me.dontenvy.videotest;

import android.app.Activity;import android.os.Bundle;


public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.container, CameraView.newInstance()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        MainMenu myMainMenu = (MainMenu) findViewById(R.id.main_menu);

        if (myMainMenu.isOpen()){
            myMainMenu.close();
        }else{
            super.onBackPressed();
        }
    }
}