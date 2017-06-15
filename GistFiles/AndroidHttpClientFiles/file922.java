package com.example.untitled3.pages;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.untitled3.R;

/**
 * Created with IntelliJ IDEA.
 * User: zemin
 * Date: 20.03.2013
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class mypage extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_page);

        TextView txtProduct = (TextView) findViewById(R.id.product_label);

        Intent i = getIntent();
        // getting attached intent data
        String product = i.getStringExtra("product");
        // displaying selected product name
        txtProduct.setText(product);
    }
}
