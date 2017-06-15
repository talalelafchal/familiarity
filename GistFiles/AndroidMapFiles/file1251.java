Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
    Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+"("+name+")"));
intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
            startActivity(intent);