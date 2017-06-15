package com.devlon.snazl;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;
import com.polites.android.GestureImageView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import at.markushi.ui.CircleButton;

public class AddSnazlActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 23;
    Drawable myDrawable;
    Typeface custom_font;

   int index = 0;
    HashMap<String,AutoResizeTextView> map = new HashMap<>();

    ImageView /*iv_mySnazl,*/ iv_back_snazl, iv_add, iv_fadein, cb_add;
    GestureImageView iv_mySnazl;
    LinearLayout ll_edit, ll_text, ll_textColor, ll_bgColor;
    TextView toolbar_title, tv_alegreya, tv_anton, tv_bangers, tv_bubblegum, tv_clicker, tv_eater, tv_emilys, tv_grand, tv_great;
    TextView  tv_henny, tv_lobster, tv_londrina, tv_lora, tv_oleo, tv_pacifico, tv_robotoReg, tv_robotoThin, tv_unkempt;

    SingleFingerView tv_editSnazl, sfv;
    EditText tv_linkOpt;
    View view_edit;
    CircleButton cb_image, cb_camera, cb_fonts, cb_black, cb_gray, cb_grayWhite, cb_blue, cb_indigo, cb_green, cb_saffron, cb_red,
            cb_pink, cb_orange, cb_lightGreen, cb_purple, cb_violet, cb_navyBlue, cb_white, cb_wooden, cb_metal, cb_water, cb_rainbow,
            cb_yellow, cb_lightPink, cb_black_text, cb_gray_text, cb_grayWhite_text, cb_blue_text, cb_green_text, cb_lightGreen_text,
            cb_red_text, cb_pink_text, cb_saffron_text, cb_orange_text, cb_indigo_text, cb_purple_text, cb_lightPink_text, cb_white_text,
            cb_link, cb_violet_text, cb_navyBlue_text, cb_yellow_text, cb_graySlate_text, cb_Slate_text, cb_brown_text, cb_lightblue_text;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Point mSize;
    String mode, color_pallete, editedUrl="", textValue;
    int textObjCount = 1;
    Dialog dialog;
    FrameLayout fl_snazl;
    Boolean isDialogShowing = false, textFontInitial = false;
    AnimationDrawable rocketAnimation;
    Boolean newTextObj = false, isWhiteBG = true;
    Boolean background_image_color = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_snazl);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
