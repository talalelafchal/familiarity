package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}package com.boxopen.tailieuso.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxopen.tailieuso.R;
import com.boxopen.tailieuso.api.API;
import com.boxopen.tailieuso.api.MainAPI;
import com.boxopen.tailieuso.entity.ListType;
import com.boxopen.tailieuso.entity.Type;
import com.boxopen.tailieuso.entity.TypeChild;
import com.boxopen.tailieuso.task.LoadingHomeDataTask;
import com.boxopen.tailieuso.task.PostToGetCoin;
import com.boxopen.tailieuso.ui.fragment.TypeFragment;
import com.google.android.gms.games.Player;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.boxopen.tailieuso.ui.activity.AndroidMultiPartEntity.ProgressListener;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ThanhCS94 on 9/12/2015.
 */
public class UploadActivity extends Activity {
    private static final int SELECT_PDF = 2;
    String selectedPath = "";
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvtitle;ImageView imgBack;
    private ProgressBar progressBar;
    private String filePath = null;
    //  private ImageView imgPreview;
    //  private VideoView vidPreview;
    // private Button btnUpload;
    long totalSize = 0;
    ArrayList<Type>arrType;
    Spinner id_spinner;
    com.boxopen.tailieuso.util.FButton bt;
    boolean isValid = false;
    TextView tvSelect, tvType;
    EditText edName , edDes, edAuthor;
    String name, type, des, author;
    String TYPE_NAME, TAG_SUBTYPE, TAG_TYPE ;
    String PRICE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        arrType = LoadingHomeDataTask.arrType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvtitle = (TextView)findViewById(R.id.tvtitle);
        imgBack = (ImageView)findViewById(R.id.imgback);
        progressBar.setVisibility(View.GONE);
         bt = (com.boxopen.tailieuso.util.FButton)findViewById(R.id.button);
         bt.setButtonColor(Color.parseColor("#006064"));
         bt.setText("UPLOAD");
         tvSelect = (TextView)findViewById(R.id.tvselect);
         edName = (EditText)findViewById(R.id.edname);
        tvType = (TextView)findViewById(R.id.tvtype);
         edAuthor = (EditText)findViewById(R.id.edauthour);
         edDes = (EditText)findViewById(R.id.eddes);
        id_spinner=(Spinner)findViewById(R.id.spinner);
        tvtitle.setText("Upload tài liệu");
       final String[] platforms =  {"1$", "2$", "5$", "10$"};
        PRICE =platforms[0];
        id_spinner.setAdapter(new MyAdapter(this, R.layout.spinercustome, platforms));

        id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   PRICE =platforms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PRICE =platforms[0];
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> array_city = new ArrayList<>();
                final ArrayList<String> array_childtag = new ArrayList<>();
                for(int i = 0 ; i < arrType.size(); i++)
                {
                    array_city.add(arrType.get(i).getTypeName());
                    array_childtag.add(arrType.get(i).getTypeTag());
                }

                final Dialog dialog = new Dialog(UploadActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_list_type);
                ListView listView = (ListView) dialog.findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_list_item_1, array_city);
                listView.setAdapter(adapter);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TYPE_NAME = array_city.get(position).toString();
                        TAG_TYPE= array_childtag.get(position).toString();
                        new LoadDataListChildType(array_childtag.get(position)).execute();
                        dialog.dismiss();
                    }
                });
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = URLEncoder.encode(edName.getText().toString() ,"UTF-8").replace(" ", "%20");
                    type = URLEncoder.encode(tvType.getText().toString() ,"UTF-8").replace(" ", "%20");
                    des =URLEncoder.encode(edDes.getText().toString() ,"UTF-8").replace(" ", "%20");
                    author = URLEncoder.encode(edAuthor.getText().toString() ,"UTF-8").replace(" ", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                    name =     nameTemp, "UTF-8").replace(" ", "%20");
