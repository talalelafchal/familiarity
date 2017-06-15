package com.google.engedu.puzzle8;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;


public class PuzzleActivity extends AppCompatActivity {

//    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap = null;
    private PuzzleBoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.puzzle_container);
        boardView = new PuzzleBoardView(this);

        // Some setup of the view.
        boardView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        container.addView(boardView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("before","asdfghjkl error");
//        ImageView myImageView = (ImageView) this.findViewById(R.id.imagePuzzle);
        PuzzleBoardView puzzleBoardView = new PuzzleBoardView(this);
        Log.i("after","asdfghjkl error");

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    imageBitmap= (Bitmap) extras.get("data");
//
//                    Bitmap dstBitmap = cropImage(imageBitmap);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                            imageBitmap, 400, 400, false);
//                    myImageView.setImageBitmap(resizedBitmap);
                    puzzleBoardView.initialize(resizedBitmap, findViewById(R.id.puzzle_container));
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
//                        dstBitmap = cropImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                          imageBitmap , 400, 400, false);
//                    Log.i("from","asdfghjkl");
//                    myImageView.setImageBitmap(resizedBitmap);
                    puzzleBoardView.initialize(resizedBitmap, findViewById(R.id.puzzle_container));
//                    Log.i("from","asdfghjkl done");
                }
                break;
        }

    }

    public void selectImage(View view) {

        final CharSequence[] items = { "Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                        galleryIntent();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void shuffleImage(View view) {
        boardView.shuffle();
    }

    public void solve(View view) {
      boardView.solve();

    }
//    crop image into a perfect square
//    public Bitmap cropImage(Bitmap srcBitmap){
//        Bitmap dstBmp = null;
//        if(srcBitmap.getHeight() >= srcBitmap.getWidth() ){
//           dstBmp = Bitmap.createBitmap(
//                    srcBitmap,
//                    0,
//                   srcBitmap.getHeight()/2 - srcBitmap.getWidth()/2,
//                   srcBitmap.getWidth(),
//                   srcBitmap.getWidth()
//            );
//        }else{
//            dstBmp = Bitmap.createBitmap(
//                    srcBitmap,
//                    srcBitmap.getWidth()/2 - srcBitmap.getHeight()/2,
//                    0,
//                    srcBitmap.getHeight(),
//                    srcBitmap.getHeight()
//            );
//        }
//        return dstBmp;
//    }
    private void cameraIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 0);
        }
    }
    private void galleryIntent(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto , 1);
    }
}
