@Override
public boolean onKeyDown(int keyCode, KeyEvent event)
{
  switch (keyCode)
  {
    case KeyEvent.KEYCODE_BACK:
      // do nothing
      break;
    case KeyEvent.KEYCODE_FOCUS:
    case KeyEvent.KEYCODE_CAMERA:
      // Handle these events so they don't launch the Camera app
      return true;
    // Use volume up/down to turn on light
    case KeyEvent.KEYCODE_VOLUME_DOWN:
      onVolumeDown();
      return true;
    case KeyEvent.KEYCODE_VOLUME_UP:
      onVolumeUp();
      return true;
  }
  return super.onKeyDown(keyCode, event);
}
