package net.vvakame.polkodotter;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.inject.Inject;

public class DotActivity extends GuiceActivity {

	@SuppressWarnings("unused")
	private final Activity self = this;

	private List<CircleF> circleList = new ArrayList<CircleF>();
	private boolean first = true;

	// Powered by BitmapFromUriExtraProvider
	@Inject
	private Bitmap mBGBitmap;

	private Bitmap mBGThumb;
	private Bitmap mClipBitmap;
	private Canvas mCanvas;
	private Paint mPaint;

	@InjectView(R.id.frame)
	LinearLayout mFrame;

	@InjectView(R.id.baseImage)
	ImageView mBaseImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dot); // Injection now!!

		mBaseImage.setBackgroundDrawable(new BitmapDrawable(mBGBitmap));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!first) {
			return;
		}

		final int height;
		final int width;

		if (mFrame.getWidth() < mBGBitmap.getWidth()
				&& mFrame.getHeight() < mBGBitmap.getHeight()) {
			width = mFrame.getWidth();
			height = mFrame.getHeight();
			mBGThumb = Bitmap
					.createScaledBitmap(mBGBitmap, width, height, true);
		} else {
			width = mBGBitmap.getWidth();
			height = mBGBitmap.getHeight();
			mBGThumb = mBGBitmap;
		}

		Face[] faces = new Face[1];
		FaceDetector detector = new FaceDetector(width, height, faces.length);
		int num = detector.findFaces(mBGThumb, faces);

		for (int i = 0; i < num; i++) {
			Face face = faces[i];
			PointF point = new PointF();
			face.getMidPoint(point);
			float eyesDistance = face.eyesDistance();
			// 美人顔の人の比率 目と目の間の長さ = 鼻の幅, 鼻の幅 * 4 = 顔の横幅
			// 顔の横幅 * 1.5 = 顔の高さ ↓でもこのMagicNumberは適当に決めた
			circleList.add(new CircleF(point.x, point.y,
					(float) (eyesDistance * 1.7)));
		}

		final Bitmap.Config c = Bitmap.Config.ARGB_8888;

		// Clip
		mClipBitmap = Bitmap.createBitmap(width, height, c);
		mCanvas = new Canvas();
		mCanvas.setBitmap(mClipBitmap);
		mPaint = new Paint();
		mPaint.setColor(Color.argb(255, 255, 255, 255));
		mPaint.setAntiAlias(true);

		for (CircleF circle : circleList) {
			mCanvas.drawCircle(circle.cx, circle.cy, circle.radius, mPaint);
		}

		// 前景の作成
		final int color = Color.argb(0, 120, 255, 255);
		int[] pixels = new int[width * height];
		mClipBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		final int length = pixels.length;
		for (int i = 0; i < length; i++) {
			// ^ 0xff000000 Alphaの反転
			// & 0xff000000 Alpha以外の切り捨て
			// | color Alpha以外の値の設定
			pixels[i] = pixels[i] ^ 0xff000000 & 0xff000000 | color;
		}
		mClipBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		mBaseImage.setImageBitmap(mClipBitmap);

		first = false;
	}

	class CircleF {
		float cx;
		float cy;
		float radius;

		public CircleF(float cx, float cy, float radius) {
			this.cx = cx;
			this.cy = cy;
			this.radius = radius;
		}
	}
}