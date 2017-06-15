package ap.cameraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    Button camera, share;
    ImageView img;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    private static final int Result=1;
    public String resuri="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (Button) findViewById(R.id.camera);
        share = (Button) findViewById(R.id.share);
        img=(ImageView)findViewById(R.id.imageView);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

                startActivity(cameraIntent);


            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(g,Result);
            }
        });


    }

    @Override
    protected void onActivityResult(int reqcode,int rescode,Intent data){
        super.onActivityResult(reqcode, rescode, data);
        if(reqcode==Result && rescode==RESULT_OK && data!=null){
            Uri selImg=data.getData();
            img.setImageURI(selImg);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_STREAM,selImg);
            i.putExtra(Intent.EXTRA_SUBJECT,"subject");
            startActivity(i.createChooser(i,"share using"));
        }
    }

}

