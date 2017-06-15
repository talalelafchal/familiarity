package net.vvakame.polkodotter;

import java.io.InputStream;

import roboguice.inject.InjectExtra;
import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class BitmapFromUriExtraProvider implements Provider<Bitmap> {

	@Inject
	protected Activity act;

	@InjectExtra("mediaImageUri")
	protected Uri mediaUri;

	@Override
	public Bitmap get() {

		ContentResolver resolver = act.getContentResolver();
		Bitmap bitmap = null;
		if (mediaUri != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				InputStream is = resolver.openInputStream(mediaUri);
				BitmapFactory.decodeStream(is, null, options);

				DisplayMetrics metrics = new DisplayMetrics();
				act.getWindowManager().getDefaultDisplay().getMetrics(metrics);

				// 端末にあわせるため、幅が広い方を高さ、狭い方を幅とする。後の事は渡した先でやってくれ

				final int width;
				final int height;
				if (options.outWidth < options.outHeight) {
					width = options.outWidth;
					height = options.outHeight;
				} else {
					width = options.outHeight;
					height = options.outWidth;
				}

				if (metrics.widthPixels * 2 < width
						|| metrics.heightPixels * 2 < height) {

					// 端末解像度 〜 端末解像度 * 2 の範囲に収まるサイズにする
					int scaleW = width / metrics.widthPixels;
					int scaleH = height / metrics.heightPixels;
					options.inSampleSize = Math.max(scaleW, scaleH);
				}

				is = resolver.openInputStream(mediaUri);
				options.inJustDecodeBounds = false;
				// NativeHeapから確保
				// https://twitter.com/shoozhoo/statuses/22621375376
				BitmapFactory.Options.class.getField("inNativeAlloc")
						.setBoolean(options, true);
				bitmap = BitmapFactory.decodeStream(is, null, options);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}
}
