@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
}

private void openDialog() {
        View view = getLayoutInflater().inflate(R.layout.sheet_main, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        TextView camera_sel = (TextView) view.findViewById(R.id.camera);
        TextView gallery_sel = (TextView) view.findViewById(R.id.gallery);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
}
