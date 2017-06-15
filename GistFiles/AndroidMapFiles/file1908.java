/**
 * This code will calculate the screen size of a device from a static context.
 * Due to lack of application context, this code will also look for a navigation
 * bar, and if one is present, it will add the height of the navigation to the
 * screen height or width, depending on the screen orientation.
 */

static { 
    final Resources resources = Resources.getSystem();
    int sScreenHeight = resources.getDisplayMetrics().heightPixels;
    int sScreenWidth = resources.getDisplayMetrics().widthPixels;
    final int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
    if(!hasHomeKey && !hasBackKey && resourceId > 0) {
        int navBarHeight = resources.getDimensionPixelSize(resourceId);
        if(resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            sScreenHeight += navBarHeight;
        } else {
            sScreenWidth += navBarHeight;
        }
    }
}