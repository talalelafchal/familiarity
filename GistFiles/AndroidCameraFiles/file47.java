// when system destroys an activity due to system contraints
@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
  savedInstanceState.putInt(PLAYER_SCORE, mCurrentScore);
  super.onSaveInstanceState(savedInstanceState);  // always all it at the end
}

// restoration - option 1
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  
  if (savedInstanceState != null) {
    mCurrentScore = savedInstanceState.getInt(PLAYER_SCORE);
  } else {
    // put default values..?
  }
}

// restoration - option2
// this method runs after onStart()
// this method runs ONLY if there's a saved state to restore
@Override
public void onRestoreInstanceState(Bundle savedInstanceState) {
  super.onRestoreInstanceState(savedInstanceState);
  mCurrentScore = savedInstanceState.getInt(PLAYER_SCORE);
}