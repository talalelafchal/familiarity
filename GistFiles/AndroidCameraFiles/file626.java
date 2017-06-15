
Intent intent = new Intent("com.android.camera.action.CROP");  
intent.setDataAndType(Uri.fromFile(capturePicFile), "image/*"); 
//如何选择


intent.putExtra("aspectX", 1);  
intent.putExtra("aspectY", 1); 
//输出大小
intent.putExtra("outputX", 96);  
intent.putExtra("outputY", 96);  
intent.putExtra("noFaceDetection", true);  

intent.putExtra("crop", "true");  


//intent.putExtra("return-data", true); //直接返回图片
//保存到本地
intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturePicFile));
startActivityForResult(intent, GET_IMAGE_FROM_LOCAL);