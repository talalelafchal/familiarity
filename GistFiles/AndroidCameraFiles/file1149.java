// Set the user interface layout for this Activity (activity_main.xml)
//Usually in the onCreate method
setContentView(R.layout.activity_main);

//R.java file have all the resources IDs provided to resources(drawables, layouts, styles etc)
//Example
R.id.edit_message //id of a layout view in the xml



//Progress dialog
progressDialog = ProgressDialog.show(this, null, getString(R.string.waiting));



/*Intents***********************************************************/
//1st parameter is the context (Activity is a subclass of Context)
//2d parameter is the class to which the system should deliver the intent
Intent intent = new Intent(this, DisplayMessageActivity.class);
    	
//findViewById returns the View found with the specific id
EditText editText = (EditText) findViewById(R.id.edit_message);
String message = editText.getText().toString();
    	
//An Intent can carry a collection of various data types as key-value pairs called extras.
//The putExtra() method takes the key name in the first parameter and the value in the second parameter.
intent.putExtra(EXTRA_MESSAGE, message);
//Needs definition
    //Key for the intent extra (see above)
    //Example
    public final static String EXTRA_MESSAGE = "gr.atc.learningapp.MESSAGE";
    	
//Start the other activity with the specific intent
//f the system identifies more than one activity that can handle the intent
//it displays a dialog for the user to select which app to use
startActivity(intent);

//OR
//With animation
ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
startActivity(intent, options.toBundle());



/*******************************************/
//OR
//If you want to start an activity and get a result back
//(Ex. your app can start a camera app and receive the captured photo as a result)
//EXAMPLE
Intent intent;

//Weather you will take a new photo or pick one from the photo library
switch (itemPosition) {
	case 0:	
		File f;			
		try {
			f = createImageFile();
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(intent,Utils.RC_PICK_FROM_CAMERA);
			//(intent, requestCode) if requestCode>=0 this will be returned in
                        //onActivityResult on the receiver class which will take the ewquestCode also to know where
                        //the result is coming from
		}
        catch (Exception e) {
			e.printStackTrace();
		}

		dialog.cancel();
		break;
	case 1:
	    intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,getString(R.string.options)),Utils.RC_PICK_FROM_FILE);

		break;
}

//In general
//start an activity and expect a result
Intent i = new Intent(this, SecondActivity.class);
startActivityForResult(i, 1);	//2d parameter is a flag that if >=0 result will go to onActivityResult

//The activity that responds must be designed to return a result !
//LIKE SO:
    Intent resultIntent = new Intent();
    // TODO Add extras or a data URI to this intent as appropriate.
    intent.putExtra(SOME_KEY, somevalue);
    setResult(Activity.RESULT_OK, resultIntent);
    finish();

/************************************************************/
//or an activity where you can select a Contact:
private void pickContact() {
    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    //static final int PICK_CONTACT_REQUEST = 1;  // The request code
}

//When the user is done with the subsequent activity and returns,
//the system calls your activity's onActivityResult() method
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
    if (requestCode == PICK_CONTACT_REQUEST) {
        // Make sure the request was successful
        //If user cancelled it will show RESULT_CANCELED
        if (resultCode == RESULT_OK) {
            // Get the URI that points to the selected contact
            Uri contactUri = data.getData();
            // We only need the NUMBER column, because there will be only one row in the result
            String[] projection = {Phone.NUMBER};

            // Perform the query on the contact to get the NUMBER column
            // We don't need a selection or sort order (there's only one result for the given URI)
            // CAUTION: The query() method should be called from a separate thread to avoid blocking
            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
            // Consider using CursorLoader to perform the query.
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            cursor.moveToFirst();

            // Retrieve the phone number from the NUMBER column
            int column = cursor.getColumnIndex(Phone.NUMBER);
            String number = cursor.getString(column);

            // Do something with the phone number...
        }
    }
}

/*******************************************/


//To verify there is an activity available that can respond to the intent, call queryIntentActivities()
//to get a list of activities capable of handling your Intent.
//If the returned List is not empty, you can safely use the intent.
PackageManager packageManager = getPackageManager();
List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
boolean isIntentSafe = activities.size() > 0;


// Start an activity if it's safe
if (isIntentSafe) {
    startActivity(mapIntent);
}


//Show an App Chooser for the Intent
Intent intent = new Intent(Intent.ACTION_SEND);
...
// Always use string resources for UI text. This says something like "Share this photo with"
String title = getResources().getText(R.string.chooser_title);
// Create and start the chooser
Intent chooser = Intent.createChooser(intent, title);
startActivity(chooser);
//This displays a dialog with a list of apps that respond to the intent passed to the createChooser() method
//and uses the supplied text as the dialog title.


//If you're done with the activity
// remove the activity from the back stack
LoginActivity.this.finish();




//Get a message from an Intent
Intent intent = getIntent();
String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
/******************************************************************/


//Check running version of Android
// Make sure we're running on Honeycomb or higher to use ActionBar APIs
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
  // Show the Up button in the action bar.
  ActionBar actionBar = getActionBar();
  actionBar.setDisplayHomeAsUpEnabled(true);
            
  // Make sure the app icon in the action bar does not behave as a button
  actionBar.setHomeButtonEnabled(false);
}


//Implicit intents do not declare the class name of the component to start, but instead declare an action to perform
//Call a number
Uri number = Uri.parse("tel:5551234");
Intent callIntent = new Intent(Intent.ACTION_DIAL, number);




//Check internet connectivity
ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

//Add internet Permission in Manifest file
<uses-permission android:name="android.permission.INTERNET"></uses-permission>




//View a map
// Map point based on address
Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
// Or map point based on latitude/longitude
Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);


//View a Web page
Uri webpage = Uri.parse("http://www.android.com");
Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);


//Send email with attachment
Intent emailIntent = new Intent(Intent.ACTION_SEND);
// The intent does not have a URI, so declare the "text/plain" MIME type
emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jon@example.com"}); // recipients
emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
// You can also attach multiple items by passing an ArrayList of Uris


//Add a calendar event
Intent calendarIntent = new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);
Calendar beginTime = Calendar.getInstance().set(2012, 0, 19, 7, 30);
Calendar endTime = Calendar.getInstance().set(2012, 0, 19, 10, 30);
calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
calendarIntent.putExtra(Events.TITLE, "Ninja class");
calendarIntent.putExtra(Events.EVENT_LOCATION, "Secret dojo");

//WEBVIEW
//Load the web view
webView = (WebView) findViewById(R.id.webView);
webView.setWebViewClient(new WebViewClient());
webView.getSettings().setJavaScriptEnabled(true);
webView.loadUrl(url);  
		
		
//Hide (dim) the navigation buttons
getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); 