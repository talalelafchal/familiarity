/* http://developer.android.com/guide/topics/ui/ui-events.html

An event listener is an interface in the View class that contains a single callback method.
These methods will be called by the Android framework when the View to which the listener has been registered
is triggered by user interaction with the item in the UI.

public void onClick()
public boolean onLongClick()
onFocusChange()
public boolean onKey()             When focused and pressed a key
onKeyDown()
onKeyUp()
public boolean onTouch()
onCreateContextMenu()
more..
*/

//You can put it the onClick in the xml layout or you can do it by code:

// Create an anonymous implementation of OnClickListener
private OnClickListener mCorkyListener = new OnClickListener() {
    public void onClick(View v) {
      // do something when the button is clicked
    }
};

protected void onCreate(Bundle savedValues) {
    ...
    // Capture our button from layout
    Button button = (Button)findViewById(R.id.corky);
    // Register the onClick listener with the implementation above
    button.setOnClickListener(mCorkyListener);
    ...
}

//OR IMPLEMENT IT IN YOUR CLASS

public class ExampleActivity extends Activity implements OnClickListener {
    protected void onCreate(Bundle savedValues) {
        ...
        Button button = (Button)findViewById(R.id.corky);
        button.setOnClickListener(this);
    }

    // Implement the OnClickListener callback
    public void onClick(View v) {
      // do something when the button is clicked
    }
}

//OR
//All the way
button.setOnClickListener(new OnClickListener() {

    	@Override
	public void onClick(View v) {
		Intent login = new Intent(DashboardActivity.this,LoginActivity.class);
		
		startActivity(login);

		// remove the activity from the back stack
		DashboardActivity.this.finish();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}
});
