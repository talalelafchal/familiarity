import android.net.Uri;

// Create a Uri from an intent string. Use the result to create an Intent.
//Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
Uri gmmIntentUri = Uri.parse("geo:23.583,120.583?z=10&q=23.583,120.583(一個店家位置)");
// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
mapIntent.setPackage("com.google.android.apps.maps");
// Attempt to start an activity that can handle the Intent
startActivity(mapIntent);