
@Override
public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
   switch (requestCode) {
       case 0: { //ActivityCompat#requestPermissions()の第2引数で指定した値
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               //許可された場合の処理
           }else{
               //拒否された場合の処理
           }
           break;
       }
   }
}
