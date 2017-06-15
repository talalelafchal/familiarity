package com.example.ContactForm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: I
 * Date: 11.02.13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class ContactForm extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //ImageView avatar = (ImageView) findViewById(R.id.avatar);


    }

    public void clickListener(View v) {
        Intent intent = new Intent();
        intent.setClass(ContactForm.this,AvatarListActivity.class);
        startActivity(intent);
        finish();
    }
}