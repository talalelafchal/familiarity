package software.is.com.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView.OnColorChangedListener;

import software.is.com.myapplication.IcrmApp;
import software.is.com.myapplication.MainActivity;
import software.is.com.myapplication.PrefManager;
import software.is.com.myapplication.R;

public class SettingActivity extends Activity {
    int TAKE_PICTURE = 001;
    int ACTIVITY_SELECT_IMAGE = 002;
    Button save;
    PrefManager prefManager;
    EditText editText;
    String title;
    Uri photoUri;
    Button button2;
    ImageView imageView;
    String stringUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = IcrmApp.getPrefManager();
        setContentView(R.layout.activity_color_picker);
        imageView = (ImageView) findViewById(R.id.imageView);
        save = (Button) findViewById(R.id.save);
        editText = (EditText) findViewById(R.id.editText);
        button2 = (Button) findViewById(R.id.button2);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editText.getText().toString();
                prefManager.title().put(title);
                prefManager.picture().put(stringUri);
                prefManager.commit();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });


//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(
//                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, TAKE_PICTURE);
//            }
//        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });

    }

    public void onActivityResult(int requestcode, int resultcode, Intent intent) {
        super.onActivityResult(requestcode, resultcode, intent);
        if (resultcode == RESULT_OK) {
            if (requestcode == TAKE_PICTURE) {
                photoUri = intent.getData();

//                tempFile = new File(PathManager.getPath(context, photoUri));
                Log.e("dddddd", photoUri + "");
                imageView.setImageURI(photoUri);


            } else if (requestcode == ACTIVITY_SELECT_IMAGE) {
                photoUri = intent.getData();
                Log.e("ssssss",photoUri+"");
                stringUri = photoUri.toString();
                //  tempFile = new File(PathManager.getPath(context, photoUri));
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(photoUri, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Drawable drawable = new BitmapDrawable(thumbnail);
                imageView.setImageBitmap(thumbnail);

            }
        }
    }

}
