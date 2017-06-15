package com.lostmind.kreatip.baru;

import rajawali.RajawaliActivity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GambarBalok extends RajawaliActivity implements
		View.OnClickListener {
	private BalokRenderer mRenderer;

	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 1000;

	// We can be in one of these 3 states

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// Remember some things for zooming

	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up our Rajawali renderer

		mRenderer = new BalokRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
		Toast.makeText(
				this,
				"Geser kanan, kiri, atas, atau bawah untuk rotate pertama kali, dan perbesar bangun dengan dua jari",
				Toast.LENGTH_LONG).show();
		// Add touch and gesture detection

		mGestureDetector = new GestureDetector(new MyGestureDetector());
		mGestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// Handle touch events here...

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					mRenderer.pinchOrDragFinished();
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						float x = event.getX() - start.x, y = event.getY()
								- start.y;
						if (Math.abs(x) > 10f || Math.abs(y) > 10f)
							mRenderer.drag(x, y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							float scale = newDist / oldDist;
							mRenderer.pinch(scale);
						}
					}
					break;
				}
				// Make sure we call our gesture detector, too

				return mGestureDetector.onTouchEvent(event);
			}
		};

		// Hook up our touch-related listeners

		mSurfaceView.setOnClickListener(this);
		mSurfaceView.setOnTouchListener(mGestureListener);

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.BOTTOM);

		TextView label = new TextView(this);
		label.setText("Geser ke atas, bawah, kanan, dan kiri untuk memutar bangun");
		label.setTextSize(8);
		label.setGravity(Gravity.CENTER);
		ll.addView(label);

		TextView label2 = new TextView(this);
		label2.setText("Gunakan 2 jari untuk memperbesar/memperkecil bangun");
		label2.setTextSize(8);
		label2.setGravity(Gravity.CENTER);
		ll.addView(label2);

		mLayout.addView(ll);

	}

	// Determine the space between the first two fingers

	private float spacing(MotionEvent event) {
		return distance(event.getX(0), event.getX(1), event.getY(0),
				event.getY(1));
	}

	// Determine the distance between two points

	private float distance(float x1, float x2, float y1, float y2) {
		float x = x1 - x2, y = y1 - y2;
		return FloatMath.sqrt(x * x + y * y);
	}

	// Calculate the mid point of the first two fingers

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// We need an empty onClick() for touch to work properly

	public void onClick(View unused) {
	}

	// Our gesture detector class

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				float x1 = e1.getX(), y1 = e1.getY(), x2 = e2.getX(), y2 = e2
						.getY();

				if (distance(x1, x2, y1, y2) > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) + Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					mRenderer.swipe(x2 - x1, y1 - y2);
					return true;
				}
			} catch (Exception e) {
			}
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mRenderer.singleTap();
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			mRenderer.doubleTap();
			return true;
		}
	}
}