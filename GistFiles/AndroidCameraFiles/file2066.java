
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PinchZoomView extends View implements OnTouchListener {

	public static final int CLICK_TIME_THRESHOLD = 500;
	public static final int CLICK_DISTANCE_THRESHOLD = 30;

	public static final float DRAG_DISTANCE_THRESHOLD = 0.8f;

	public static final int DRAG_JUMP_CAP = 60;
	public static final int PINCH_JUMP_CAP = 9999;

	public static final int SNAP_FACTOR = 6;
	public static final int SNAP_DELAY = 20;
	public static final float SNAP_ZOOM_THRESHOLD = 0.001f;
	public static final int SNAP_ALIGN_THRESHOLD = 1;

	private int _pointerId1 = -1;
	private int _pointerId2 = -1;

	private Rect _contentRect;
	private float _contentAspectRatio;
	private float _aspectRatio;

	private PointF _camera;
	private RectF _cameraEdge;
	private float _zoom = 1f;
	private float _refZoom = 1f;

	// TODO: Calculate these values properly instead of hard-coding them
	private float _minZoom = 0.5f;
	private float _maxZoom = 1.5f;

	private PointF _ref;
	// private PointF _ref
	private float _refDist;
	private long _refTime;

	private boolean _isPinching = false;
	private boolean _isClicking = false;

	private Timer _snapTimer;
	private TimerTask _snapTask;

	public PinchZoomView(Context context) {
		super(context);
		init();
	}

	public PinchZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PinchZoomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setOnTouchListener(this);

		_snapTimer = new Timer();
	}

	private void init2() {
		_camera = new PointF(0f, 0f);
		_cameraEdge = new RectF(0f, 0f, 0f, 0f);

		_contentRect = onMeasureContent();
		_contentAspectRatio = (float) _contentRect.width() / _contentRect.height();

		if (getWidth() > 0) {
			_aspectRatio = (float) getWidth() / getHeight();

			if (_aspectRatio < _contentAspectRatio) {
				_minZoom = (float) getWidth() / _contentRect.width();
			} else {
				_minZoom = (float) getHeight() / _contentRect.height();
			}

			_maxZoom = 3f; // TODO: Calculate properly
			setZoom(_minZoom);

		} else {
			setZoom(_minZoom = _maxZoom = 1f);
		}

		// setZoom updates _cameraEdge, so we'll need to re-position
		// the camera position accordingly
		_camera.set(_cameraEdge.left, _cameraEdge.top);

		_ref = new PointF(0f, 0f);
		_refDist = 0f;
		_refTime = 0;
		_refZoom = _zoom;
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		throw new UnsupportedOperationException(
				"PinchZoomView does not support external touch listeners.");
	}

	protected void onDrawContent(Canvas canvas, PointF contentOffset, float zoom) {
		// SHOULD BE IMPLEMENTED BY CHILD CLASS
	}

	protected void onContentClick(float x, float y) {
		// SHOULD BE IMPLEMENTED BY CHILD CLASS
	}

	protected Rect onMeasureContent() {
		// SHOULD BE IMPLEMENTED BY CHILD CLASS
		return new Rect(0, 0, 0, 0);
	}

	@Override
	public void invalidate() {
		init2();
		myInvalidate();
	}

	private void myInvalidate() {
		super.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (_camera == null)
			init2();

		PointF point = new PointF(-_camera.x, -_camera.y);
		onDrawContent(canvas, point, _zoom);
	}

	public final boolean onTouch(View v, MotionEvent e) {
		endSnap();

		int actionId = e.getAction() & MotionEvent.ACTION_MASK;
		int pointerId = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		switch (actionId) {
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			onDown(e, actionId, pointerId);
			break;

		case MotionEvent.ACTION_MOVE:
			onMove(e, actionId, pointerId);
			break;

		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			onUp(e, actionId, pointerId);
			break;
		}

		myInvalidate();
		return true;
	}

	private void onDown(MotionEvent e, int actionId, int pointerId) {
		if (_pointerId1 == -1) {
			Log.d("PZ", "First finger down.");
			_pointerId1 = pointerId;

			_isClicking = true;
			_isPinching = false;

			_ref.set(e.getX(), e.getY());
			_refDist = 0;

		} else if (_pointerId2 == -1 && _pointerId1 != pointerId) {
			Log.d("PZ", "Second finger down.");
			_pointerId2 = pointerId;
			_isClicking = false;
			_isPinching = true;

			setToPinchCenter(_ref, e);
			_ref.offset(-_camera.x, -_camera.y);
			_refZoom = _zoom;
			_refDist = getPointerDistance(e);
		}

		_refTime = e.getEventTime();
	}

	private void onMove(MotionEvent e, int actionId, int pointerId) {
		if (!_isPinching) { // is drag-moving or clicking
			Log.d("PZ", "MOVE");

			// doesn't need camera coordinates since we just need the
			// difference from last finger position
			float x = e.getX(), y = e.getY();
			float dx = x - _ref.x, dy = y - _ref.y;

			float dist = PointF.length(dx, dy);

			if (dist > CLICK_DISTANCE_THRESHOLD
					|| (e.getEventTime() - _refTime) > CLICK_TIME_THRESHOLD)
				_isClicking = false;

			if (Math.abs(dist - _refDist) < DRAG_JUMP_CAP)
				_camera.offset(dx, dy);

			Log.d("PZ", "Camera: (" + Float.toString(_camera.x) + ", "
					+ Float.toString(_camera.y) + ")");

			_ref.set(x, y);
			_refDist = dist;

		} else { // _isPinching == true

			float dist = getPointerDistance(e);
			float scale = dist / _refDist;

			PointF temp = new PointF();
			setToPinchCenter(temp, e);
			pinchScale(temp, scale);
		}
	}

	protected void pinchScale(PointF pinch, float scale) {
		float cameraX = pinch.x - _ref.x * scale;
		float cameraY = pinch.y - _ref.y * scale;

		// prevents content jumping
		if (PointF.length(cameraX - _camera.x, cameraY - _camera.y) < PINCH_JUMP_CAP) {
			_camera.set(cameraX, cameraY);
			setZoom(_refZoom * scale);
		}
	}

	public float getZoom() {
		return _zoom;
	}

	private void setZoom(float newZoom) {
		_zoom = newZoom;
		Log.d("K", "ZOOM: " + Float.toString(newZoom));

		int height = getHeight(), width = getWidth();

		// OPTIMIZE: Can be done *after* zooming, not continuously
		if (_aspectRatio < _contentAspectRatio) {
			int widthCap = ((int) (width - _contentRect.width() * _zoom));
			int zoomedHeight = (int) (_contentRect.height() * _zoom);

			if (zoomedHeight > height)
				_cameraEdge.set(0f, 0f, widthCap, height - zoomedHeight);
			else {
				int heightCap = (height - zoomedHeight) / 2;
				_cameraEdge.set(0f, heightCap, widthCap, heightCap);
			}

		} else {
			int heightCap = ((int) (height - _contentRect.height() * _zoom));
			int zoomedWidth = (int) (_contentRect.width() * _zoom);

			if (zoomedWidth > width)
				_cameraEdge.set(0f, 0f, width - zoomedWidth, heightCap);
			else {
				int widthCap = (width - zoomedWidth) / 2;
				_cameraEdge.set(widthCap, 0f, widthCap, heightCap);
			}

		}

	}

	private void onUp(MotionEvent e, int actionId, int pointerId) {
		if (_pointerId2 != -1) {
			Log.d("PZ", "Second finger up.");

			// ensure that if there's only one Id, it's stored in _pointerId1
			if (_pointerId1 == pointerId)
				_pointerId1 = _pointerId2;

			_pointerId2 = -1;

		} else {
			Log.d("PZ", "First finger up.");

			if (_isClicking)
				onClick(e);

			_pointerId1 = -1;

			// check for snap condition
			if (_zoom < _minZoom || _zoom > _maxZoom || _camera.x > _cameraEdge.left
					|| _camera.x < _cameraEdge.right || _camera.y > _cameraEdge.top
					|| _camera.y < _cameraEdge.bottom)
				beginSnap();
		}

		_isPinching = _isClicking = false;
	}

	private void onClick(MotionEvent e) {
		Log.d("PZ", "CLICK");

		// zoom the co-ordinates
		float x = (e.getX() - _camera.x) / _zoom;
		float y = (e.getY() - _camera.y) / _zoom;

		onContentClick(x, y);
	}

	private float getPointerDistance(MotionEvent e) {
		int index1 = e.findPointerIndex(_pointerId1);
		int index2 = e.findPointerIndex(_pointerId2);

		return PointF.length(e.getX(index1) - e.getX(index2), e.getY(index1)
				- e.getY(index2));
	}

	private void setToPinchCenter(PointF point, MotionEvent e) {
		int index1 = e.findPointerIndex(_pointerId1);
		int index2 = e.findPointerIndex(_pointerId2);

		float newX = (e.getX(index1) + e.getX(index2)) / 2;
		float newY = (e.getY(index1) + e.getY(index2)) / 2;
		point.set(newX, newY);
	}

	private void beginSnap() {
		Log.d("PZ-SNAP", "BEGIN SNAP");
		endSnap();

		final int UPDATE_X = 0, UPDATE_Y = 1, UPDATE_ZOOM = 2, INVALIDATE = 3;

		final SimpleHandler<Float> handler = new SimpleHandler<Float>() {
			@Override
			protected void receive(int what, Float value) {
				Log.d("PZ-SNAP", "RECEIVE");
				switch (what) {
				case UPDATE_X:
					_camera.set(value, _camera.y);
					break;
				case UPDATE_Y:
					_camera.set(_camera.x, value);
					break;
				case UPDATE_ZOOM:
					setZoom(value);
					break;
				case INVALIDATE:
					myInvalidate();
					break;
				}
			}
		};

		_snapTask = new TimerTask() {
			@Override
			public void run() {
				Log.d("PZ-SNAP", "SEND");
				float delta = 0f, value = 0f;
				handler.reset();
				if (_snapTask == null) {
					cancel();
					return;
				}

				if (_zoom < _minZoom) {
					delta = (_minZoom - _zoom) / SNAP_FACTOR;
					value = delta < SNAP_ZOOM_THRESHOLD ? _minZoom : _zoom + delta;
					handler.send(UPDATE_ZOOM, value);

				} else if (_zoom > _maxZoom) {
					delta = (_zoom - _maxZoom) / SNAP_FACTOR;
					value = delta < SNAP_ZOOM_THRESHOLD ? _maxZoom : _zoom - delta;
					handler.send(UPDATE_ZOOM, value);
				}

				if (_camera.x > _cameraEdge.left) {
					delta = (_camera.x - _cameraEdge.left) / SNAP_FACTOR;
					value = delta < SNAP_ALIGN_THRESHOLD ? _cameraEdge.left : _camera.x
							- delta;
					handler.send(UPDATE_X, value);

				} else if (_camera.x < _cameraEdge.right) {
					delta = (_cameraEdge.right - _camera.x) / SNAP_FACTOR;
					value = delta < SNAP_ALIGN_THRESHOLD ? _cameraEdge.right : _camera.x
							+ delta;
					handler.send(UPDATE_X, value);
				}

				if (_camera.y > _cameraEdge.top) {
					delta = (_camera.y - _cameraEdge.top) / SNAP_FACTOR;
					value = delta < SNAP_ALIGN_THRESHOLD ? _cameraEdge.top : _camera.y
							- delta;
					handler.send(UPDATE_Y, value);

				} else if (_camera.y < _cameraEdge.bottom) {
					delta = (_cameraEdge.bottom - _camera.y) / SNAP_FACTOR;
					value = delta < SNAP_ALIGN_THRESHOLD ? _cameraEdge.bottom : _camera.y
							+ delta;
					handler.send(UPDATE_Y, value);
				}

				if (handler.hasSentOnce())
					handler.send(INVALIDATE, null);
				else
					cancel();
			}
		};

		_snapTimer.scheduleAtFixedRate(_snapTask, 0, SNAP_DELAY);
	}

	private void endSnap() {
		Log.d("PZ-SNAP", "END SNAP");
		if (_snapTask != null) {
			_snapTask.cancel();
			_snapTask = null;
		}
	}
}
