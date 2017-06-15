    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UXTesting.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UXTesting.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }