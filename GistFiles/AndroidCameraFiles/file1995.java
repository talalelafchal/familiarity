public class ExampleActivity extends AppCompatActivity {
    
    private static final int CAMERA_INTENT = 111;
    private static final int GALLERY_INTENT = 222;
    
    private PhotoData photoData;
    private ImageView photoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        
        photoData = new PhotoData(this);
        
        Button photoBtn = (Button) findViewById(R.id.photBtn);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        
        Button galleryBtn = (Button) findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              galleryPhoto();
            }
        });
        
        //This will be use to set the image
        photoPreview = (ImageView) findViewById(R.id.photoPreview);
    }
    
    private void takePhoto() {
        File photo = null;
                try {
                    photo = photoData.createImageFile();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                    photoData.saveUriPath(Uri.fromFile(photo).getPath());
                    startActivityForResult(intent, CAMERA_INTENT);
                } catch (IOException e) {
                    //TODO warn the user the photo fail
                }
    }
    
    private void galleryPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_INTENT);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the intent to camera or gallery worked then the code is
        if (Activity.RESULT_OK == resultCode) {
            if (CAMERA_INTENT == requestCode) {
                showPhoto();
            } else if (data != null && GALLERY_INTENT == requestCode) {
                //This first part is for getting the Uri path of the photo
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                
                //Why save this if we are back in the activity, cause we can reuse the method
                photoData.saveUriPath(imgDecodableString);

                showPhoto();
            } else {
                //TODO warn the user the operation failed
            }
        } else {
            //TODO warn the user the action was cancelled
        }
    }
    
    private void showPhoto() {
        //To keep this snippet short we are using Picasso
        //We have to retrive the photo uri path from our shared prefference memory now
        Picasso.with(context)
                .load(new File(photoData.getUriPath))
                .into(holder.photoImg);
        //Creating another File instance is not perfect, it works, but could be improve
    }
    
}