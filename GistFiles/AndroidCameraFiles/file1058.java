package net.winnerawan.umroh.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import net.winnerawan.umroh.R;
import net.winnerawan.umroh.app.AppInterface;
import net.winnerawan.umroh.app.AppRequest;
import net.winnerawan.umroh.helper.SQLiteHandler;
import net.winnerawan.umroh.response.CheckReqResponse;
import net.winnerawan.umroh.response.RegResponse;
import net.winnerawan.umroh.response.RegisterResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView view_passport,view_photo,view_surat_nikah,view_ktp,view_kk,view_akte;
    FloatingActionMenu buttonAdd;
    com.github.clans.fab.FloatingActionButton button_passport, button_photo, button_surat;
    com.github.clans.fab.FloatingActionButton button_ktp, button_kk, button_akte, button_save;
    AppInterface api;
    private Bitmap bitmap;
    int reg_id;
    private int PICK_IMAGE_REQUEST = 1;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    final static int MY_PERMISSIONS_REQUEST =1;
    final static int UP_PASSPORT = 2;
    final static int UP_PHOTO = 3;
    final static int UP_SN = 4;
    final static int UP_KK = 5;
    final static int UP_KTP = 6;
    final static int UP_AKTE = 7;
    int REQS = 9;
    String api_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req);
        init();
        AppRequest request = new AppRequest();
        api = request.UMROH().create(AppInterface.class);
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        api_key = user.get("api_key");
        check(api_key);

//        button_passport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //
//                showFileChooser(UP_PASSPORT);
//            }
//        });
//
//        button_surat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser(UP_SN);
//            }
//        });
//
//        button_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser(UP_PHOTO);
//            }
//        });
//
//        button_ktp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser(UP_KTP);
//            }
//        });
//
//        button_kk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser(UP_KK);
//            }
//        });
//
//        button_akte.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser(UP_AKTE);
//            }
//        });
//
//        button_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
    
    private void init() {
        this.view_passport = (ImageView) findViewById(R.id.ic_passport);
        this.view_photo = (ImageView) findViewById(R.id.ic_photo);
        this.view_surat_nikah = (ImageView) findViewById(R.id.ic_surat_nikah);
        this.view_ktp = (ImageView) findViewById(R.id.ic_ktp);
        this.view_kk = (ImageView) findViewById(R.id.ic_kk);
        this.view_akte = (ImageView) findViewById(R.id.ic_akte);
        view_passport.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        view_photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        view_surat_nikah.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        view_ktp.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        view_kk.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        view_akte.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
        this.buttonAdd = (FloatingActionMenu) findViewById(R.id.button_add);
        this.button_passport = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_passport);
        this.button_photo = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_photo);
        this.button_ktp = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_ktp);
        this.button_surat = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_surat);
        this.button_kk = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_kk);
        this.button_akte = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_akte);
        this.button_save = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.button_save);

    }

    private void check(final String api_key) {
        Call<CheckReqResponse> checkReq = api.checkReqs(api_key);
        checkReq.enqueue(new Callback<CheckReqResponse>() {
            @Override
            public void onResponse(Call<CheckReqResponse> call, Response<CheckReqResponse> response) {
                boolean error = response.body().getError();
                if ((!error) && response.body().getResult().size()!=0) {

                    String passport = response.body().getResult().get(0).getReqPassport();
                    String photo = response.body().getResult().get(0).getReqPhoto();
                    String surat = response.body().getResult().get(0).getReqSuratNikah();
                    String ktp = response.body().getResult().get(0).getReqKtp();
                    String kk = response.body().getResult().get(0).getReqKk();
                    String akte = response.body().getResult().get(0).getReqAkte();

                    if (!passport.equals("")) {
                        view_passport.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                    if (!photo.equals("")) {
                        view_photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                    if (!surat.equals("")) {
                        view_surat_nikah.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                    if (!ktp.equals("")) {
                        view_ktp.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                    if (!kk.equals("")) {
                        view_kk.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                    if (!akte.equals("")) {
                        view_akte.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckReqResponse> call, Throwable t) {

            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void capture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                //foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && requestCode == RESULT_OK ) {

            try {
                //String imageId = convertImageUriToFile(imageUri, cameraActivity);

                bitmap = (Bitmap) data.getExtras().get("data");
                //foto.setImageBitmap(bitmap);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadReq(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadPassport(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    private void uploadPhoto(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadPhoto(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    private void uploadSN(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadSN(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    private void uploadKK(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadKK(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    private void uploadKTP(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadKTP(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    private void uploadAkte(final String req, final String key) {
        Call<RegisterResponse> up = api.uploadAKte(req, key);
        up.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.e("LOG...", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == button_akte) {
            //showFileChooser();
            String req = getStringImage(bitmap);
            uploadAkte(req, api_key);
        }
        if (view == button_passport) {
            showFileChooser();
            String req = getStringImage(bitmap);
            uploadReq(req, api_key);
        }
    }
}
