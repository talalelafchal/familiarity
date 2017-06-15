package com.example.android.camera2basic;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.*;


public class ImageGalleryDemoActivity extends Activity implements View.OnTouchListener {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    private static final String TAG = "LogCatTest";

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;

    Paint paint;

    private Button camButton;
    private Button captureButton;
    private Button ClearPaintButton;

    private FrameLayout container;

    boolean imgSizeinit = false;

    int imageNumber = R.drawable.image_0031_layer_1;

    public class Label {
        float startX, startY;
        float endX, endY;
    }
    Label lArray[] = new Label[20];
    boolean inputting = false;

    float downx = 0.0f, downy = 0.0f,upx = 0.0f,upy = 0.0f;
    float startx= 0.0f, startY = 0.0f, endx = 0.0f, endY = 0.0f;
    float dw, dh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagegallery);

        imageView = (ImageView) this.findViewById(R.id.imgView);
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        dw = currentDisplay.getWidth();
        dh =currentDisplay.getHeight();

        bitmap = Bitmap.createBitmap((int) dw, (int) dh,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(GREEN);
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(this);

        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        MyGalleryAdapter galAdapter = new MyGalleryAdapter(this);
        gallery.setAdapter(galAdapter);

        camButton = (Button)findViewById(R.id.camButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageGalleryDemoActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });



        ClearPaintButton = (Button) findViewById(R.id.clearButton);
        ClearPaintButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                canInit();
            }
        });

        container = (FrameLayout)findViewById(R.id.fLayout);
        captureButton = (Button)findViewById(R.id.saveButton);
        captureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFrame(container);
            }
        });
    }

    public class MyGalleryAdapter extends BaseAdapter {
        Context context;
        int[] labelID = { R.drawable.label1, R.drawable.label2, R.drawable.label3, R.drawable.label4, R.drawable.label5, R.drawable.label6 };
        public MyGalleryAdapter(Context c){
            context = c;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview2 = new ImageView(context);
            imageview2.setLayoutParams(new Gallery.LayoutParams(400, 3000));

            imageview2.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview2.setPadding(5, 5, 5, 5);
            imageview2.setImageResource(labelID[position]);

            final int pos = position;
            imageview2.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event){
                    imageNumber = labelID[pos];
                    return false;
                }
            });

            return imageview2;
        }



        public int getCount() {
            return labelID.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }




    public void saveFrame(View v) {
        DecimalFormat decimalFormat = new DecimalFormat("00");//
        DecimalFormat NumFormat = new DecimalFormat("0000");//

        Calendar rightNow = Calendar.getInstance();// 날짜 불러오는 함수
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH);// 달
        int date = rightNow.get(Calendar.DATE);// 일

        long now = System.currentTimeMillis();
        Date date2 = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HHmmss");
        String strNow = sdfNow.format(date2);

        String day = decimalFormat.format(year) + decimalFormat.format(month) + decimalFormat.format(date) + "_" + strNow;
        Log.d(TAG, day);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        path = path + "/DCIM/Camera";

        container.buildDrawingCache();
        Bitmap captureView = container.getDrawingCache();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path + day + ".jpg");
            fos = new FileOutputStream(path + day + ".jpg");
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "IMAGE SAVED", Toast.LENGTH_LONG).show();
    }


    public void canInit(){
        dw = imageView.getWidth();
        dh = imageView.getHeight();
        bitmap = Bitmap.createBitmap((int) dw, (int) dh,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(GREEN);
        imageView.setImageBitmap(bitmap);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView2);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    public Bitmap getResizedImage(Bitmap bitmap, double length)
    {
        int x= (int) length;  int y=70; //바꿀 이미지 사이즈
        Log.d(TAG, "Length: " + length);
        Bitmap output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(bitmap, 0, 0, null);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(0, 0, x, y);//이 크기로 변경됨
        canvas.drawBitmap(bitmap, src, dst, null);
        return output;
    }

    //@Override
    protected void onDraw(Canvas canvas) {
        double angle = 0;
        double dx = endx - startx, dy = endY - startY;
        double length = Math.sqrt((dx * dx) + (dy * dy));
        angle = Math.atan2(dy, dx) / Math.PI * 180;
        while (angle < 0) {
            angle += 360;
        }

        if(imgSizeinit==false) {
            canInit();
            imgSizeinit=true;
        }

        if(inputting=true) {
            Bitmap b = BitmapFactory.decodeResource(getResources(), imageNumber);
            Matrix rotator = new Matrix();
            rotator.postRotate((float) angle, 0, 0);
            rotator.postTranslate(startx, startY);
            canvas.drawBitmap(getResizedImage(b, length), rotator, null);
            Log.d(TAG, "X:" + startx + ", Y:" + startY + ", Angle:" + angle);
        }

    }

    protected void newText () {

        //EditText mTitle = (EditText)findViewById(R.id.labelEditText);

        EditText editText = new EditText(this);

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //키보드를 띄운다.
        imm.showSoftInput(editText, 0);

        //TextView tv1 = new TextView(this);
        //tv1.setText(editText.getText());



    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                inputting = true;
                startx = downx = event.getX();
                startY = downy = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
//                onDraw(canvas);
//                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;

            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                endx = event.getX();
                endY = event.getY();

                imageView.invalidate();
                onDraw(canvas);
                inputting = false;


                newText();

                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(50);
                float middleX = startx;
                float middelY = startY + 40;
                canvas.drawText("Some Text", middleX, middelY, paint);

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }


}
