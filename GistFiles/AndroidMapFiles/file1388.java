public class LongPressMapView extends MapView {
	private boolean mWasLongClick = false;

	private LongPressMapView.OnLongpressListener longpressListener;

	public LongPressMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public LongPressMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LongPressMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnLongpressListener(LongPressMapView.OnLongpressListener listener) {
		longpressListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if(mWasLongClick) {
			mWasLongClick = false;
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	final GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent e) {
			if(longpressListener != null) {
				longpressListener.onLongpress(LongPressMapView.this,
						getProjection().fromPixels((int) e.getX(), (int) e.getY()));
			}

			mWasLongClick = true;
		}
	});

	public static interface OnLongpressListener {
		public void onLongpress(MapView view, GeoPoint longpressLocation);
	}
}