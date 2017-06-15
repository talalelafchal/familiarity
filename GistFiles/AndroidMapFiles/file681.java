//http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
Uri.parse("http://maps.google.com/maps?saddr="+latitude_source+","+longitude_source+"&daddr="+latitude_dest+","+longitude_dest));
startActivity(intent);


Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
Uri.parse("google.navigation:q=an+address+city"));