@Override
public void onStart() {
    super.onStart();
    // Add this line only when you have to support version below API 14
    UXTesting.onStart(this);
}

@Override
public void onStop() {
    super.onStop();
    // Add this line only when you have to support version below API 14
    UXTesting.onStop();
}