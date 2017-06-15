
En un metodo o en un evento poner simplemente 

{{
  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
  startActivityForResult(cameraIntent, CAMERA_REQUEST);
}}
Ahí mandas a llamar el intent correspondiente
Y en cuando tomas la foto o lo que sea ejecutas esto

 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data"); //La info que te llega la conviertes a bitmap
            cameraInformationDelegate.setPreviusImage(photo);  //Aqui tengo un delegate pero ignora esta linea,
            saveImage(photo, "como_quieras_nombrar_a_la_imagen");
        }

    }


Para guardar un bitmap usa esto


private void saveImage(Bitmap bmp, String link) {
            try {
                FileOutputStream fos;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                File file = new File(mFolderStorage + link);
                if (!file.exists())
                    file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write(bytes.toByteArray());
                fos.close();
            } catch (Exception ignored) {
            }
            LogService.e("Image save "+(index++));
            System.gc();
        }
        
        
        