package com.lostmind.kreatip.baru;

import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Matrix4;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;
import rajawali.parser.ObjParser;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.lostmind.kretip.geofun.R;

public class BalokRenderer extends RajawaliRenderer {
	private BaseObject3D mRootSphere = null;
	private DirectionalLight mLight = null;
	private Animation3D mAnim = null;
	private Context mContext;

	private static final int baseDuration = 4000;
	private static final int minDuration = 1000;

	private int mAnimDuration = baseDuration;
	private Number3D mDirection = new Number3D();
	private boolean mAnimPaused = false;
	private float mCameraDistance = -5f;
	private float mRecentCameraDistance;
	private Quaternion mDragRotation = null;

	// Numbers of spheres to be downloaded per level

	public static final int[] mLimits = new int[] { 1, 9, 29, 89, 299, 989,
			2837, 6635, 12119, 16187, 18107 };

	// Public bundle for restoring data

	public Bundle mBundle = null;

	// The level we'll download and process

	public int mLevel = 3;

	// Initialization/set-up related

	public BalokRenderer(Context context) {
		super(context);
		mContext = context;
		setFrameRate(30);
	}

	protected void initScene() {
		// We'll restart rendering once we've downloaded/processed

		stopRendering();
		setBackgroundColor(0xffeeeeee);
		// Set up the camera

		mCamera.setPosition(0f, 0f, 0f);
		setCameraDistance(1f);

		// Create the default light

		createLight();

		// If we have geometry, exit now

		parseObject();

	}

	private void parseObject() {
		// TODO Auto-generated method stub
		ObjParser objParser = new ObjParser(mContext.getResources(),
				mTextureManager, R.raw.balok_obj);
		objParser.parse();
		mRootSphere = objParser.getParsedObject();
		addChild(mRootSphere);
		SimpleMaterial simple = new SimpleMaterial();
		simple.setUseColor(true);
		mRootSphere.setMaterial(simple);
		mRootSphere.setColor(0xff009900);
	}

	private void createLight() {
		// Add a light source

		if (mLight == null) {
			mLight = new DirectionalLight(0f, 0f, 1.0f);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPosition(.5f, 0, -2);
			mLight.setPower(1f);
		}
	}

	private void storeRotation() {
		mDragRotation = mRootSphere.getOrientation();
	}

	private void setCameraDistance(float scale) {
		// Set our camera distance

		mRecentCameraDistance = mCameraDistance / scale;
		mCamera.setZ(mRecentCameraDistance);
	}

	private void cancelAnimation() {
		if (mAnim != null) {
			mAnim.cancel();
			mAnim = null;
		}
	}

	private void resetAnimation(boolean sameOrOpposite) {
		// Start by canceling any existing animation

		cancelAnimation();

		// Get our axis of rotation, perpendicular to the swipe
		// direction

		Number3D axis = perpendicularAxis(-mDirection.x, mDirection.y);
		axis.normalize();

		Quaternion q = mRootSphere.getOrientation();
		Matrix4 mat = q.toRotationMatrix().inverse();
		axis = mat.transform(axis);
		axis.normalize();

		mAnim = new RotateAnimation3D(axis, 360);
		mAnim.setDuration(mAnimDuration);
		mAnim.setTransformable3D(mRootSphere);
		mAnim.setRepeatCount(Animation3D.INFINITE);
		mAnim.setRepeatMode(Animation3D.RESTART);
		mAnim.setInterpolator(new LinearInterpolator());
		mAnim.start();
	}

	// Touch gesture protocol

	public void singleTap() {
		// Pauses or restarts spinning

		if (mAnim != null) {
			if (mAnimPaused)
				mAnim.start();
			else {
				mAnim.cancel();
				storeRotation();
			}
			mAnimPaused = !mAnimPaused;
		}
	}

	public void doubleTap() {
		// Cancels spinning

		cancelAnimation();

		mAnimPaused = false;
		mAnimDuration = baseDuration;

		storeRotation();
	}

	public void drag(float x, float y) {
		// Rotates a short distance

		if (mAnim == null || mAnimPaused) {
			// Determine how far to rotate and in which direction

			float rotAng = magnitudeOfRotation(x, y) * -0.1f;
			Number3D axis = perpendicularAxis(x, y);

			// If our objects have an existing rotation, transform
			// the axis of rotation

			if (mDragRotation != null) {
				Matrix4 mat = mDragRotation.toRotationMatrix().inverse();
				axis = mat.transform(axis);
			}
			axis.normalize();

			// Get the new rotation as a quaternion

			Quaternion rot = new Quaternion();
			rot.fromAngleAxis(rotAng, axis);

			// Apply any existing rotation to it

			if (mDragRotation != null)
				rot.multiply(mDragRotation);

			mRootSphere.setOrientation(rot);
		}
	}

	public void swipe(float x, float y) {
		// Spins in a particular direction

		mAnimPaused = false;

		boolean needReset = false, sameOrOpposite = false;
		if (mAnim == null) {
			// No existing animation

			mDirection = new Number3D(x, y, 0f);
			needReset = true;
		} else {
			// Existing animation...

			if (sameDirection(x, y, mDirection.x, mDirection.y)) {
				sameOrOpposite = true;
				// ... in the same direction as the swipe, so we
				// speed up the animation by halving the duration

				if ((mAnimDuration / 2) >= minDuration) {
					mAnimDuration /= 2;
					needReset = true;
				}
			} else {
				// A new direction, reset the duration

				mDirection = new Number3D(x, y, 0f);
				mAnimDuration = baseDuration;
				needReset = true;
				sameOrOpposite = sameDirection(-x, -y, mDirection.x,
						mDirection.y);
			}
		}
		if (needReset)
			resetAnimation(sameOrOpposite);
	}

	public void pinch(float scale) {
		// Zooms the view

		setCameraDistance(scale);
	}

	public void pinchOrDragFinished() {
		mCameraDistance = mRecentCameraDistance;

		storeRotation();
	}

	// Touch-related helpers

	private boolean sameDirection(float x1, float y1, float x2, float y2) {
		return Math.abs(Math.atan2(y1, x1) - Math.atan2(y2, x2)) < 0.1;
	}

	private Number3D perpendicularAxis(float x, float y) {
		// Uses a fairly unsophisticated approach to generating
		// a perpendicular vector

		if (y == 0)
			return new Number3D(y, -x, 0);
		else
			return new Number3D(-y, x, 0);
	}

	private float magnitudeOfRotation(float x, float y) {
		return new Number3D(x, y, 0).length();
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	}
}
