package com.example.naver.sandus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.example.naver.sandus.data.profiles.UserData;
import com.example.naver.sandus.library.material.dialog.MaterialDialog;
import com.example.naver.sandus.library.material.materialedittext.MaterialEditText;
import com.example.naver.sandus.library.material.widget.RippleView;
import com.example.naver.sandus.meta.DelayActivity;
import com.example.naver.sandus.net.JoinAsyncTask;
import com.example.naver.sandus.util.Const;
import com.example.naver.sandus.util.DelayUtil;
import com.example.naver.sandus.util.ErrorDelay;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ProfileActivity extends DelayActivity {

    private final int CHOICE_PROFILE = 0;
    private final int CHOICE_BUSINESS = 1;

    private RadioButton member_company_radio;
    private RadioButton member_user_radio;

    private ImageView profile_image;
    private ImageView biz_img;

    private MaterialEditText name_edit;
    private MaterialEditText email_edit;
    private MaterialEditText pass_edit;
    private MaterialEditText pass_confirm_edit;
    private MaterialEditText phone_edit;
    private MaterialEditText company_name_edit;

    private String currentPhotoPath = "";
    private String changeImagePath = "";
    private String finalProfileImagePath = "";

    private String finalBusinessImagePath = "";

    private boolean bChangeProfileImage = false;
    private boolean bChangeBusinessImage = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("title");

        if(title.isEmpty())
        {
            title = "";
        }

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        RippleView image_btn = (RippleView) findViewById(R.id.image_btn);
        image_btn.setOnRippleCompleteListener(onProfileImageListener);

        member_company_radio = (RadioButton) findViewById(R.id.member_company_radio);
        member_user_radio = (RadioButton) findViewById(R.id.member_user_radio);

        member_company_radio.setOnCheckedChangeListener(onCompanyRadioListener);
        member_user_radio.setOnCheckedChangeListener(onUserRadioListener);

        member_company_radio.setChecked(true);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        biz_img = (ImageView) findViewById(R.id.biz_img);

        name_edit = (MaterialEditText) findViewById(R.id.name_edit);
        email_edit = (MaterialEditText) findViewById(R.id.email_edit);
        pass_edit = (MaterialEditText) findViewById(R.id.pass_edit);
        pass_confirm_edit = (MaterialEditText) findViewById(R.id.pass_confirm_edit);
        phone_edit = (MaterialEditText) findViewById(R.id.phone_edit);
        company_name_edit = (MaterialEditText) findViewById(R.id.company_name_edit);

        RippleView bisness_btn = (RippleView) findViewById(R.id.bisness_btn);
        bisness_btn.setOnRippleCompleteListener(onBisnessListener);

    }

    private RippleView.OnRippleCompleteListener onBisnessListener = new RippleView.OnRippleCompleteListener() {
        @Override
        public void onComplete(RippleView rippleView) {
            startImageSelectPopup(CHOICE_BUSINESS);
        }
    };

    private RippleView.OnRippleCompleteListener onProfileImageListener = new RippleView.OnRippleCompleteListener() {
        @Override
        public void onComplete(RippleView rippleView) {
            startImageSelectPopup(CHOICE_PROFILE);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCompanyRadioListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                member_user_radio.setChecked(false);
                member_user_radio.invalidate();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onUserRadioListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked)
            {
                member_company_radio.setChecked(false);
                member_user_radio.invalidate();
            }
        }
    };

    private void startjoin()
    {
        boolean bCheck;

        bCheck = checkName();

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "이름을 입력하세요");

            return;
        }

        bCheck = checkEmail();

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "이메일을 형식데로 입력하세요");

            return;
        }

        bCheck = checkPass();

        if(!bCheck)
        {
            return;
        }

        bCheck = checkPhone();

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "폰번호 형식데로 입력하세요");

            return;
        }

        bCheck = checkCompanyName();

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "상호명을 입력하세요");

            return;
        }

        showProgressDialog();

        JoinAsyncTask task = new JoinAsyncTask();
        task.setBaseParam(this, null, progressDialog, "");

        try {
            UserData data = new UserData();

//            data.family_name = URLEncoder.encode("김","UTF-8");
//            data.given_name = URLEncoder.encode(name_edit.getText().toString(), "UTF-8");
//            data.email = URLEncoder.encode(email_edit.getText().toString(), "UTF-8");
//            data.pass = URLEncoder.encode(pass_edit.getText().toString(),"UTF-8");
//            data.phone_number = URLEncoder.encode(phone_edit.getText().toString(), "UTF-8");
//            data.company_name = URLEncoder.encode(company_name_edit.getText().toString(),"UTF-8");

            data.family_name = "김";
            data.given_name = name_edit.getText().toString();
            data.email = email_edit.getText().toString();
            data.pass = pass_edit.getText().toString();
            data.phone_number = phone_edit.getText().toString();
            data.company_name = company_name_edit.getText().toString();

            data.profile_image = finalProfileImagePath;
            data.business_license = finalBusinessImagePath;

            task.setJsonParam(data);
            task.execute();
        }
        catch (JSONException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
//        catch (UnsupportedEncodingException e)
//        {
//            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
//        }
    }

    private boolean checkCompanyName()
    {
        boolean bResult = true;

        String name = company_name_edit.getText().toString();

        if(name.isEmpty())
        {
            bResult = false;
        }

        return bResult;
    }

    private boolean checkPhone()
    {
        boolean bCheck;

        String phone = phone_edit.getText().toString();

        bCheck = Patterns.PHONE.matcher(phone).matches();

        return bCheck;
    }

    private boolean checkEmail()
    {
        boolean bResult;

        String email = email_edit.getText().toString();

        bResult = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        return bResult;
    }


    private boolean checkPass()
    {
        boolean bCheck;

        String pass = pass_edit.getText().toString();
        String passConfirm = pass_confirm_edit.getText().toString();

        bCheck = pass.equals(passConfirm);

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "입력한 패스워드가 다릅니다");
            return bCheck;
        }

        bCheck = pass.matches("^(?=.*[a-zA-Z]).{8,12}$");

        if(!bCheck)
        {
            ErrorDelay.setError(this, ErrorDelay.CUSTOM, "입력한 패스워드 형식이 다릅니다");
        }

        return bCheck;
    }


    private boolean checkName()
    {
        boolean bResult = true;

        String str = name_edit.getText().toString();

        if(str.isEmpty())
        {
            bResult = false;
        }

        return bResult;
    }

    private void startImageSelectPopup(int type)
    {
        if(checkCameraPermission(type))
        {
            showChoicePopup(type);
        }
    }
    private boolean checkCameraPermission(int type)
    {
        int choiceType = Const.REQUEST_CAMERA_PERMISSION;

        if(type == CHOICE_BUSINESS)
        {
            choiceType = Const.REQUEST_BIZ_CAMERA_PERMISSION;
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CAMERA
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }
                    , choiceType);
            return false;
        } else {
            return true;
        }
    }

    private void showChoicePopup(int type)
    {
        MaterialDialog.ListCallback callback = listCallback;

        if(type == CHOICE_PROFILE)
        {
            callback = listCallback;
        }
        else if(type == CHOICE_BUSINESS)
        {
            callback = businessCallback;
        }

        new MaterialDialog.Builder(this)
                .items(R.array.photo_input)
                .itemsCallback(callback)
                .show();
    }

    private MaterialDialog.ListCallback listCallback = new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            if(which == 0)
            {
                dispatchCamera(CHOICE_PROFILE);
            }
            else if(which == 1)
            {
                dispatchPhoto(CHOICE_PROFILE);
            }
        }
    };

    private MaterialDialog.ListCallback businessCallback = new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            if(which == 0)
            {
                dispatchCamera(CHOICE_BUSINESS);
            }
            else if(which == 1)
            {
                dispatchPhoto(CHOICE_BUSINESS);
            }
        }
    };

    private void dispatchCamera(int type)
    {
        try {
            File f = DelayUtil.createImageFile(this, "");
            currentPhotoPath = f.getAbsolutePath();

            Uri uri = Uri.fromFile(f);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            int inputType = Const.PHOTO_INPUT_CAMERA;

            if(type == CHOICE_BUSINESS)
            {
                inputType = Const.PHOTO_INPUT_BIZ_CAMERA;
            }

            startActivityForResult(intent, inputType);
        }
        catch(IOException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
    }

    private void dispatchPhoto(int type)
    {
        try {
            File f = DelayUtil.createImageFile(this, "");
            currentPhotoPath = f.getAbsolutePath();

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

            int inputType = Const.PHOTO_INPUT_ALBUM;

            if(type == CHOICE_BUSINESS)
            {
                inputType = Const.PHOTO_INPUT_BIZ_ALBUM;
            }

            startActivityForResult(intent, inputType);
        }
        catch (IOException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
    }

    private boolean checkJoin()
    {
        boolean bResult = true;

        return bResult;
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", "메인화면");
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_main);

        finish();
    }

    private void handleCrop(Bitmap bitmap, int type)
    {
        try
        {
            int height = 720*bitmap.getHeight() / bitmap.getWidth();

            Bitmap resize = DelayUtil.makeResizeBitmap(bitmap, 720, height);

            bitmap.recycle();

            if(type == CHOICE_PROFILE)
            {
                File file = DelayUtil.createImageFile(this, "profile.jpg");

                OutputStream out = new FileOutputStream(file);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, out);

                finalProfileImagePath = file.getPath();

                Glide.with(this)
                        .load(finalProfileImagePath)
                        .bitmapTransform(new CropCircleTransformation(Glide.get(getApplicationContext()).getBitmapPool()))
                        .into(profile_image);

            }
            else if(type == CHOICE_BUSINESS)
            {
                File file = DelayUtil.createImageFile(this, "biz.jpg");

                OutputStream out = new FileOutputStream(file);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, out);

                finalBusinessImagePath = file.getPath();

                Glide.with(this)
                        .load(finalBusinessImagePath)
                        .into(biz_img);
            }
        }
        catch (Exception e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
    }

    private void handleCrop(String data, int type) {

        changeImagePath = data;

        try {
            ExifInterface exif = new ExifInterface(changeImagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);

            Bitmap image = BitmapFactory.decodeFile(changeImagePath);

            image = rotate(image, exifDegree);

            handleCrop(image, type);
        }
        catch (IOException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
    }

    private void handleCrop(Intent data, int type)
    {
        Uri uri = data.getData();

        try {
            String filePath = DelayUtil.getPath(this, uri);
            ExifInterface exif = new ExifInterface(filePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);

            Bitmap image = BitmapFactory.decodeFile(filePath);

            image = rotate(image, exifDegree);

            handleCrop(image, type);
        }
        catch (FileNotFoundException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
        catch (IOException e)
        {
            ErrorDelay.setError(this, ErrorDelay.EXCEPTION, e);
        }
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                ErrorDelay.setError(this, ErrorDelay.EXCEPTION, ex);
                return bitmap;
            }
        }
        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case Const.PHOTO_INPUT_CAMERA:
                handleCrop(currentPhotoPath, CHOICE_PROFILE);
                break;
            case Const.PHOTO_INPUT_ALBUM:
                handleCrop(data, CHOICE_PROFILE);
                break;
            case Const.PHOTO_INPUT_BIZ_CAMERA:
                handleCrop(currentPhotoPath, CHOICE_BUSINESS);
                break;
            case Const.PHOTO_INPUT_BIZ_ALBUM:
                handleCrop(data, CHOICE_BUSINESS);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean isGranted = grantResults != null;
        if (isGranted) {
            for (int result : grantResults) {
                isGranted &= (result == PackageManager.PERMISSION_GRANTED);
            }

            switch (requestCode) {
                case Const.REQUEST_CAMERA_PERMISSION:
                    if (isGranted) {
                        startImageSelectPopup(CHOICE_PROFILE);
                    } else {
                        moveToSettingPermission();
                    }
                    break;
                case Const.REQUEST_BIZ_CAMERA_PERMISSION:
                    if (isGranted) {
                        startImageSelectPopup(CHOICE_BUSINESS);
                    } else {
                        moveToSettingPermission();
                    }
                    break;

            }
        }
    }

    public void onHttpResult(int requestCode, Object object)
    {
        closeProgressDialog();

        if(requestCode == Const.JOIN)
        {
            startMainActivity();
        }
    }

    private void moveToSettingPermission() {
        ErrorDelay.setError(ProfileActivity.this, ErrorDelay.CUSTOM, "permission error");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        setFinish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home)
        {
            setFinish();
            return true;
        }
        else if(id == R.id.complete_item)
        {
            if(checkJoin())
            {
                startjoin();

                return true;
            }
            else
            {
                //TODO:회원 가입 입력값 오류 에러
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
