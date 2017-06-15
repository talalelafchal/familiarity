Uri uri = /*URI an der die Kamera-App das Foto abgelegt hat*/
Intent intent = new Intent("com.android.camera.action.CROP");
intent.setDataAndType(uri, "image/*");
