Uri gmmIntentUri = Uri.parse(String.format(Locale.US, "google.navigation:q=%f,%f", panicLocation.latitude, panicLocation.longitude));
Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
mapIntent.setPackage("com.google.android.apps.maps");
startActivity(mapIntent);