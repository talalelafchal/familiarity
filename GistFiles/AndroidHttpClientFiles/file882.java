package com.example.Posten2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public EditText et_sporingsnummer;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        et_sporingsnummer= (EditText)findViewById(R.id.sporingsnummer);
        yes.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        //To change body of implemented methods use File | Settings | File Templates.
        switch (v.getId()) {

            case R.id.btn_yes:
                Posten posten = new Posten();
                String [] input = {String.valueOf(et_sporingsnummer.toString())};
                posten.startGetData(input);
                File s = new File("data/data/com.example.Posten2/files");
                posten.ListDir(s);
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
