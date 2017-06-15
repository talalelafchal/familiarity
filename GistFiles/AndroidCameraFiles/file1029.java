package com.aravindraj.b2.profile;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView dp;
    EditText rename;
    ImageButton imgbt,dn;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA = 1888;
    TextInputLayout inputLayoutName;
    String name = "Aravind Raj C",uri_st;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        inputLayoutName = (TextInputLayout) findViewById(R.id.name_til);

        setSupportActionBar(toolbar);

        dp = (ImageView) findViewById(R.id.dp);
        dn = (ImageButton) findViewById(R.id.done_btn);
        imgbt = (ImageButton) findViewById(R.id.imgBut);
        rename = (EditText) findViewById(R.id.label_edit);
        rename.addTextChangedListener(new MyTextWatcher(rename));


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            File exStore = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            String pictureName = getPictureName();
                            File imageFile = new File(exStore,pictureName);
                            Uri uri = Uri.fromFile(imageFile);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                            uri_st = uri.toString();

                            startActivityForResult(intent, REQUEST_CAMERA);

                        } else if (items[item].equals("Choose from Library")) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);

                        } else if (items[item].

                                equals("Cancel")

                                )

                        {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


    }

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        return "H&N Profile Picture"+timestamp+".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.dp);

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageView.setImageURI(selectedImage);

        }

        else if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && null!=data){

            Uri test = Uri.parse(uri_st);
            dp.setImageURI(test);

        }

    }

    public void editName (View view){
//        Intent i = new Intent(MainActivity.this,NameChangeActivity.class);
//        i.putExtra("name", "Aravind Raj C");
//        startActivity(i);

        TextView name = (TextView) findViewById(R.id.label);
        EditText lbl_edit = (EditText) findViewById(R.id.label_edit);
        lbl_edit.setSelection(lbl_edit.getText().length());
        ImageButton rename = (ImageButton) findViewById(R.id.imgBut);
        ImageButton done = (ImageButton) findViewById(R.id.done_btn);

        InputMethodManager imm = (InputMethodManager) MainActivity.this
                .getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);

        name.setVisibility(View.GONE);
        rename.setVisibility(View.GONE);
        lbl_edit.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

    }
    public void done(View view){
        TextView name = (TextView) findViewById(R.id.label);
        EditText lbl_edit = (EditText) findViewById(R.id.label_edit);
        ImageButton rename = (ImageButton) findViewById(R.id.imgBut);
        ImageButton done = (ImageButton) findViewById(R.id.done_btn);

        name.setText(lbl_edit.getText().toString());
        done.setVisibility(View.GONE);
        lbl_edit.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        rename.setVisibility(View.VISIBLE);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
                    return;
            }
        }
    }