//        myToolbar.setTitle("Cancel");
        myToolbar.setTitleTextColor(Color.WHITE);

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            myDrawable = getApplicationContext().getDrawable(R.drawable.ic_overflow);
        } else {
            myDrawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_overflow);
        }

        myToolbar.setOverflowIcon(myDrawable);

        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        mode = "edit";
        color_pallete = "background";
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddSnazlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        iv_mySnazl = (GestureImageView) findViewById(R.id.iv_mySnazl);
        iv_back_snazl = (ImageView) findViewById(R.id.iv_back_snazl);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_fadein = (ImageView) findViewById(R.id.iv_fadein);

        iv_fadein.setImageResource(R.drawable.fading);
        rocketAnimation = (AnimationDrawable) iv_fadein.getDrawable();

        iv_fadein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rocketAnimation.start();
            }
        });

        cb_image = (CircleButton) findViewById(R.id.cb_image);
        cb_camera = (CircleButton) findViewById(R.id.cb_camera);
        cb_fonts = (CircleButton) findViewById(R.id.cb_fonts);
        cb_link = (CircleButton) findViewById(R.id.cb_link);
        cb_add = (ImageView) findViewById(R.id.cb_add);

        // background color palette
        cb_black = (CircleButton) findViewById(R.id.cb_black);
        cb_gray = (CircleButton) findViewById(R.id.cb_gray);
        cb_grayWhite = (CircleButton) findViewById(R.id.cb_grayWhite);
        cb_blue = (CircleButton) findViewById(R.id.cb_blue);
        cb_indigo = (CircleButton) findViewById(R.id.cb_indigo);
        cb_green = (CircleButton) findViewById(R.id.cb_green);
        cb_saffron = (CircleButton) findViewById(R.id.cb_saffron);
        cb_red = (CircleButton) findViewById(R.id.cb_red);
        cb_pink = (CircleButton) findViewById(R.id.cb_pink);
        cb_orange = (CircleButton) findViewById(R.id.cb_orange);
        cb_lightGreen = (CircleButton) findViewById(R.id.cb_lightGreen);
        cb_purple = (CircleButton) findViewById(R.id.cb_purple);
        cb_white = (CircleButton) findViewById(R.id.cb_white);
        cb_wooden = (CircleButton) findViewById(R.id.cb_wooden);
        cb_metal = (CircleButton) findViewById(R.id.cb_metal);
        cb_water = (CircleButton) findViewById(R.id.cb_water);
        cb_rainbow = (CircleButton) findViewById(R.id.cb_rainbow);
        cb_violet = (CircleButton) findViewById(R.id.cb_violet);
        cb_navyBlue = (CircleButton) findViewById(R.id.cb_navyBlue);
        cb_yellow = (CircleButton) findViewById(R.id.cb_yellow);
        cb_lightPink = (CircleButton) findViewById(R.id.cb_lightPink);

        // text color palette
        cb_black_text = (CircleButton) findViewById(R.id.cb_black_text);
        cb_gray_text = (CircleButton) findViewById(R.id.cb_gray_text);
        cb_grayWhite_text = (CircleButton) findViewById(R.id.cb_grayWhite_text);
        cb_blue_text = (CircleButton) findViewById(R.id.cb_blue_text);
        cb_green_text = (CircleButton) findViewById(R.id.cb_green_text);
        cb_lightGreen_text = (CircleButton) findViewById(R.id.cb_lightGreen_text);
        cb_red_text = (CircleButton) findViewById(R.id.cb_red_text);
        cb_pink_text = (CircleButton) findViewById(R.id.cb_pink_text);
        cb_saffron_text = (CircleButton) findViewById(R.id.cb_saffron_text);
        cb_orange_text = (CircleButton) findViewById(R.id.cb_orange_text);
        cb_indigo_text = (CircleButton) findViewById(R.id.cb_indigo_text);
        cb_purple_text = (CircleButton) findViewById(R.id.cb_purple_text);
        cb_lightPink_text = (CircleButton) findViewById(R.id.cb_lightPink_text);
        cb_white_text = (CircleButton) findViewById(R.id.cb_white_text);
        cb_violet_text = (CircleButton) findViewById(R.id.cb_violet_text);
        cb_navyBlue_text = (CircleButton) findViewById(R.id.cb_navyBlue_text);
        cb_yellow_text = (CircleButton) findViewById(R.id.cb_yellow_text);
        cb_graySlate_text = (CircleButton) findViewById(R.id.cb_graySlate_text);
        cb_Slate_text = (CircleButton) findViewById(R.id.cb_Slate_text);
        cb_brown_text = (CircleButton) findViewById(R.id.cb_brown_text);
        cb_lightblue_text = (CircleButton) findViewById(R.id.cb_lightblue_text);

        ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        ll_textColor = (LinearLayout) findViewById(R.id.ll_textColor);
        ll_bgColor = (LinearLayout) findViewById(R.id.ll_bgColor);

        view_edit = findViewById(R.id.view_edit);

        tv_editSnazl = (SingleFingerView) findViewById(R.id.tv_editSnazl);

        fl_snazl = (FrameLayout) findViewById(R.id.fl_snazl);

        tv_alegreya = (TextView) findViewById(R.id.tv_alegreya);
        tv_anton = (TextView) findViewById(R.id.tv_anton);
        tv_bangers = (TextView) findViewById(R.id.tv_bangers);
        tv_bubblegum = (TextView) findViewById(R.id.tv_bubblegum);
        tv_clicker = (TextView) findViewById(R.id.tv_clicker);
        tv_eater = (TextView) findViewById(R.id.tv_eater);
        tv_emilys = (TextView) findViewById(R.id.tv_emilys);
        tv_grand = (TextView) findViewById(R.id.tv_grand);
        tv_great = (TextView) findViewById(R.id.tv_great);
        tv_henny = (TextView) findViewById(R.id.tv_henny);
        tv_lobster = (TextView) findViewById(R.id.tv_lobster);
        tv_londrina = (TextView) findViewById(R.id.tv_londrina);
        tv_lora = (TextView) findViewById(R.id.tv_lora);
        tv_oleo = (TextView) findViewById(R.id.tv_oleo);
        tv_pacifico = (TextView) findViewById(R.id.tv_pacifico);
        tv_robotoReg = (TextView) findViewById(R.id.tv_robotoReg);
        tv_robotoThin = (TextView) findViewById(R.id.tv_robotoThin);
        tv_unkempt = (TextView) findViewById(R.id.tv_unkempt);

        tv_alegreya.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AlegreyaSC-Regular.ttf"));
        tv_anton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anton.ttf"));
        tv_bangers.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bangers.ttf"));
        tv_bubblegum.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BubblegumSans-Regular.ttf"));
        tv_clicker.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ClickerScript-Regular.ttf"));
        tv_eater.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Eater-Regular.ttf"));
        tv_emilys.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/EmilysCandy-Regular.ttf"));
        tv_grand.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.ttf"));
        tv_great.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GreatVibes-Regular.ttf"));
        tv_henny.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HennyPenny-Regular.ttf"));
        tv_lobster.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf"));
        tv_londrina.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LondrinaShadow-Regular.ttf"));
        tv_lora.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lora-Regular.ttf"));
        tv_oleo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OleoScript-Regular.ttf"));
        tv_pacifico.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        tv_robotoReg.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
        tv_robotoThin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
        tv_unkempt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Unkempt-Bold.ttf"));

        // main three buttons

        cb_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                iv_mySnazl.setScaleType(ImageView.ScaleType.FIT_XY);
                isWhiteBG = false;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        cb_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                iv_mySnazl.setScaleType(ImageView.ScaleType.FIT_XY);
                isWhiteBG = false;
                requestForCameraPermission(view);
            }
        });

        cb_fonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ll_edit.setVisibility(View.GONE);
