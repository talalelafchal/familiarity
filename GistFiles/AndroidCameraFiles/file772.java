TextView mTextView; // text view in the layout

@Override
public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  
  // set UI
  // e.g.: res/layout/main_activity.xml
  setContentView(R.layout.main_activity);
  
  // set up member variables
  mTextView = (TextView) findViewById(R.id.text_message);
  
  // version checking
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    getActionBar().setHomeButtonEnabled(false);
  }
}

@Override
public void onDestroy() {
  super.onDestory(); // ALWAYS call this
  
  // TODO: destroy background threads (otherwise, this method overriding is often optional)
}