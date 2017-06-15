public class SetupProfile extends BaseActivity implements ProfileView, ProfilePresenter.ProgressListener {

    private static final int RC_CHANGE_IMAGE = 100;
    private static final int RC_CHANGE_CERTIFICATE = 200;
    private static final int RC_CHANGE_DOC = 300;
    //private static final int RC_FILECHOOSER = 400;
    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 500;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
//  @Bind(R.id.lin_image_certificate)
//  LinearLayout linImageCertificate;
//  @Bind(R.id.lin_image_documents)
//  LinearLayout linImageDocument;
    @Bind(R.id.img_profile)
    CircleImageView imgProfile;
    @Bind(R.id.input_portofolio)
    EditText textPortofolio;

    private Uri mCropImageUri;
    private Uri uri;
    private ProgressDialog progressDialog;
    private android.app.ProgressDialog percentProgressDialog;

    @Inject
    ProfilePresenter profilePresenter;

    private File pictureImage;
    private UpdateProfile updateProfile;

    private List<Certificates> certificateList = new ArrayList<>();

    private int positionPost = 0;

    private boolean statusLoading = true;

    private boolean isOnNext = false;

    public static String IMAGE_DIRECTORY_NAME = "Robs Jobs";

    @Bind(R.id.img_doc)
    ImageView imgDoc;
    @Bind(R.id.img_doc_1)
    ImageView imgDoc1;
    @Bind(R.id.img_doc_2)
    ImageView imgDoc2;
    @Bind(R.id.tv_filename_1)
    TextView tvFilename1;
    @Bind(R.id.tv_filename_2)
    TextView tvFilename2;
    @Bind(R.id.tv_filename_3)
    TextView tvFilename3;
    @Bind(R.id.tv_doc_type_1)
    TextView tvDocType1;
    @Bind(R.id.tv_doc_type_2)
    TextView tvDocType2;
    @Bind(R.id.tv_doc_type_3)
    TextView tvDocType3;

    ArrayList<ImageView> imgDocs = new ArrayList<>();
    ArrayList<TextView> tvFilenames = new ArrayList<>();
    ArrayList<TextView> tvDocTypes = new ArrayList<>();

    @OnClick(R.id.img_doc)
    void onDocClicked() {
        showFileChooserDialog(0);
    }

    @OnClick(R.id.img_doc_1)
    void onDocClicked1() {
        showFileChooserDialog(1);
    }

    @OnClick(R.id.img_doc_2)
    void onDocClicked2() {
        showFileChooserDialog(2);
    }

    int idx;

    void showFileChooserDialog(int idx) {
        this.idx = idx;
        if (certificateList.get(idx).getDocType() == Certificates.NULL) {
            new MaterialDialog.Builder(this)
                    .title(R.string.title_file_type)
                    .items(R.array.documents)
                    .positiveText(R.string.cancel)
                    .itemsCallback(listCallback)
                    .show();
        } else {
            new MaterialDialog.Builder(this)
                    //.title(R.string.choose_action)
                    //.items(R.array.documents)
                    .content(R.string.delete_document_certificate)
                    .positiveText(R.string.delete)
                    .onPositive((dialog, which) -> {
                        new MaterialDialog.Builder(this)
                                .content(R.string.confirm_delete)
                                .negativeText(R.string.no)
                                .positiveText(R.string.delete)
                                .onPositive((dialog2, pos) -> {
                                    if (certificateList.get(idx).getId() != 0) {
                                        deleteDoc();
                                    } else {
                                        resetDocUI();
                                    }
                                })
                                .itemsCallback(listCallback)
                                .show();
                    })
                    .negativeText(R.string.cancel)
                    //.itemsCallback(listCallback)
                    .show();
        }
    }

    private void deleteDoc() {
        statusLoading = true;
        profilePresenter.deleteDoc(certificateList.get(idx).getId());
        certificateList.get(idx).zeroId();
        resetDocUI();
    }

    private void resetDocUI() {
        certificateList.get(idx).setDocType(Certificates.NULL);
        imgDocs.get(idx).setImageResource(R.drawable.rj_addfile_icon);
        tvFilenames.get(idx).setText("-");
        tvDocTypes.get(idx).setText("");
    }

    MaterialDialog.ListCallback listCallback = (dialog, itemView, position, text) -> {
        int type = 0;
        switch (position) {
            case 0:
                type = RC_CHANGE_CERTIFICATE;
                break;
            case 1:
                type = RC_CHANGE_DOC;
                break;
        }
        performFileSearch(type);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.kana.robsjobs.R.layout.activity_setup_three2);
        ButterKnife.bind(this);
        activityComponent().inject(this);

        setSupportActionBar(mToolbar);
        setBackIcon();
        setTitle(co.kana.robsjobs.R.string.setup_profile_three_title);

        profilePresenter.attachView(this);
        profilePresenter.listener = this;

        updateProfile = getIntent().getParcelableExtra(UpdateProfile.class.getSimpleName());

        boolean statusEdit = getIntent().getBooleanExtra(STATUS_EDIT, false);
        Profile profile = Preferences.getProfile();
        Log.e("status_edit", profile.getImage());
        Glide.with(this)
                .load(profile.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_profile_white)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(imgProfile);
        Log.e("tes", profile.getImage());
        textPortofolio.setText(profile.getPortofolio());

        profilePresenter.getCertificates();

        initList();

        RobJobsApp application = (RobJobsApp) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Setup Profile 2");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initList() {
        imgDocs.add(imgDoc);
        imgDocs.add(imgDoc1);
        imgDocs.add(imgDoc2);
        //for marquee
        tvFilename1.setSelected(true);
        tvFilename2.setSelected(true);
        tvFilename3.setSelected(true);
        tvFilenames.add(tvFilename1);
        tvFilenames.add(tvFilename2);
        tvFilenames.add(tvFilename3);
        tvDocTypes.add(tvDocType1);
        tvDocTypes.add(tvDocType2);
        tvDocTypes.add(tvDocType3);
        certificateList.add(new Certificates());
        certificateList.add(new Certificates());
        certificateList.add(new Certificates());
    }

    @OnClick(co.kana.robsjobs.R.id.img_profile)
    void changeProfile(View view) {
        onSelectImageClick(view);
    }

    /*@OnClick(co.kana.robsjobs.R.id.img_profile)
    void changeProfile() {
        ImagePicker.create(SetupProfileThreeAct.this)
                .single()
                .showCamera(true)
                .start(RC_CHANGE_IMAGE);
    }*/

    @OnClick(R.id.btn_next)
    void navigateNext() {
        isOnNext = true;
        String profileImagePath = Preferences.getProfile().getImage();
        if (pictureImage == null && (profileImagePath == null || profileImagePath.equals(""))) {
            MessageDialog.showMessage(this, getResources().getString(R.string.error_msg_empty_profile_image));
        } else {
            if (pictureImage != null) {
                statusLoading = true;
                profilePresenter.postPhoto(pictureImage);
            } else {
                upload(0);
            }
        }
    }

    void upload(int position) {
        if (position > 2) {
            updateDataProfile();
            return;
        }
        Certificates certificate = certificateList.get(position);
        if (certificate.getDocType() == Certificates.NULL) {
            if (certificate.getId() != 0) {
                profilePresenter.deleteDoc(certificate.getId());
            } else {
                positionPost = positionPost + 1;
                upload(positionPost);
                return;
            }
        } else {
            if (certificate.getId() == 0) {
                profilePresenter.uploadFile(certificate.getFile(), certificate.getDocType(), certificate.getFilename());
            } else {
                positionPost = positionPost + 1;
                upload(positionPost);
                return;
            }

        }
    }

    private void updateDataProfile() {
        statusLoading = true;
        String portofolio = textPortofolio.getText().toString();
        Profile profile = Preferences.getProfile();
        profilePresenter.updateProfile(profile.getId(), updateProfile.getName(), updateProfile.getBirthdate(), updateProfile.getCity(),
                updateProfile.getProvince(), updateProfile.getSalarymin(), updateProfile.getSalarymax(), updateProfile.getEdulevel(), updateProfile.getJurusan(), updateProfile.getKompetensi(),
                updateProfile.getInterests(), updateProfile.getEmptype(), updateProfile.getEmpsector(), updateProfile.getDistance(),
                updateProfile.getBio(), updateProfile.getSkills(), updateProfile.getIsemployed(), updateProfile.getCurrentsector(),
                updateProfile.getHasexperience(), portofolio, updateProfile.getExperience(), updateProfile.getEmp_status());
    }

    public static void start(Context context, UpdateProfile updateProfile, boolean statusEdit) {
        context.startActivity(new Intent(context, SetupProfileThreeAct.class)
                .putExtra(UpdateProfile.class.getSimpleName(), updateProfile)
                .putExtra(STATUS_EDIT, statusEdit));
    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CHANGE_IMAGE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            Image image = images.size() == 0 && images.isEmpty() ? null : images.get(0);
            if (image != null) {
                Bitmap bitmap = ImageUtils.getScaledBitmap(image.getPath(), 720);
                imgProfile.setImageBitmap(bitmap);
                pictureImage = saveFile(bitmap);

                }
            }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            startCropImageActivity(imageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((CircleImageView) findViewById(R.id.img_profile)).setImageURI(result.getUri());
                mCropImageUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCropImageUri);
                    imgProfile.setImageBitmap(bitmap);
                    pictureImage = saveFile(bitmap);
                    Glide.with(this)
                            .load(pictureImage)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile_white)
