//显示网页
Uri uri = Uri.parse("http://google.com");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);

//显示地图
Uri uri = Uri.parse("geo:38.899533,-77.036476");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);
//其他 geo URI 範例
//geo:latitude,longitude
//geo:latitude,longitude?z=zoom
//geo:0,0?q=my+street+address
//geo:0,0?q=business+near+city
//google.streetview:cbll=lat,lng&cbp=1,yaw,,pitch,zoom&mz=mapZoom

//路径规划

Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=startLat%20startLng&daddr=endLat20endLng&hl=en");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);
//where startLat, startLng, endLat, endLng are a long with 6 decimals like: 50.123456

//打电话
//叫出拨号程序
Uri uri = Uri.parse("tel:0800000123");
Intent it = new Intent(Intent.ACTION_DIAL, uri);
startActivity(it);

//直接打电话出去
Uri uri = Uri.parse("tel:0800000123");
Intent it = new Intent(Intent.ACTION_CALL, uri);
startActivity(it);
//用這個，要在 AndroidManifest.xml 中，加上
//<uses-permission id="android.permission.CALL_PHONE" />

//传送SMS/MMS
//调用短信程序
Intent it = new Intent(Intent.ACTION_VIEW, uri);
it.putExtra("sms_body", "The SMS text");
it.setType("vnd.android-dir/mms-sms");
startActivity(it);

//传送消息
Uri uri = Uri.parse("smsto://0800000123");  // 多个号码用;分割
Intent it = new Intent(Intent.ACTION_SENDTO, uri);
it.putExtra("sms_body", "The SMS text");
startActivity(it);

//传送 MMS
Uri uri = Uri.parse("content://media/external/images/media/23");
Intent it = new Intent(Intent.ACTION_SEND);
it.putExtra("sms_body", "some text");
it.putExtra(Intent.EXTRA_STREAM, uri);
it.setType("image/png");
startActivity(it);

//传送 Email
Uri uri = Uri.parse("mailto:xxx@abc.com");
Intent it = new Intent(Intent.ACTION_SENDTO, uri);
startActivity(it);

Intent it = new Intent(Intent.ACTION_SEND);
it.putExtra(Intent.EXTRA_EMAIL, "me@abc.com");
it.putExtra(Intent.EXTRA_TEXT, "The email body text");
it.setType("text/plain");
startActivity(Intent.createChooser(it, "Choose Email Client"));

Intent it=new Intent(Intent.ACTION_SEND);
String[] tos={"me@abc.com"};
String[] ccs={"you@abc.com"};
it.putExtra(Intent.EXTRA_EMAIL, tos);
it.putExtra(Intent.EXTRA_CC, ccs);
it.putExtra(Intent.EXTRA_TEXT, "The email body text");
it.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
it.setType("message/rfc822");
startActivity(Intent.createChooser(it, "Choose Email Client"));

//传送附件
Intent it = new Intent(Intent.ACTION_SEND);
it.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
it.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/mysong.mp3");
sendIntent.setType("audio/mp3");
startActivity(Intent.createChooser(it, "Choose Email Client"));

//播放多媒体
Uri uri = Uri.parse("file:///sdcard/song.mp3");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
it.setType("audio/mp3");
startActivity(it);

Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);

//Market 相关
//寻找某个应用
Uri uri = Uri.parse("market://search?q=pname:pkg_name");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);
//where pkg_name is the full package path for an application
//显示某个应用的相关信息
Uri uri = Uri.parse("market://details?id=app_id");
Intent it = new Intent(Intent.ACTION_VIEW, uri);
startActivity(it);
//where app_id is the application ID, find the ID
//by clicking on your application on Market home
//page, and notice the ID from the address bar

//Uninstall 应用程序
Uri uri = Uri.fromParts("package", strPackageName, null);
Intent it = new Intent(Intent.ACTION_DELETE, uri);
startActivity(it);