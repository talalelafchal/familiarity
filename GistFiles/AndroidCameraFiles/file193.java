//
//  ManualAddItemActivity.java
//  BeforeSopiled
//
//  Created by Luyang Li on 5/23/16.
//

package cs165.edu.dartmouth.cs.beforespoiled;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.vstechlab.easyfonts.EasyFonts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cs165.edu.dartmouth.cs.beforespoiled.database.Label;
import cs165.edu.dartmouth.cs.beforespoiled.database.LabelDataSource;
import cs165.edu.dartmouth.cs.beforespoiled.database.ReminderEntry;
import cs165.edu.dartmouth.cs.beforespoiled.database.ReminderEntryAsyncTask;
import cs165.edu.dartmouth.cs.beforespoiled.helper.DateHelper;


public class ManualAddItemActivity extends Activity {
    
    // Mark: Basic variants.
    public static final int TAKE_PHOTO_REQUEST_CODE = 0;
    protected static final int RESULT_SPEECH = 1;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private TextView mDisplayDateTime;
    private Calendar mBaseDateAndTime = Calendar.getInstance();
    private Calendar mDateAndTime = Calendar.getInstance();
    private EditText itemName;
    private ImageButton cameraButton;
    private ImageButton speechButton;
    private SearchableSpinner categorySpinner;
    private Uri mImageCaptureUri;
    private String filePath;
    private ArrayAdapter adapter = null;
    private List<Label> labels = new ArrayList<>();
    private boolean photoExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_manual_add_item);
        TextView textView1 = (TextView) findViewById(R.id.textView);
        textView1.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        TextView textView2 = (TextView) findViewById(R.id.textViewCategory);
        textView2.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));

        //request caramera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                /* Show an expanation to the user *asynchronously* -- don't block
                 this thread waiting for the user's response! After the user
                 sees the explanation, try again to request the permission.*/

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 0);
            }
        }
        // request storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        //load data
        LabelDataSource dataSource = new LabelDataSource(this);
        dataSource.open();
        labels = dataSource.fetchEntries();
        dataSource.close();

        //User type item name
        itemName = (EditText) findViewById(R.id.editTextName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemName.setText(bundle.getString(AddFromHistoryActivity.ITEM_NAME));
            mBaseDateAndTime = DateHelper.dataToCalendar(bundle.getString(AddFromHistoryActivity.BUY_DATE));
        }
        itemName.clearFocus();

        //User change expire date by clicking image button.
        mDisplayDateTime = (TextView) findViewById(R.id.DateDisplayView);
        mDateAndTime.setTime(mBaseDateAndTime.getTime());
        mDateAndTime.add(Calendar.DATE, labels.get(0).getStoragePeriod());
        updateDateAndTimeDisplay();

        //Camera button
        cameraButton = (ImageButton) findViewById(R.id.ShowCameraButton);
        cameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);

                startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);

            }
        });

        //Speech button. This is our feature about voice input.
        speechButton = (ImageButton) findViewById(R.id.SpeakButton);
        speechButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        categorySpinner = (SearchableSpinner) findViewById(R.id.spinnerCategory);
        categorySpinner.setPositiveButton("OK");
        categorySpinner.setTitle("Select Item");
        // Set adaptor for new listView.
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels.toArray());
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!photoExist) {
                    LabelDataSource labelDataSource = new LabelDataSource(ManualAddItemActivity.this);
                    cameraButton.setImageResource(labelDataSource.getImageReSrcById(position));
                    cameraButton.setScaleType(ImageView.ScaleType.FIT_XY);
                    mDateAndTime.setTime(mBaseDateAndTime.getTime());
                    mDateAndTime.add(Calendar.DATE, labels.get(position).getStoragePeriod());
                    updateDateAndTimeDisplay();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cameraButton.setImageResource(R.drawable.fish);
            }
        });
    }
    
    //Mark: When click the best before image button which will lead to date and time selection.
    public void onDateClicked(View v) {

        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateAndTimeDisplay();
            }
        };

        new DatePickerDialog(ManualAddItemActivity.this, mDateListener,
                mDateAndTime.get(Calendar.YEAR),
                mDateAndTime.get(Calendar.MONTH),
                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();

    }
    //Mark: show the new date and time.
    private void updateDateAndTimeDisplay() {
        mDisplayDateTime.setText(DateUtils.formatDateTime(this,
                mDateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR));
    }
    
    public void onCancelClicked(View v) {
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_CODE:
                beginCrop(mImageCaptureUri);
                break;
            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);
                // Delete temporary image taken by camera after crop.
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists())
                    f.delete();
                break;
            case RESULT_SPEECH:
                if (null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    itemName.setText(text.get(0));
                }
                break;

        }
    }
    
    // Mark: crop the image that user had taken from their camera.
    private void beginCrop(Uri source) {
        filePath = getCacheDir() + "cropped" + System.currentTimeMillis();
        File file = new File(filePath);
        Uri destination = Uri.fromFile(file);
        Crop.of(source, destination).asSquare().start(this);
    }
    
    // Mark: after cropped will store a new image.
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            cameraButton.setImageURI(Crop.getOutput(result));
            cameraButton.setScaleType(ImageView.ScaleType.FIT_XY);
            photoExist = true;
            File file = new File(filePath);
            if(file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // Mark: When user clicked the save button, we store all the information user has created.
    public void onSaveClicked(View view){
        Toast.makeText(getApplicationContext(), getString(R.string.save_message), Toast.LENGTH_SHORT).show();

        ReminderEntry entry = new ReminderEntry();
        entry.setName(itemName.getText().toString());
        entry.setLabel(categorySpinner.getSelectedItemPosition());
        entry.setExpireDate(mDateAndTime);

        if (photoExist) {
            cameraButton.buildDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cameraButton.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, stream);
            entry.setImage(stream.toByteArray());
        }

        (new ReminderEntryAsyncTask(this)).execute(ReminderEntryAsyncTask.INSERT, entry);
        finish();
    }
}