//                          .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .into(imgProfile);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

        if ((requestCode == RC_CHANGE_CERTIFICATE || requestCode == RC_CHANGE_DOC) && resultCode == RESULT_OK && data != null) {
            try {
                uri = data.getData();
                Log.e("path", uri.getPath());
                String realPath = getRealPath(uri);
                File file;
                if (Utils.isNullOrEmpty(realPath)) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    String filename = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
                    file = new File(getCacheDir(), filename);
                    OutputStream outStream = new FileOutputStream(file);
                    outStream.write(buffer);
                } else {
                    file = new File(realPath);
                }
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                if (file_size >= 3072) {
                    new MaterialDialog.Builder(this)
                            .title("Fail")
                            .content("File must be less than 3 MB. Choose another file?")
                            .positiveText("CHOOSE")
                            .negativeText("CANCEL")
                            .onPositive((dialog, which) -> performFileSearch(requestCode))
                            .show();
                    return;
                }
                if (requestCode == RC_CHANGE_CERTIFICATE || requestCode == RC_CHANGE_DOC) {
                    new MaterialDialog.Builder(this)
                            .title(R.string.title_file_name)
                            .positiveText(R.string.ok)
                            .negativeText(R.string.cancel)
                            .customView(R.layout.custom_dialog_filename, false)
                            .onPositive((dialog, which) -> {
                                Utils.hideSoftKeyboard(this);
                                if (certificateList.get(idx).getId() != 0) {
                                    deleteDoc();
                                }
                                EditText editText = ButterKnife.findById(dialog, R.id.input_filename);
                                imgDocs.get(idx).setImageResource(R.drawable.ic_document);
                                if (editText != null) {
                                    String filename = editText.getText().toString();
                                    tvFilenames.get(idx).setText(filename);
                                    certificateList.get(idx).setFilename(filename);
                                }
                                certificateList.get(idx).setFile(file);
                                switch (requestCode) {
                                    case RC_CHANGE_CERTIFICATE:
                                        certificateList.get(idx).setDocType(Certificates.CERTIFICATES);
                                        tvDocTypes.get(idx).setText("cert");
                                        break;
                                    case RC_CHANGE_DOC:
                                        certificateList.get(idx).setDocType(Certificates.DOCUMENT);
                                        tvDocTypes.get(idx).setText("doc");
                                        break;
                                }
                            })
                            .onNegative((dialog, which) -> Utils.hideSoftKeyboard(this))
                            .show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        performFileSearch(reqCode);
                    }
                    break;
                }
            }
        }*/

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCropImageActivity(mCropImageUri);
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    performFileSearch(reqCode);
                }
                break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(proj[0]);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }


    private File saveFile(Bitmap bitmap) {
        FileOutputStream out = null;
        File filePath = getOutputMediaFile();

        try {
            if (filePath != null) {
                out = new FileOutputStream(filePath);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            if (out != null) {
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
    }

    @Override
    public void onSuccessProfile(Profile data) {
        if (Preferences.getWalktrough())
            LoadingAct.start(this);
        else
            WalkthroughAct.start(this);
        finish();
    }

    @Override
    public void onSuccessCertificates(List<Certificates> data) {
        int i = 0;
        for (Certificates certificates : data) {
            if (i>2){
                break;
            }
            certificates.setDocType(certificates.getDoc_type());
            certificateList.set(i,certificates);
            imgDocs.get(i).setImageResource(R.drawable.ic_document);
            String filename = certificates.getFilename();
            if (Utils.isNullOrEmpty(filename)){
                filename = "-";
            }
            tvFilenames.get(i).setText(filename);
            switch (certificates.getDoc_type()){
                case Certificates.DOCUMENT:
                    tvDocTypes.get(i).setText("doc");
                    break;
                case Certificates.CERTIFICATES:
                    tvDocTypes.get(i).setText("cert");
                    break;
            }
            i = i+1;
        }
    }

    @Override
    public void onSuccessUpload() {
        upload(0);
    }

    @Override
    public void onSuccessUploadDoc() {
        positionPost = positionPost +1;
        upload(positionPost);
    }

    @Override
    public void onSuccessDelete() {
        if (isOnNext){
            positionPost = positionPost +1;
            upload(positionPost);
        }
    }

    @Override
    public void onShowProgressDialog() {
        percentProgressDialog = new android.app.ProgressDialog(this);
        percentProgressDialog.setMessage("Uploading file..");
        percentProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        percentProgressDialog.setCancelable(false);
        percentProgressDialog.show();
    }

    @Override
    public void onDismissProgressDialog() {
        if (percentProgressDialog != null && statusLoading) {
            percentProgressDialog.dismiss();
            statusLoading = false;
        }
    }

    @Override
    public void onShowLoading() {
        progressDialog = ProgressDialog.create();
        progressDialog.show(getBaseFragmentManager());
    }

    @Override
    public void onDismissLoading() {
        if (statusLoading) {
            progressDialog.dismiss();
            statusLoading = false;
        }
    }

    @Override
    public void onFailed(String message) {
        MessageDialog.showMessage(this, message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profilePresenter.detachView();
    }

    @Override
    public void onProgress(int progress) {
        if (percentProgressDialog != null){
            percentProgressDialog.setProgress(progress);
        }
    }

    int reqCode;

    public void performFileSearch(int reqCode) {
        this.reqCode = reqCode;
        if (checkPermission()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            Intent filechooser = Intent.createChooser(intent,"Choose a file");
            startActivityForResult(filechooser, reqCode);
        }
    }

    private String getRealPath(Uri uri){
        String realPath;
        if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtils.getPath(this, uri);
            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtils.getPath(this, uri);
        return realPath;
    }

    private boolean checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
            return false;
        } else {
            return true;
        }
    }
}