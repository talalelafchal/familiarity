// Modified by Hyrole SoASWT 
// Assume that here is an action to retrieve gps location
...
...
...
// get the location parameters
// use any methods that suitable
final String location = getIntent().getExtras().getString("location");
...
...
// function call
plotLocation(location);

...
...
...

// Here is the funtion
public  void plotLocation(String getLocation) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ getLocation +"(Lokasi+bencana)");

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }