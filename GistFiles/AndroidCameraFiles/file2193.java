if (ContextCompat.checkSelfPermission(
  this,android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
  // 許可されている時の処理
}else{
  //許可されていない時の処理
   if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
      //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
   } else {
      //まだ許可を求める前の時、許可を求めるダイアログを表示します。
       ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);

   }
}
