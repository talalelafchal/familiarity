/// intents.xml en res/values folder. 
<resources>
    <string-array name="intents">
        <item>Open Browser</item>
        <item>Call Someone</item>
        <item>Dial</item>
        <item>Show Map</item>
        <item>Search on Map</item>
        <item>Take picture</item>
        <item>Show contacts</item>
        <item>Edit first contact</item>
    </string-array>
    
</resources> 

// layout

<?xml version="1.0" encoding="utf-8"?>
<GridLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:alignmentMode="alignBounds"
    android:columnCount="1" >

      <Spinner
        android:id="@+id/spinner"
        android:layout_gravity="fill_horizontal"
        android:drawSelectorOnTop="true"
        >
      </Spinner>
    
    <Button
        android:id="@+id/trigger"
        android:onClick="onClick"
        android:text="Trigger Intent">
    </Button>

  
</GridLayout> 

// manifest del proyecto
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="pe.edu.upc.moviles.intentimplicit"
    android:versionCode="1"
    android:versionName="1.0" >

    <uses-sdk android:minSdkVersion="15" />

    <uses-permission android:name="android.permission.CALL_PRIVILEGED" >
    </uses-permission>
    <uses-permission android:name="android.permission.CALL_PHONE" >
    </uses-permission>
    <uses-permission android:name="android.permission.CAMERA" >
    </uses-permission>
    <uses-permission android:name="android.permission.READ_CONTACTS" >
    </uses-permission>
    <uses-permission android:name="android.permission.INTERNET"/>

    <application
        android:icon="@drawable/icon"
        android:label="@string/app_name" >
        <activity
            android:name=".CallIntentsActivity"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest> 

// CallIntentsActivity
public class CallIntentsActivity extends Activity {
  private Spinner spinner;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    spinner = (Spinner) findViewById(R.id.spinner);
    ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
        R.array.intents, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }

  public void onClick(View view) {
    int position = spinner.getSelectedItemPosition();
    Intent intent = null;
    switch (position) {
    case 0:
      intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("http://www.upc.edu.pe"));
      break;
    case 1:
      intent = new Intent(Intent.ACTION_CALL,
          Uri.parse("tel:(+49)12345789"));
      break;
    case 2:
      intent = new Intent(Intent.ACTION_DIAL,
          Uri.parse("tel:(+49)12345789"));
      startActivity(intent);
      break;
    case 3:
      intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("geo:50.123,7.1434?z=19"));
      break;
    case 4:
      intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("geo:0,0?q=query"));
      break;
    case 5:
      intent = new Intent("android.media.action.IMAGE_CAPTURE");
      break;
    case 6:
      intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("content://contacts/people/"));
      break;
    case 7:
      intent = new Intent(Intent.ACTION_EDIT,
          Uri.parse("content://contacts/people/1"));
      break;

    }
    if (intent != null) {
      startActivity(intent);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == 0) {
      String result = data.toURI();
      Toast.makeText(this, result, Toast.LENGTH_LONG);
    }
  }

} 

/// 