//                    type =    URLEncoder.encode(typeTemp, "UTF-8");//.replace(" ", "%20");
//                    des =    URLEncoder.encode(desTemp, "UTF-8");//.replace(" ", "%20");
//                    author =    URLEncoder.encode(edAuthorTemp, "UTF-8");//.replace(" ", "%20");

                    Log.wtf("data post : " , name+"\n"+type+"\n"+des+"\n"+author);

                //if(isValid==true&&!name.equalsIgnoreCase("")&&!type.equalsIgnoreCase("")&!des.equalsIgnoreCase("")&&!author.equalsIgnoreCase(""))
                new UploadFileToServer().execute();
               // else
               //     Toast.makeText(UploadActivity.this, "Xin vui lòng kiểm tra lại dữ liệu nhập của bạn.", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void openPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF "), SELECT_PDF);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PDF) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri.getLastPathSegment().endsWith("pdf")) {
                    filePath = Uri.decode(selectedImageUri.toString());
                    Charset.forName("UTF-8").encode(filePath);
                    String path = filePath.replace("file:///", "");
                    path = path.replace("%20", " ");
                    tvSelect.setText(path);
                    try {
                        URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tvSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    if(edName.getText().toString().equalsIgnoreCase("")||edName.getText().toString()==null)
                    {
                        String temp = path.replace("/", "=");
                        String[] arr = temp.split("=");
                        String name = arr[arr.length-1].replace(".pdf", "");
                        name = name.replace("%20", " ");
                        name = name.replace("-", " ");
                        name = name.replace("_", " ");
                        tvSelect.setText(name);
                        edName.setText(name);
                        isValid = true;
                    }

                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            bt.setText("UPLOAD  "+String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MainAPI.UPLOAD_BOOK);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"),
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                String path = filePath.replace("file:///", "");
                path = path.replace("%20", " ");
                Log.wtf("path1 : " ,  path);
                Log.wtf("path2 : " ,  filePath);
                File sourceFile = new File(path);

                Log.wtf("source file  : " ,  sourceFile.toString());

                // Adding file data to http body
                entity.addPart("Link_offline", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("ten_sach",
                        new StringBody(name));
                entity.addPart("type",
                        new StringBody(TAG_TYPE));
                entity.addPart("chi_tiet",
                        new StringBody(des));
//                entity.addPart("link_anh",
//                        new StringBody("www.androidhive.info"));
                entity.addPart("type_child",
                        new StringBody(TAG_SUBTYPE));
                entity.addPart("nguoi_dang",
                        new StringBody(MainActivityMain.USER_ID));
                entity.addPart("price",
                        new StringBody(PRICE));
                       // new StringBody(MainActivityMain.USER_ID));
                entity.addPart("tac_gia",
                        new StringBody(author));
                Log.wtf("ten : ", name);
                Log.wtf("tye : ", TAG_TYPE);
                Log.wtf("subtype : ", TAG_SUBTYPE);
                Log.wtf("chitiet : ",des);
                Log.wtf("tacgia : ", author);
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            bt.setText("UPLOAD SUCCESS");
            new PostToGetCoin(UploadActivity.this ,PRICE, MainActivityMain.USER_ID,  "0", "0").execute();
            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);
//                    Intent intent = new Intent(UploadActivity.this, com.artifex.mupdfdemo.MuPDFActivity.class);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(filePath));
//                    //set true value for horizontal page scrolling, false value for vertical page scrolling
//                    intent.putExtra("horizontalscrolling", true);
//                    startActivity(intent);
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class LoadDataListChildType extends AsyncTask<Void, Integer, Void>{
        String tag;
        List<TypeChild> typeChild;

        public LoadDataListChildType(String tag)
        {
            this.tag =  tag;
        }
        @Override
        protected Void doInBackground(Void... params) {


            ListType even;

            try {
                URL URL = new URL(MainAPI.TYPE_DOCUMENT+tag);
                Reader reader = API.getData(URL);
                if(reader!=null)
                {
                    even = new GsonBuilder().create().fromJson(reader, ListType.class);
                    typeChild = even.getTypeChild();
                }
                else
                    typeChild=null;

            } catch (Exception e) {
                System.err.println("Error data");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final ArrayList<String> array_city = new ArrayList<>();
            final ArrayList<String> array_childtag = new ArrayList<>();
            for(int i = 0 ; i < typeChild.size(); i++)
            {
                array_city.add(typeChild.get(i).getTypeChildName());
                array_childtag.add(typeChild.get(i).getTypeChildTag());
            }

            final Dialog dialog = new Dialog(UploadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_list_type);
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                    android.R.layout.simple_list_item_1, array_city);
            listView.setAdapter(adapter);
            dialog.show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TYPE_NAME+= ", "+array_city.get(position).toString();
                    TAG_SUBTYPE = array_childtag.get(position).toString();
                    tvType.setText(TYPE_NAME);
                    dialog.dismiss();
                }
            });
            super.onPostExecute(result);
        }
    }


    public class MyAdapter extends ArrayAdapter<String> {
        String[] platforms;
        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects)
        {
            super(ctx, txtViewResourceId, objects);
            this.platforms = objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt)
        { return getCustomView(position, cnvtView, prnt); }
        @Override public View getView(int pos, View cnvtView, ViewGroup prnt)
        { return getCustomView(pos, cnvtView, prnt); }
        public View getCustomView(int position, View convertView, ViewGroup parent)
        { LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.spinercustome, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.textView);
            main_text.setText(platforms[position]);
            return mySpinner;
        }
    }
}

//http://stackoverflow.com/questions/13126327/utf-8-encoding-with-filebody-in-multipartentity