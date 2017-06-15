package com.lucaspozzi.bookscan;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button scanBtn, previewBtn, linkBtn;
    private TextView authorText, titleText, descriptionText, dateText, ratingCountText;
    private LinearLayout starLayout;
    private ImageView thumbView;
    private ImageView[] starView;
    private Bitmap thumbImg;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
}


   public void onClick(View view){

   }




}