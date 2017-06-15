package id.co.firzil.antar.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.co.firzil.antar.R;
import id.co.firzil.antar.RootBaseActivity;
import id.co.firzil.antar.app.AppConfig;
import id.co.firzil.antar.app.AppController;
import id.co.firzil.antar.helpers.MultipartRequest;
import id.co.firzil.antar.helpers.managers.SessionManager;
import id.co.firzil.antar.helpers.utils.GeneralUtils;
import id.co.firzil.antar.models.Customer;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class RegisterActivity extends RootBaseActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.et_nama_register)
    EditText etNama;

    @Bind(R.id.et_email_register)
    EditText etEmail;

    @Bind(R.id.et_password_register)
    EditText etPassword;

    @Bind(R.id.et_confirm_password_register)
    EditText etConfirmPasswd;

    @Bind(R.id.et_nohp_register)
    EditText etNohp;

    @Bind(R.id.txtError)
    TextView txtError;

    @Bind(R.id.profile_image_register)
    CircleImageView imageView;

    private File foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Konfigurasi easy image
        EasyImage.configuration(this)
                .setImagesFolderName("antar")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);
    }

    @OnClick(R.id.btn_register_register)
    void processRegister() {
        if (GeneralUtils.isNetworkConnected(this)) submitForm();
        else
            Toast.makeText(RegisterActivity.this, "Anda sedang tidak terhubung ke internet!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.profile_image_register)
    void takePicture() {
        // Tampilkan pilihan ambil gambar dari kamera atau galeri
        EasyImage.openChooser(RegisterActivity.this, "Pilih foto", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(RegisterActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotoReturned(File photoFile) {
        Glide.with(this)
                .load(photoFile)
                .fitCenter()
                .centerCrop()
                .into(imageView);

        foto = photoFile;
    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    // Submit form
    private void submitForm() {
        String nama = etNama.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPasswd.getText().toString();
        String nohp = etNohp.getText().toString();

        GeneralUtils m = new GeneralUtils();

        // Validasi setiap input dari user
        if (!m.validateName(etNama, nama)) return;
        if (!m.validateEmail(etEmail, email)) return;
        if (!m.validatePassword(etPassword, password)) return;
        if (!m.validateConfirmPassword(etConfirmPasswd, confirmPass, password)) return;
        if (!m.validateNohp(etNohp, nohp)) return;

        if (foto != null) makeRegisterRequestWithImage(nama, email, password, nohp, foto);
        else makeRegisterRequest(nama, email, password, nohp);
    }

    // Lakukan registrasi ke server dengan upload gambar
    private void makeRegisterRequestWithImage(String nama, final String email, final String password, String nohp, File file) {
        // Tampilan dialog loading
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Parameter yang dikirim ke server
        HashMap<String, String> register = new HashMap<String, String>();
        register.put("nama", nama);
        register.put("email", email);
        register.put("password", password);
        register.put("no_hp", nohp);
        register.put("gcm_token", "");
        //register.put("foto", urlFoto);

        MultipartRequest multipartRequest = new MultipartRequest(AppConfig.CUSTOMER_REGISTER, file, nama, register,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            int code = res.getInt("code");

                            if (code == 200) {
                                //startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                makeLoginRequest(email, password);
                            } else {
                                Toast.makeText(RegisterActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.i(TAG, e.getMessage());
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, " Error : " + volleyError.getMessage());
                volleyError.printStackTrace();
                JSONObject error = null;
                try {
                    error = new JSONObject(new String(volleyError.networkResponse.data));
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(error.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("X-Authorization", AppConfig.X_AUTH_KEY);
                return map;
            }
        };

        // Set request timeout
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(multipartRequest);
    }

    // Melakukan request registrasi ke server, jika berhasil lanjut ke proses login
    private void makeRegisterRequest(String nama, final String email, final String password, String nohp) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> register = new HashMap<String, String>();
        register.put("nama", nama);
        register.put("email", email);
        register.put("password", password);
        register.put("no_hp", nohp);
        register.put("gcm_token", "");
        //register.put("foto", urlFoto);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                AppConfig.CUSTOMER_REGISTER, new JSONObject(register), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");

                    if (code == 200) {
                        //startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                        makeLoginRequest(email, password);
                    } else {
                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, " Error : " + volleyError.getMessage());
                volleyError.printStackTrace();
                JSONObject error = null;
                try {
                    error = new JSONObject(new String(volleyError.networkResponse.data));
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(error.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("X-Authorization", AppConfig.X_AUTH_KEY);
                return map;
            }
        };

        // Set request timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Melakukan request login untuk mendapatkan access token
    private void makeLoginRequest(String email, String password) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> register = new HashMap<String, String>();
        register.put("email", email);
        register.put("password", password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                AppConfig.CUSTOMER_LOGIN, new JSONObject(register), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");

                    if (code == 200) {
                        JSONObject data = response.getJSONObject("data");
                        String token = data.getString("token");
                        JSONObject customer = data.getJSONObject("costomer");

                        Customer newCustomer = new Customer(
                                customer.getInt("id"),
                                customer.getString("nama"),
                                customer.getString("email"),
                                customer.getString("no_hp"),
                                customer.getString("foto"),
                                customer.getString("status"),
                                customer.getString("gcm_token"),
                                token
                        );

                        SessionManager sessionManager = new SessionManager(RegisterActivity.this);
                       // sessionManager.setLogin(true, newCustomer);

                        Toast.makeText(RegisterActivity.this, "Registrasi telah berhasil dilakukan.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, " Error : " + volleyError.getMessage());
                volleyError.printStackTrace();
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("X-Authorization", AppConfig.X_AUTH_KEY);
                return map;
            }
        };
        // Set request timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