//                view_edit.setVisibility(View.GONE);
                ll_text.setVisibility(View.VISIBLE);
//                tv_editSnazl.setVisibility(View.VISIBLE);
                ll_bgColor.setVisibility(View.GONE);
                ll_textColor.setVisibility(View.VISIBLE);
                mode = "text";
                color_pallete = "textString";
                textObjCount++;
                if (textObjCount > 2) {
                    newTextObj = true;
                    addTextObject();
                }
                
                if (!textFontInitial){
                    textValue = "default";
                    tv_editSnazl.setVisibility(View.VISIBLE);
                    tv_editSnazl.mView.setTag(String.valueOf(index));
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anton.ttf"));
                    Constant.text_typeface_name="fonts/Anton.ttf";
                    Constant.textsmap.put(String.valueOf(index),tv_editSnazl.mView);
                    index++;
                    tv_editSnazl.mView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(tv_editSnazl.mView, InputMethodManager.SHOW_IMPLICIT);
                }
                
                textFontInitial = true;

                if (isWhiteBG){
                    if (newTextObj){
                        sfv.mView.setHintTextColor(Color.parseColor("#000000"));
                        sfv.mView.setTextColor(Color.parseColor("#000000"));
                        sfv.mView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(sfv.mView, InputMethodManager.SHOW_IMPLICIT);
                    }else {
                        tv_editSnazl.mView.setHintTextColor(Color.parseColor("#000000"));
                        tv_editSnazl.mView.setTextColor(Color.parseColor("#000000"));
                    }
                }else {
                    if (newTextObj){
                        sfv.mView.setHintTextColor(Color.parseColor("#ffffff"));
                        sfv.mView.setTextColor(Color.parseColor("#ffffff"));
                        sfv.mView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(sfv.mView, InputMethodManager.SHOW_IMPLICIT);
                    }else {

                        tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ffffff"));
                        tv_editSnazl.mView.setTextColor(Color.parseColor("#ffffff"));
                    }
                }

            }
        });

        cb_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(AddSnazlActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_link);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbg);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);

                isDialogShowing = true;

                tv_linkOpt = (EditText) dialog.findViewById(R.id.tv_linkOpt);
                tv_linkOpt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                if (!editedUrl.isEmpty()) {
                    tv_linkOpt.setText(editedUrl);
                    tv_linkOpt.setSelection(tv_linkOpt.getText().length());
                }

                tv_linkOpt.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {

                                case KeyEvent.KEYCODE_ENTER:
                                    editedUrl = tv_linkOpt.getText().toString().trim();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    dialog.dismiss();
                            }
                        }
                        return false;
                    }
                });

                dialog.show();
            }
        });

        KeyboardVisibilityEvent.setEventListener(AddSnazlActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                // some code depending on keyboard visiblity status
                if (isOpen) {
                    Log.e("Keyboard", " " + isOpen);
                    if (tv_editSnazl.mView.hasFocus()){
                        tv_editSnazl.mPushView.setVisibility(View.VISIBLE);
                    }

                    if (newTextObj){
                        if (sfv.mView.hasFocus()){
                            sfv.mPushView.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    if (isDialogShowing) {
                        dialog.dismiss();
                        isDialogShowing = false;
                    }
                }
            }
        });

        toolbar_title.setTypeface(custom_font);
        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // circle buttons backgrounds

        cb_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#000000"));
                isWhiteBG = false;
            }
        });

        cb_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#cbcbcb"));
                isWhiteBG = false;
            }
        });

        cb_grayWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#ededed"));
                isWhiteBG = true;
            }
        });

        cb_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#5accf8"));
                isWhiteBG = false;
            }
        });

        cb_indigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#8d2ac2"));
                isWhiteBG = false;
            }
        });

        cb_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#6da41d"));
                isWhiteBG = false;
            }
        });

        cb_saffron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#f76005"));
                isWhiteBG = false;
            }
        });

        cb_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#ba0014"));
                isWhiteBG = false;
            }
        });

        cb_pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#ef0000"));
                isWhiteBG = false;
            }
        });

        cb_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#fbbc48"));
                isWhiteBG = false;
            }
        });

        cb_lightGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#14ff08"));
                isWhiteBG = false;
            }
        });

        cb_purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#b975da"));
                isWhiteBG = false;
            }
        });

        cb_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#ffffff"));
                isWhiteBG = true;
            }
        });

        cb_violet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#3a00fb"));
                isWhiteBG = false;
            }
        });

        cb_navyBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#075b9b"));
                isWhiteBG = false;
            }
        });

        cb_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#ffff08"));
                isWhiteBG = false;
            }
        });

        cb_lightPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.parseColor("#f7b3d4"));
                isWhiteBG = false;
            }
        });

        cb_wooden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);
                iv_mySnazl.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_mySnazl.setImageResource(R.drawable.wooden_bg);
                isWhiteBG = false;
            }
        });

        cb_metal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);
                iv_mySnazl.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_mySnazl.setImageResource(R.drawable.metal_bg);
                isWhiteBG = false;
            }
        });

        cb_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);
                iv_mySnazl.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_mySnazl.setImageResource(R.drawable.water_bg);
                isWhiteBG = false;
            }
        });

        cb_rainbow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);
                iv_mySnazl.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_mySnazl.setImageResource(R.drawable.rainbow_bg);
                isWhiteBG = false;
            }
        });

        // circle textcolor buttons

        cb_black_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")){
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#000000"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#000000"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#000000"));
                        sfv.mView.setHintTextColor(Color.parseColor("#000000"));
                    }
                }
            }
        });

        cb_gray_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#cbcbcb"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#cbcbcb"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#cbcbcb"));
                        sfv.mView.setHintTextColor(Color.parseColor("#cbcbcb"));
                    }
                }
            }
        });

        cb_grayWhite_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#ededed"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ededed"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#ededed"));
                        sfv.mView.setHintTextColor(Color.parseColor("#ededed"));
                    }
                }
            }
        });

        cb_blue_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#5accf8"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#5accf8"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#5accf8"));
                        sfv.mView.setHintTextColor(Color.parseColor("#5accf8"));
                    }
                }
            }
        });

        cb_green_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#6da41d"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#6da41d"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#6da41d"));
                        sfv.mView.setHintTextColor(Color.parseColor("#6da41d"));
                    }
                }
            }
        });

        cb_lightGreen_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#14ff08"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#14ff08"));
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#14ff08"));
                        sfv.mView.setHintTextColor(Color.parseColor("#14ff08"));
                    }
                }
            }
        });

        cb_red_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#ba0014"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ba0014"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#ba0014"));
                        sfv.mView.setHintTextColor(Color.parseColor("#ba0014"));
                    }
                }
            }
        });

        cb_pink_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#ef0000"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ef0000"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#ef0000"));
                        sfv.mView.setHintTextColor(Color.parseColor("#ef0000"));
                    }
                }
            }
        });

        cb_saffron_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#f76005"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#f76005"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#f76005"));
                        sfv.mView.setHintTextColor(Color.parseColor("#f76005"));
                    }
                }
            }
        });

        cb_orange_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#fbbc48"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#fbbc48"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#fbbc48"));
                        sfv.mView.setHintTextColor(Color.parseColor("#fbbc48"));
                    }
                }
            }
        });

        cb_indigo_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#8d2ac2"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#8d2ac2"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#8d2ac2"));
                        sfv.mView.setHintTextColor(Color.parseColor("#8d2ac2"));
                    }
                }
            }
        });

        cb_purple_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#b975da"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#b975da"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#b975da"));
                        sfv.mView.setHintTextColor(Color.parseColor("#b975da"));
                    }
                }
            }
        });

        cb_lightPink_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#f9bff8"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#f9bff8"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#f9bff8"));
                        sfv.mView.setHintTextColor(Color.parseColor("#f9bff8"));
                    }
                }
            }
        });

        cb_white_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#ffffff"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ffffff"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#ffffff"));
                        sfv.mView.setHintTextColor(Color.parseColor("#ffffff"));
                    }
                }
            }
        });

        cb_violet_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#3a00fb"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#3a00fb"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#3a00fb"));
                        sfv.mView.setHintTextColor(Color.parseColor("#3a00fb"));
                    }
                }
            }
        });

        cb_navyBlue_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#075b9b"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#075b9b"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#075b9b"));
                        sfv.mView.setHintTextColor(Color.parseColor("#075b9b"));
                    }
                }
            }
        });

        cb_yellow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#ffff08"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#ffff08"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#ffff08"));
                        sfv.mView.setHintTextColor(Color.parseColor("#ffff08"));
                    }
                }
            }
        });

        cb_graySlate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#65808c"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#65808c"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#65808c"));
                        sfv.mView.setHintTextColor(Color.parseColor("#65808c"));
                    }
                }
            }
        });

        cb_Slate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#465b65"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#465b65"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#465b65"));
                        sfv.mView.setHintTextColor(Color.parseColor("#465b65"));
                    }
                }
            }
        });

        cb_brown_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#77524a"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#77524a"));
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#77524a"));
                        sfv.mView.setHintTextColor(Color.parseColor("#77524a"));
                    }
                }
            }
        });

        cb_lightblue_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTextColor(Color.parseColor("#17ffff"));
                    tv_editSnazl.mView.setHintTextColor(Color.parseColor("#17ffff"));
                }
                if (newTextObj) {
                    if (textValue.equals("added")) {
                        sfv.mView.setTextColor(Color.parseColor("#17ffff"));
                        sfv.mView.setHintTextColor(Color.parseColor("#17ffff"));
                    }
                }
            }
        });

        //fonts

        tv_anton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anton.ttf"));
                    Constant.text_typeface_name="fonts/Anton.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anton.ttf"));
                        Constant.text_typeface_name="fonts/Anton.ttf";
                    }
                }
            }
        });

        tv_alegreya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AlegreyaSC-Regular.ttf"));
                    Constant.text_typeface_name="fonts/AlegreyaSC-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AlegreyaSC-Regular.ttf"));
                        Constant.text_typeface_name="fonts/AlegreyaSC-Regular.ttf";
                    }
                }
            }
        });

        tv_bangers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bangers.ttf"));
                    Constant.text_typeface_name="fonts/Bangers.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bangers.ttf"));
                        Constant.text_typeface_name="fonts/Bangers.ttf";
                    }
                }
            }
        });

        tv_bubblegum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BubblegumSans-Regular.ttf"));
                    Constant.text_typeface_name="fonts/BubblegumSans-Regular.ttf";
                 }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BubblegumSans-Regular.ttf"));
                        Constant.text_typeface_name="fonts/BubblegumSans-Regular.ttf";
                    }
                }
            }
        });

        tv_clicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ClickerScript-Regular.ttf"));
                    Constant.text_typeface_name="fonts/ClickerScript-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ClickerScript-Regular.ttf"));
                        Constant.text_typeface_name="fonts/ClickerScript-Regular.ttf";
                    }
                }
            }
        });

        tv_eater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Eater-Regular.ttf"));
                    Constant.text_typeface_name="fonts/Eater-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Eater-Regular.ttf"));
                        Constant.text_typeface_name="fonts/Eater-Regular.ttf";
                    }
                }

            }
        });

        tv_emilys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/EmilysCandy-Regular.ttf"));
                    Constant.text_typeface_name="fonts/EmilysCandy-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/EmilysCandy-Regular.ttf"));
                        Constant.text_typeface_name="fonts/EmilysCandy-Regular.ttf";
                    }
                }
            }
        });

        tv_grand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.ttf"));
                    Constant.text_typeface_name="fonts/GrandHotel-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.ttf"));
                        Constant.text_typeface_name="fonts/GrandHotel-Regular.ttf";
                    }
                }
            }
        });

        tv_great.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GreatVibes-Regular.ttf"));
                    Constant.text_typeface_name="fonts/GreatVibes-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GreatVibes-Regular.ttf"));
                        Constant.text_typeface_name="fonts/GreatVibes-Regular.ttf";
                    }
                }
            }
        });

        tv_henny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HennyPenny-Regular.ttf"));
                    Constant.text_typeface_name="fonts/HennyPenny-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HennyPenny-Regular.ttf"));
                        Constant.text_typeface_name="fonts/HennyPenny-Regular.ttf";
                    }
                }
            }
        });

        tv_lobster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf"));
                    Constant.text_typeface_name="fonts/Lobster-Regular.ttf";
                }

                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf"));
                        Constant.text_typeface_name="fonts/Lobster-Regular.ttf";
                    }
                }
            }
        });

        tv_londrina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LondrinaShadow-Regular.ttf"));
                    Constant.text_typeface_name="fonts/LondrinaShadow-Regular.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LondrinaShadow-Regular.ttf"));
                        Constant.text_typeface_name="fonts/LondrinaShadow-Regular.ttf";
                    }
                }
            }
        });

        tv_lora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lora-Regular.ttf"));
                    Constant.text_typeface_name="fonts/Lora-Regular.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lora-Regular.ttf"));
                        Constant.text_typeface_name="fonts/Lora-Regular.ttf";
                    }
                }
            }
        });

        tv_oleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OleoScript-Regular.ttf"));
                    Constant.text_typeface_name="fonts/OleoScript-Regular.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OleoScript-Regular.ttf"));
                        Constant.text_typeface_name="fonts/OleoScript-Regular.ttf";
                    }
                }
            }
        });

        tv_pacifico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
                    Constant.text_typeface_name="fonts/Pacifico.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
                        Constant.text_typeface_name="fonts/Pacifico.ttf";
                    }
                }
            }
        });

        tv_robotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
                    Constant.text_typeface_name="fonts/Roboto-Regular.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
                        Constant.text_typeface_name="fonts/Roboto-Regular.ttf";
                    }
                }
            }
        });

        tv_robotoThin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
                    Constant.text_typeface_name="fonts/Roboto-Thin.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
                        Constant.text_typeface_name="fonts/Roboto-Thin.ttf";
                    }
                }
            }
        });

        tv_unkempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textValue.equals("default")) {
                    tv_editSnazl.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Unkempt-Bold.ttf"));
                    Constant.text_typeface_name="fonts/Unkempt-Bold.ttf";
                }
                if (newTextObj){
                    if (textValue.equals("added")) {
                        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Unkempt-Bold.ttf"));
                        Constant.text_typeface_name="fonts/Unkempt-Bold.ttf";
                    }
                }
            }
        });

        tv_editSnazl.mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {

                        case KeyEvent.KEYCODE_ENTER:
                            tv_editSnazl.mView.setBackgroundColor(Color.TRANSPARENT);
                            tv_editSnazl.mPushView.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Log.d("KEY", "enter_key_called");
//                            Toast.makeText(AddSnazlActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        /*tv_editSnazl.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println("focus changed");
                focus_Count++;
                if (focus_Count % 2 == 0) {

                }
            }
        });*/

        tv_editSnazl.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tv_editSnazl.mPushView.setVisibility(View.VISIBLE);
                textValue = "default";
                ll_bgColor.setVisibility(View.GONE);
                ll_textColor.setVisibility(View.VISIBLE);
                ll_text.setVisibility(View.VISIBLE);
            }
        });

        tv_editSnazl.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        tv_editSnazl.mView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect rectf = new Rect();
                iv_mySnazl.getLocalVisibleRect(rectf);

                Log.d("WIDTH        :", String.valueOf(rectf.width()));
                Log.d("HEIGHT       :", String.valueOf(rectf.height()));
                Log.d("left         :", String.valueOf(rectf.left));
                Log.d("right        :", String.valueOf(rectf.right));
                Log.d("top          :", String.valueOf(rectf.top));
                Log.d("bottom       :", String.valueOf(rectf.bottom));

                int[] l = new int[2];
                iv_mySnazl.getLocationOnScreen(l);
                int x = l[0];
                int y = l[1];
                int w = iv_mySnazl.getWidth();
                int h = iv_mySnazl.getHeight();

                Constant.iv_x = x;
                Constant.iv_y = y;
                Constant.iv_w = w;
                Constant.iv_h = h;
                Constant.ivBackground = iv_mySnazl;

                System.out.println("Bounds : " + x + " , " + y + " , " + w + " , " + h);

                int[] maxTextureSize = new int[1];
                GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
                Log.i("glinfo", "Max texture size = " + maxTextureSize[0]);

                /*if (rx < x || rx > x + w || ry < y || ry > y + h) {
                    return false;
                }*/

            }
        }, 500);

        iv_mySnazl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                ll_bgColor.setVisibility(View.VISIBLE);
                ll_textColor.setVisibility(View.GONE);
                ll_text.setVisibility(View.GONE);
                tv_editSnazl.mPushView.setVisibility(View.GONE);
                if (newTextObj) {
                    sfv.mPushView.setVisibility(View.GONE);
                }
            }
        });

       /* int[] l = new int[2];
        iv_mySnazl.getLocationOnScreen(l);*/
