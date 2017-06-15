Intent intent = new Intent(Intent.ACTION_VIEW);
intent.setData(Uri.parse("geo:47.6, -122.3")); //geolocation
if (intent.resolveActivity(getPackageManager()) != null ) {
  startActivity(intent);
}