@Override
public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults)  {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    UXTesting.onRequestPermissionsResult(requestCode, permissions, grantResults);
}