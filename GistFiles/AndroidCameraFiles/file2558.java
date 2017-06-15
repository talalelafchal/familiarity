    String userChoosenTask;
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnGetPhoto) {
            /* selecionar opção photo ou galeria */
            final CharSequence[] items = { "Tirar foto", "Selecionar da galeria", "Cancelar" };

            AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
            builder.setTitle("Adicionar foto!");

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Tirar foto")) {
                        userChoosenTask="Tirar foto";
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                    } else if (items[item].equals("Selecionar da galeria")) {
                        userChoosenTask="Selecionar da galeria";
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

                    } else if (items[item].equals("Cancelar")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        /* caso retorne algo jogar bara o CircleImageView */
        Bitmap img = null;
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (imageReturnedIntent != null) {

            switch(requestCode) {
                case 0: /* from camera */
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        imgPerfil.setImageURI(selectedImage);

                        Bundle bundle = imageReturnedIntent.getExtras();
                        img = (Bitmap) bundle.get("data");
                    }

                    break;
                case 1:/* gallery */
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        imgPerfil.setImageURI(selectedImage);

                        File file = new File(selectedImage.getPath());

                        img = BitmapFactory.decodeFile(file.getName());
                    }
                    break;
            }

//            imgPerfil.setImageBitmap(img);
            savePhoto(img);

        }
    }