public class MapDummyAppActivity extends Activity {
 
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {	
super.onCreate(savedInstanceState);
setContentView(R.layout.main);
//
Uri uri = Uri.parse("geo:0,0?q=http://code.google.com/apis/kml/documentation/KML_Samples.kml");
Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
mapIntent.setData(uri);
startActivity(Intent.createChooser(mapIntent, "Sample Map")); 
}
}