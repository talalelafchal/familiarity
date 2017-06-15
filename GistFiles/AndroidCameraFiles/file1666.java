// 1. stop animations and other CPU-consuming actions
// 2. commit unsaved changes
// 3. release resources (receivers, handles, sockets..)
@Override
public void onPause() {
  super.onPause();  // always call this first
  if (mCamera != null) {
    m.Camera.release();
    m.Camera = null;
  }
}

// kind of counter-acts the onPause() method
@Override
public void onResume() {
  super.onResule();
  
  if (mCamera == null) {
    initializeCamera();
  }
}
