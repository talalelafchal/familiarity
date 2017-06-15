      <string-array name="camera_options">
        <item>Take Photo</item>
        <item>Take Video</item>
        <item>Choose Photo</item>
        <item>Choose Video</item>
    </string-array>
    
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.camera_options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();