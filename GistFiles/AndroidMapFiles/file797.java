package com.nutiteq.hellomap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nutiteq.MapView;
import com.nutiteq.log.Log;

public class MyMapView extends MapView {

	private boolean touched;

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.debug("onTouchEvent "+event.getAction());
                this.setTouched(true);
		return super.onTouchEvent(event);
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
}
