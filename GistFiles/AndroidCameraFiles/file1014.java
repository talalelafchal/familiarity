package com.riot.projetoriotboothrfid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class Instruction extends Activity {

    private static Instruction mInst;

    public static Instruction instance() {
        return mInst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
    }

    @Override
    public void onStart() {
        super.onStart();
        mInst = this;
    }

    public void mudaCamera(){
        Log.d("APPLog", "MudaCamera");
        Intent intent = new Intent(Instruction.this, CameraActivity.class);
        startActivity(intent);
    }

    public void voltaSplash(){
        Intent intent = new Intent(Instruction.this, SplashActivity.class);
        startActivity(intent);
    }
}