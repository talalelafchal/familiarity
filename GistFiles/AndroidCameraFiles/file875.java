            --> Android M needs explicit gallery perms with in the phone <--
            "This is intended behavior to avoid user frustration where they revoked the camera permission from an app
            and the app still being able to take photos via the intent. Users are not aware that the photo 
            taken after the permission revocation happens via different mechanism and would question 
            the correctness of the permission model. This applies to MediaStore.ACTION_IMAGE_CAPTURE, 
            MediaStore.ACTION_VIDEO_CAPTURE, and Intent.ACTION_CALL the docs for which document 
            the behavior change for apps targeting M."
            
            builder.setItems(R.array.camera_options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    switch (which) {
                        case 0:
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setType("video/*");
                            startActivity(intent);
                            break;
                    }