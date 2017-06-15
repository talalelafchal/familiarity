if (ContextCompat.checkSelfPermission(
  this,android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
  // 許可されている時の処理
}else{
  // 拒否されている時の処理
}
