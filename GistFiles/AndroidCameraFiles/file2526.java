package net.vvakame.polkodotter;

import android.graphics.Bitmap;
import roboguice.config.AbstractAndroidModule;

public class MyModule extends AbstractAndroidModule {
	@Override
	protected void configure() {
		bind(Bitmap.class).toProvider(BitmapFromUriExtraProvider.class);
	}
}
