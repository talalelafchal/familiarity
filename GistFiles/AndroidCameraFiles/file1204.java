// Exemplo de chamada:
requestPermissions();


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSION:
                if(grantResults[0] ==  PackageManager.PERMISSION_GRANTED){

                    //fazer o necessário
                }else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    //TODO tratar em caso de Denied
                }

                if(grantResults[1] ==  PackageManager.PERMISSION_GRANTED){
                    Log.i("CAMERA PERMISSION", "GRANTED");

                }else if(grantResults[1] == PackageManager.PERMISSION_DENIED){
                    //TODO tratar em caso de Denied
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(){
        String[] perms = { Manifest.permission.READ_CONTACTS , Manifest.permission.CAMERA};
        int hasReadSMSPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if(hasReadSMSPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(perms, REQUEST_CODE_PERMISSION);
            return;
        }
        startManageLocalContactsJob();
    }