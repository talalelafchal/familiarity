package de.vogella.android.listview3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListView3d extends ListView {

	private final Camera mCamera = new Camera();
	private final Matrix mMatrix = new Matrix();
	private final Paint mPaint = new Paint();

	public ListView3d(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


    @Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

		Bitmap bitmap = getChildDrawingCache(child);
		final int top = child.getTop();  // (top,left) is the pixel position of the child inside the list
		final int left = child.getLeft();
		final int childCenterY = child.getHeight() / 2;   		// center point of child
		final int childCenterX = child.getWidth() / 2;
		final int parentCenterY = getHeight() / 2;    	// center of list
		final int absChildCenterY = child.getTop() + childCenterY;    		// center point of child relative to list
		final int distanceY = parentCenterY - absChildCenterY-90; 		// distance of child center to the list center
		final int r = 1000;    	// radius of imaginary cirlce

		prepareMatrix(mMatrix, distanceY, r);

		mMatrix.preTranslate(-childCenterX, -childCenterY);
		mMatrix.postTranslate(childCenterX, childCenterY);
		mMatrix.postTranslate(left, top);

		canvas.drawBitmap(bitmap, mMatrix, mPaint);

		return false;
	}

    private void prepareMatrix(final Matrix outMatrix, int distanceY, int r) {
		final int distance = Math.min(r, Math.abs(distanceY));          	// clip the distance
		final float translateZ = (float) Math.sqrt((r * r) - (distance * distance));  	// use circle formula
		// solve for t: distance = r*cos(t)
		double radians = Math.acos((float) distance / r);
        double degree = 90 - (180 / Math.PI) * radians;

		mCamera.save();
		mCamera.translate(0, 0, r - translateZ);
		mCamera.rotateX((float) degree);

		degree = 360 - degree;

		mCamera.rotateY((float) degree);
		mCamera.getMatrix(outMatrix);
		mCamera.restore();
	}

	private Bitmap getChildDrawingCache(final View child) {
		Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			child.setDrawingCacheEnabled(true);
			child.buildDrawingCache();
			bitmap = child.getDrawingCache();
		}
		return bitmap;
	}
}
