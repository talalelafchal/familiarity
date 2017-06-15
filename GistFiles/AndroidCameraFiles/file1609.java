package com.riot.projetoriotboothrfid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static SplashActivity mInst;
    public TextView tagText;
    public boolean task = false;


    public static SplashActivity instance() {
        return mInst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,     WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        tagText = (TextView) findViewById(R.id.tagText);
        tagText.setText("Verificando o status da Internet...");
        User u = new User();
        if (isNetworkAvailable()){
            MySingleton singleton = MySingleton.getInstance();
            singleton.setInternet(true);
            conexaoComInternet();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("APPLog", "Start Splash");
        mInst = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("APPLog", "Resume Splash");
        conexaoComInternet();
    }

    public void conexaoComInternet(){
        MySingleton singleton = MySingleton.getInstance();
        tagText = (TextView) findViewById(R.id.tagText);
        if (singleton.getInternet()){
            tagText.setText("Conectado com a Internet! Aguardando o leitor de RFID...");
            if (!task){
                try{
                    singleton.getNetwork().execute();
                    task= true;
                    tagText.setText("Socket OK! RFID pronto.");
                    Intent intent = new Intent(SplashActivity.this, Instruction.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("APPLog", "Erro");
                    tagText.setText("Erro no Socket!!!");
                    task= false;
                    /*setContentView(R.layout.activity_splash);
                    try {
                        Thread.sleep(30000);
                        conexaoComInternet();
                    } catch (InterruptedException e1) {
                        Log.d("APPLog", "Erro Thread");
                    }*/
                }
            }
        }else{
            tagText.setText("Sem conexao com a Internet! Aguarde...");
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}
