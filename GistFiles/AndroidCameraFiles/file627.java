@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UXTesting.onActivityResult(requestCode, resultCode, data);
}