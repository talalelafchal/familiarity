public static int getActionBarHeight(Context cxt) {
	int[] abSzAttr;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		abSzAttr = new int[] { android.R.attr.actionBarSize };
	} else {
		abSzAttr = new int[] { R.attr.actionBarSize };
	}
	TypedArray a = cxt.obtainStyledAttributes(abSzAttr);
	return a.getDimensionPixelSize(0, -1);
}


public static void setStatusBarColor(Activity activity, int color) {
	Window window = activity.getWindow();
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
	}
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		window.setStatusBarColor(color);
	}
}

public static int getNavigationBarHeight(Context cxt) {
	boolean hasMenuKey = ViewConfiguration.get(cxt)
	                                      .hasPermanentMenuKey();
	boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
	int navigationBarHeight = 0;
	if (!hasMenuKey && !hasBackKey) {
		final Resources resources = cxt.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			navigationBarHeight = resources.getDimensionPixelSize(resourceId);
		}
	}
	return navigationBarHeight;
}

public static int getStatusBarHeight(Context cxt) {
	int result = 0;
	final Resources resources = cxt.getResources();
	int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
	if (resourceId > 0) {
		result = resources.getDimensionPixelSize(resourceId);
	}
	return result;
}

public static int getScreenVisibleHeight(Activity cxt) {
	Window window = cxt.getWindow();
	Rect rect = new Rect();
	window.getDecorView()
	      .getWindowVisibleDisplayFrame(rect);
	int height = rect.height();

	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
		height -= getStatusBarHeight(cxt);
	}
	return height;
}