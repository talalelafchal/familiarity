/* 1 . Intents disponibles en android */

public static void invokeWebBrowser(Activity activity){
   Intent intent = new Intent(Intent.ACTION_VIEW);
   intent.setData(Uri.parse("http://www.google.com"));
   activity.startActivity(intent);
}

public static void invokeWebSearch(Activity activity){
   Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
   intent.setData(Uri.parse("http://www.google.com"));
   activity.startActivity(intent);
}

public static void dial(Activity activity){
   Intent intent = new Intent(Intent.ACTION_DIAL);
   activity.startActivity(intent);
}
   
public static void call(Activity activity){
   Intent intent = new Intent(Intent.ACTION_CALL);
   intent.setData(Uri.parse("tel:555-555-555"));
   activity.startActivity(intent);
}

public static void showMapAtLatLong(Activity activity){
   Intent intent = new Intent(Intent.ACTION_VIEW);
   intent.setData(Uri.parse("geo:0,0?z=4&q=restaurantes"));
   activity.startActivity(intent);
}