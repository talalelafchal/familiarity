Intent intent = new Intent(Intent.ACTION_VIEW, 
	Uri.parse("http://maps.google.com/maps?f=d&saddr=53.447,-0.878&daddr=51.448,-0.972"));
intent.setComponent(new ComponentName("com.google.android.apps.maps", 
	"com.google.android.maps.MapsActivity"));
startActivity(intent);