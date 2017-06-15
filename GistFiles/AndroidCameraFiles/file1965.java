public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.imageView)
    ImageView imageView;
    @InjectView(R.id.button)
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.button)
    public void onClick() {
        choosePhoto();
    }

    public void choosePhoto() {

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    alertDialogBuilder
            .setTitle("Photo")
            .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);

                dialog.cancel();
                }
            })
            .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);

                dialog.cancel();

                }
            });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

            case 0:
                if (resultCode == Activity.RESULT_OK) {

                    Bitmap bmp = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imageView.setImageBitmap(bmp);

                }
                break;

            case 1:
                if (resultCode == Activity.RESULT_OK) {

                    Uri imageUri = imageReturnedIntent.getData();
                    imageView.setImageURI(imageUri);

                }
                break;

            default:
                break;
        }
    }

}