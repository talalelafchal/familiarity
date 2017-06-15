
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityChooseImage extends AppCompatActivity implements View.OnClickListener {
    private static int RESULT_CAM = 0;
    private static int RESULT_GAL = 1;
    CustomList customList ;
    Button btn1;
    String[] web = {"Camera","Gallery"} ;
    Integer[] imageId = {R.drawable.alert_camera,R.drawable.alert_gallery };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(this);
        customList = new CustomList(this, web, imageId);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button1:
                new AlertDialog.Builder(this).setAdapter(customList,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0:
                                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(takePicture, RESULT_CAM);//zero can be replaced with any action code
                                        break;
                                    case 1:
                                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(pickPhoto , RESULT_GAL);//one can be replaced with any action code
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == RESULT_CAM && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                assert imageView != null;
                imageView.setImageBitmap(photo);
            }

            if (requestCode == RESULT_GAL && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                assert imageView != null;
                imageView.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    public class CustomList extends ArrayAdapter<String> {
        private String[] names;
        private Integer[] imageid;
        private Activity context;

        public CustomList(Activity context, String[] names, Integer[] imageid) {
            super(context, R.layout.alert_list_layout, names);
            this.context = context;
            this.names = names;
            this.imageid = imageid;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater1 = context.getLayoutInflater();
            View listViewItem = inflater1.inflate(R.layout.alert_list_layout, null, true);
            TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
            ImageView image = (ImageView) listViewItem.findViewById(R.id.imageView);

            textViewName.setText(names[position]);
            image.setImageResource(imageid[position]);
            return  listViewItem;
        }
    }

}
