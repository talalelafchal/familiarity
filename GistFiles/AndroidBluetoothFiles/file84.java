package com.polkapolka.bluetooth.le;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polkapolka.bluetooth.le.socket.Send;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends Activity {
    EditText etPass;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView tvResult;
    Button btnLogin;
    public static String IP;

    LinearLayout hideLayout;
    private Socket socket;
    public static String barcode;
    public static final String PREFERENCES_NAME = "myPreference"; //偏好設定名稱
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        reloadpref();
    }

    private void initView(){
        tvResult = (TextView) findViewById(R.id.tvResult);
        etPass = (EditText) findViewById(R.id.editText);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        hideLayout= (LinearLayout) findViewById(R.id.hideLayout);
    }

    public void scanBar(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }


    //儲存偏好設定
    private void savePref() {
        SharedPreferences sp = this.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("IP",etPass.getText().toString()).apply();
        IP=etPass.getText().toString();
    }

    //讀取偏好設定
    private void reloadpref() {
        SharedPreferences sp = this.getSharedPreferences(PREFERENCES_NAME, 0);
        etPass.setText(sp.getString("IP", ""));
        IP=etPass.getText().toString();
    }
    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

//				Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                tvResult.setText("病例號碼:" + contents);
                barcode=contents+"";
                hideLayout.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
//				toast.show();
            }
        }
    }

    public void btnLogin(View view) {
        if (!etPass.getText().toString().equals("")){
            savePref();
            myThread(barcode+" 報到！");
            startActivity(new Intent(MainActivity.this,DeviceScanActivity.class));
        }else{
            Toast.makeText(this,"請輸入IP",Toast.LENGTH_SHORT).show();
        }
    }
    private void myThread(final String str){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    socket = new Socket(MainActivity.IP,5050);
                    new Thread(new Send(socket,str)).start();


                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        }).start();
    }
}