//        System.out.println("Bounds : "+x+" , "+y+" , "+w+" , "+h);


       /* iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleFingerView sfv = new SingleFingerView(AddSnazlActivity.this);
                sfv.mView.setTextColor(Color.BLACK);
                sfv.mPushView.setImageResource(R.drawable.push_btn);
                sfv.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                fl_snazl.addView(sfv);
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            System.out.println("File uri : " + picturePath);
            cursor.close();

            iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            int b_height = bitmap.getHeight();
            int b_width = bitmap.getWidth();
            System.out.println("Scale : "+b_height+" , "+b_width);

            if (b_height==b_width){
                iv_mySnazl.setScaleType(ImageView.ScaleType.FIT_XY);
            }else {
                iv_mySnazl.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            if(bitmap.getHeight()>=2048||bitmap.getWidth()>=2048){
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(picturePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

                Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                iv_mySnazl.setImageBitmap(adjustedBitmap);
            }else {
                iv_mySnazl.setImageBitmap(bitmap);
            }
        }

        if(resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            // Get the bitmap in according to the width of the device
            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.x, mSize.x);
//            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
            iv_back_snazl.setBackgroundColor(Color.TRANSPARENT);
            iv_mySnazl.setScaleType(ImageView.ScaleType.FIT_XY);
            iv_mySnazl.setImageBitmap(bitmap);
        }
    }

    public void requestForCameraPermission(View view) {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(AddSnazlActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddSnazlActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            launch();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(AddSnazlActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddSnazlActivity.this.requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(AddSnazlActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    private void launch() {
        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launch();
                }
                break;

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


    public void addTextObject(){
        textValue = "added";
        sfv = new SingleFingerView(AddSnazlActivity.this);
//                    sfv.setId(resIdCount++);

        sfv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tv_editSnazl.getHeight()));

        sfv.mPushView.setImageResource(R.drawable.push_btn);
        sfv.mPushView.setLayoutParams(new FrameLayout.LayoutParams(20, 20));
        sfv.mView.setTop(Constant.ivBackground.getTop() + Constant.ivBackground.getHeight() / 2);
        sfv.mView.setLeft(Constant.ivBackground.getLeft() + Constant.ivBackground.getWidth() / 2);

        sfv.mView.setTag(String.valueOf(index));
        fl_snazl.addView(sfv);

        sfv.mView.setFocusableInTouchMode(true);
        sfv.mView.setSingleLine(true);
        sfv.mView.setHint("Text");
        sfv.mView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Anton.ttf"));
        sfv.mView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
        sfv.mView.setSelection(0,0);

        Constant.textsmap.put(String.valueOf(index), sfv.mView);
        index++;

         Constant.text_typeface_name="fonts/Anton.ttf";

        sfv.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        sfv.mView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        sfv.mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {

                        case KeyEvent.KEYCODE_ENTER:
                            sfv.mView.setBackgroundColor(Color.TRANSPARENT);
                            sfv.mPushView.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        sfv.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sfv.mPushView.setVisibility(View.VISIBLE);
                textValue = "added";
                ll_bgColor.setVisibility(View.GONE);
                ll_textColor.setVisibility(View.VISIBLE);
                ll_text.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_snazl, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        share.setVisible(true);
        share.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            tv_editSnazl.mPushView.setVisibility(View.GONE);
            if (newTextObj){
                sfv.mPushView.setVisibility(View.GONE);
            }

            File file = saveBitMap(this, fl_snazl);    //which view you want to pass that view as parameter

            if (file != null) {
                Log.i("TAG", "Drawing saved to the gallery!");
            } else {
                Log.i("TAG", "Oops! Image could not be saved.");
            }


            /*Constant.text_title = tv_editSnazl.mView.getText().toString();
            Constant.text_cordinate_x = String.valueOf(tv_editSnazl.mView.getX());
            Constant.text_cordinate_y = String.valueOf(tv_editSnazl.mView.getY());
            Constant.text_color = String.valueOf(tv_editSnazl.mView.getCurrentTextColor());
            Constant.text_rotation = String.valueOf(tv_editSnazl.mView.getRotation());
            Constant.text_height = String.valueOf(tv_editSnazl.mView.getHeight());
            Constant.text_weight = String.valueOf(tv_editSnazl.mView.getWidth());
            Constant.text_font_style = Constant.text_typeface_name;*/

            Intent intent = new Intent(AddSnazlActivity.this,ShareSnazlActivity.class);
            intent.putExtra("linkUrl",editedUrl);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);


        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);

        System.out.println("Bitmap xy : "+String.valueOf(returnedBitmap.getWidth()+" , "+String.valueOf(returnedBitmap.getHeight())));

        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap

        Constant.snazlBitmap = returnedBitmap;
        return returnedBitmap;
    }
    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mode.equals("text")) {
            ll_edit.setVisibility(View.VISIBLE);
            view_edit.setVisibility(View.VISIBLE);
            ll_text.setVisibility(View.GONE);
//            tv_editSnazl.setVisibility(View.GONE);
            ll_bgColor.setVisibility(View.VISIBLE);
            ll_textColor.setVisibility(View.GONE);
            mode = "edit";
            color_pallete = "background";
        } else {
            finish();
        }
    }*/
}
