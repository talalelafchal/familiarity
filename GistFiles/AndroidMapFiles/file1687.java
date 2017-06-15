/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.atomcode.naptime.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.TimeInterpolator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LayoutAnimator
{
	public static LayoutAnimator of(View view)
	{
		return new LayoutAnimator(view);
	}

	/**
	 * The View whose properties are being animated by this class. This is set at
	 * construction time.
	 */
	final View mView;

	/**
	 * The duration of the underlying Animator object. By default, we don't set the duration
	 * on the Animator and just use its default duration. If the duration is ever set on this
	 * Animator, then we use the duration that it was set to.
	 */
	private long mDuration;

	/**
	 * A flag indicating whether the duration has been set on this object. If not, we don't set
	 * the duration on the underlying Animator, but instead just use its default duration.
	 */
	private boolean mDurationSet = false;

	/**
	 * The startDelay of the underlying Animator object. By default, we don't set the startDelay
	 * on the Animator and just use its default startDelay. If the startDelay is ever set on this
	 * Animator, then we use the startDelay that it was set to.
	 */
	private long mStartDelay = 0;

	/**
	 * A flag indicating whether the startDelay has been set on this object. If not, we don't set
	 * the startDelay on the underlying Animator, but instead just use its default startDelay.
	 */
	private boolean mStartDelaySet = false;

	/**
	 * The interpolator of the underlying Animator object. By default, we don't set the interpolator
	 * on the Animator and just use its default interpolator. If the interpolator is ever set on
	 * this Animator, then we use the interpolator that it was set to.
	 */
	private TimeInterpolator mInterpolator;

	/**
	 * A flag indicating whether the interpolator has been set on this object. If not, we don't set
	 * the interpolator on the underlying Animator, but instead just use its default interpolator.
	 */
	private boolean mInterpolatorSet = false;

	/**
	 * Listener for the lifecycle events of the underlying ValueAnimator object.
	 */
	private Animator.AnimatorListener mListener = null;

	/**
	 * Listener for the update events of the underlying ValueAnimator object.
	 */
	private ValueAnimator.AnimatorUpdateListener mUpdateListener = null;

	/**
	 * A lazily-created ValueAnimator used in order to get some default animator properties
	 * (duration, start delay, interpolator, etc.).
	 */
	private ValueAnimator mTempValueAnimator;

	/**
	 * This listener is the mechanism by which the underlying Animator causes changes to the
	 * properties currently being animated, as well as the cleanup after an animation is
	 * complete.
	 */
	private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener();

	/**
	 * This list holds the properties that have been asked to animate. We allow the caller to
	 * request several animations prior to actually starting the underlying animator. This
	 * enables us to run one single animator to handle several properties in parallel. Each
	 * property is tossed onto the pending list until the animation actually starts (which is
	 * done by posting it onto mView), at which time the pending list is cleared and the properties
	 * on that list are added to the list of properties associated with that animator.
	 */
	ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList<NameValuesHolder>();
	private Runnable mPendingSetupAction;
	private Runnable mPendingCleanupAction;
	private Runnable mPendingOnStartAction;
	private Runnable mPendingOnEndAction;

	static final int NONE 			= 0x0000;
	static final int WIDTH 			= 0x0001;
	static final int HEIGHT 		= 0x0002;

	/**
	 * The mechanism by which the user can request several properties that are then animated
	 * together works by posting this Runnable to start the underlying Animator. Every time
	 * a property animation is requested, we cancel any previous postings of the Runnable
	 * and re-post it. This means that we will only ever run the Runnable (and thus start the
	 * underlying animator) after the caller is done setting the properties that should be
	 * animated together.
	 */
	private Runnable mAnimationStarter = new Runnable() {
		@Override
		public void run() {
			startAnimation();
		}
	};

	/**
	 * This class holds information about the overall animation being run on the set of
	 * properties. The mask describes which properties are being animated and the
	 * values holder is the list of all property/value objects.
	 */
	private static class PropertyBundle {
		int mPropertyMask;
		ArrayList<NameValuesHolder> mNameValuesHolder;

		PropertyBundle(int propertyMask, ArrayList<NameValuesHolder> nameValuesHolder) {
			mPropertyMask = propertyMask;
			mNameValuesHolder = nameValuesHolder;
		}

		/**
		 * Removes the given property from being animated as a part of this
		 * PropertyBundle. If the property was a part of this bundle, it returns
		 * true to indicate that it was, in fact, canceled. This is an indication
		 * to the caller that a cancellation actually occurred.
		 *
		 * @param propertyConstant The property whose cancellation is requested.
		 * @return true if the given property is a part of this bundle and if it
		 * has therefore been canceled.
		 */
		boolean cancel(int propertyConstant) {
			if ((mPropertyMask & propertyConstant) != 0 && mNameValuesHolder != null) {
				int count = mNameValuesHolder.size();
				for (int i = 0; i < count; ++i) {
					NameValuesHolder nameValuesHolder = mNameValuesHolder.get(i);
					if (nameValuesHolder.mNameConstant == propertyConstant) {
						mNameValuesHolder.remove(i);
						mPropertyMask &= ~propertyConstant;
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * This list tracks the list of properties being animated by any particular animator.
	 * In most situations, there would only ever be one animator running at a time. But it is
	 * possible to request some properties to animate together, then while those properties
	 * are animating, to request some other properties to animate together. The way that
	 * works is by having this map associate the group of properties being animated with the
	 * animator handling the animation. On every update event for an Animator, we ask the
	 * map for the associated properties and set them accordingly.
	 */
	private HashMap<Animator, PropertyBundle> mAnimatorMap =
			new HashMap<Animator, PropertyBundle>();
	private HashMap<Animator, Runnable> mAnimatorSetupMap;
	private HashMap<Animator, Runnable> mAnimatorCleanupMap;
	private HashMap<Animator, Runnable> mAnimatorOnStartMap;
	private HashMap<Animator, Runnable> mAnimatorOnEndMap;

	/**
	 * This is the information we need to set each property during the animation.
	 * mNameConstant is used to set the appropriate field in View, and the from/delta
	 * values are used to calculate the animated value for a given animation fraction
	 * during the animation.
	 */
	static class NameValuesHolder {
		int mNameConstant;
		float mFromValue;
		float mDeltaValue;
		NameValuesHolder(int nameConstant, float fromValue, float deltaValue) {
			mNameConstant = nameConstant;
			mFromValue = fromValue;
			mDeltaValue = deltaValue;
		}
	}

	/**
	 * Constructor, called by View. This is private by design, as the user should only
	 * get a ViewPropertyAnimator by calling View.animate().
	 *
	 * @param view The View associated with this ViewPropertyAnimator
	 */
	LayoutAnimator(View view) {
		mView = view;
	}

	/**
	 * Sets the duration for the underlying animator that animates the requested properties.
	 * By default, the animator uses the default value for ValueAnimator. Calling this method
	 * will cause the declared value to be used instead.
	 * @param duration The length of ensuing property animations, in milliseconds. The value
	 * cannot be negative.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator setDuration(long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Animators cannot have negative duration: " +
					duration);
		}
		mDurationSet = true;
		mDuration = duration;
		return this;
	}

	/**
	 * Returns the current duration of property animations. If the duration was set on this
	 * object, that value is returned. Otherwise, the default value of the underlying Animator
	 * is returned.
	 *
	 * @see #setDuration(long)
	 * @return The duration of animations, in milliseconds.
	 */
	public long getDuration() {
		if (mDurationSet) {
			return mDuration;
		} else {
			// Just return the default from ValueAnimator, since that's what we'd get if
			// the value has not been set otherwise
			if (mTempValueAnimator == null) {
				mTempValueAnimator = new ValueAnimator();
			}
			return mTempValueAnimator.getDuration();
		}
	}

	/**
	 * Returns the current startDelay of property animations. If the startDelay was set on this
	 * object, that value is returned. Otherwise, the default value of the underlying Animator
	 * is returned.
	 *
	 * @see #setStartDelay(long)
	 * @return The startDelay of animations, in milliseconds.
	 */
	public long getStartDelay() {
		if (mStartDelaySet) {
			return mStartDelay;
		} else {
			// Just return the default from ValueAnimator (0), since that's what we'd get if
			// the value has not been set otherwise
			return 0;
		}
	}

	/**
	 * Sets the startDelay for the underlying animator that animates the requested properties.
	 * By default, the animator uses the default value for ValueAnimator. Calling this method
	 * will cause the declared value to be used instead.
	 * @param startDelay The delay of ensuing property animations, in milliseconds. The value
	 * cannot be negative.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator setStartDelay(long startDelay) {
		if (startDelay < 0) {
			throw new IllegalArgumentException("Animators cannot have negative start " +
					"delay: " + startDelay);
		}
		mStartDelaySet = true;
		mStartDelay = startDelay;
		return this;
	}

	/**
	 * Sets the interpolator for the underlying animator that animates the requested properties.
	 * By default, the animator uses the default interpolator for ValueAnimator. Calling this method
	 * will cause the declared object to be used instead.
	 *
	 * @param interpolator The TimeInterpolator to be used for ensuing property animations. A value
	 * of <code>null</code> will result in linear interpolation.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator setInterpolator(TimeInterpolator interpolator) {
		mInterpolatorSet = true;
		mInterpolator = interpolator;
		return this;
	}

	/**
	 * Returns the timing interpolator that this animation uses.
	 *
	 * @return The timing interpolator for this animation.
	 */
	public TimeInterpolator getInterpolator() {
		if (mInterpolatorSet) {
			return mInterpolator;
		} else {
			// Just return the default from ValueAnimator, since that's what we'd get if
			// the value has not been set otherwise
			if (mTempValueAnimator == null) {
				mTempValueAnimator = new ValueAnimator();
			}
			return mTempValueAnimator.getInterpolator();
		}
	}

	/**
	 * Sets a listener for events in the underlying Animators that run the property
	 * animations.
	 *
	 * @see Animator.AnimatorListener
	 *
	 * @param listener The listener to be called with AnimatorListener events. A value of
	 * <code>null</code> removes any existing listener.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator setListener(Animator.AnimatorListener listener) {
		mListener = listener;
		return this;
	}

	Animator.AnimatorListener getListener() {
		return mListener;
	}

	/**
	 * Sets a listener for update events in the underlying ValueAnimator that runs
	 * the property animations. Note that the underlying animator is animating between
	 * 0 and 1 (these values are then turned into the actual property values internally
	 * by ViewPropertyAnimator). So the animator cannot give information on the current
	 * values of the properties being animated by this ViewPropertyAnimator, although
	 * the view object itself can be queried to get the current values.
	 *
	 * @see android.animation.ValueAnimator.AnimatorUpdateListener
	 *
	 * @param listener The listener to be called with update events. A value of
	 * <code>null</code> removes any existing listener.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator setUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
		mUpdateListener = listener;
		return this;
	}

	ValueAnimator.AnimatorUpdateListener getUpdateListener() {
		return mUpdateListener;
	}

	/**
	 * Starts the currently pending property animations immediately. Calling <code>start()</code>
	 * is optional because all animations start automatically at the next opportunity. However,
	 * if the animations are needed to start immediately and synchronously (not at the time when
	 * the next event is processed by the hierarchy, which is when the animations would begin
	 * otherwise), then this method can be used.
	 */
	public void start() {
		mView.removeCallbacks(mAnimationStarter);
		startAnimation();
	}

	/**
	 * Cancels all property animations that are currently running or pending.
	 */
	public void cancel() {
		if (mAnimatorMap.size() > 0) {
			HashMap<Animator, PropertyBundle> mAnimatorMapCopy =
					(HashMap<Animator, PropertyBundle>)mAnimatorMap.clone();
			Set<Animator> animatorSet = mAnimatorMapCopy.keySet();
			for (Animator runningAnim : animatorSet) {
				runningAnim.cancel();
			}
		}
		mPendingAnimations.clear();
		mPendingSetupAction = null;
		mPendingCleanupAction = null;
		mPendingOnStartAction = null;
		mPendingOnEndAction = null;
		mView.removeCallbacks(mAnimationStarter);
	}

	private int sizeInUnits(int value, int unit)
	{
		return (int)TypedValue.applyDimension(unit, value, mView.getContext().getResources().getDisplayMetrics());
	}

	/**
	 * This method will cause the View's LayoutParams.width property to be animated to the
	 * specified value. Animations already running on the property will be canceled.
	 *
	 * @param value The value to be animated to.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator width(int value)
	{
		animateProperty(WIDTH, value);
		return this;
	}

	public LayoutAnimator width(int value, int unit)
	{
		return width(sizeInUnits(value, unit));
	}

	public LayoutAnimator widthBy(int value)
	{
		animatePropertyBy(WIDTH, value);
		return this;
	}

	public LayoutAnimator widthBy(int value, int unit)
	{
		return widthBy(sizeInUnits(value, unit));
	}

	/**
	 * This method will cause the View's LayoutParams.height property to be animated to the
	 * specified value. Animations already running on the property will be canceled.
	 *
	 * @param value The value to be animated to.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator height(int value)
	{
		if (value < 0)
		{
			// TODO: Handle WRAP_CONTENT/MATCH_PARENT calculations
			Log.e("LayoutAnimator", "Unsupported animation!");
			return this;
		}
		animateProperty(HEIGHT, value);
		return this;
	}

	public LayoutAnimator height(int value, int unit)
	{
		return height(sizeInUnits(value, unit));
	}

	public LayoutAnimator heightBy(int value)
	{
		animatePropertyBy(HEIGHT, value);
		return this;
	}

	public LayoutAnimator heightBy(int value, int unit)
	{
		return heightBy(sizeInUnits(value, unit));
	}

	/**
	 * Specifies an action to take place when the next animation runs. If there is a
	 * {@link #setStartDelay(long) startDelay} set on this ViewPropertyAnimator, then the
	 * action will run after that startDelay expires, when the actual animation begins.
	 * This method, along with {@link #withEndAction(Runnable)}, is intended to help facilitate
	 * choreographing ViewPropertyAnimator animations with other animations or actions
	 * in the application.
	 *
	 * @param runnable The action to run when the next animation starts.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator withStartAction(Runnable runnable) {
		mPendingOnStartAction = runnable;
		if (runnable != null && mAnimatorOnStartMap == null) {
			mAnimatorOnStartMap = new HashMap<Animator, Runnable>();
		}
		return this;
	}

	/**
	 * Specifies an action to take place when the next animation ends. The action is only
	 * run if the animation ends normally; if the ViewPropertyAnimator is canceled during
	 * that animation, the runnable will not run.
	 * This method, along with {@link #withStartAction(Runnable)}, is intended to help facilitate
	 * choreographing ViewPropertyAnimator animations with other animations or actions
	 * in the application.
	 *
	 * <p>For example, the following code animates a view to x=200 and then back to 0:</p>
	 * <pre>
	 *     Runnable endAction = new Runnable() {
	 *         public void run() {
	 *             view.animate().x(0);
	 *         }
	 *     };
	 *     view.animate().x(200).withEndAction(endAction);
	 * </pre>
	 *
	 * @param runnable The action to run when the next animation ends.
	 * @return This object, allowing calls to methods in this class to be chained.
	 */
	public LayoutAnimator withEndAction(Runnable runnable) {
		mPendingOnEndAction = runnable;
		if (runnable != null && mAnimatorOnEndMap == null) {
			mAnimatorOnEndMap = new HashMap<Animator, Runnable>();
		}
		return this;
	}

	boolean hasActions() {
		return mPendingSetupAction != null
				|| mPendingCleanupAction != null
				|| mPendingOnStartAction != null
				|| mPendingOnEndAction != null;
	}

	/**
	 * Starts the underlying Animator for a set of properties. We use a single animator that
	 * simply runs from 0 to 1, and then use that fractional value to set each property
	 * value accordingly.
	 */
	private void startAnimation() {
		mView.setHasTransientState(true);
		ValueAnimator animator = ValueAnimator.ofFloat(1.0f);
		ArrayList<NameValuesHolder> nameValueList =
				(ArrayList<NameValuesHolder>) mPendingAnimations.clone();
		mPendingAnimations.clear();
		int propertyMask = 0;
		int propertyCount = nameValueList.size();
		for (int i = 0; i < propertyCount; ++i) {
			NameValuesHolder nameValuesHolder = nameValueList.get(i);
			propertyMask |= nameValuesHolder.mNameConstant;
		}
		mAnimatorMap.put(animator, new PropertyBundle(propertyMask, nameValueList));
		if (mPendingSetupAction != null) {
			mAnimatorSetupMap.put(animator, mPendingSetupAction);
			mPendingSetupAction = null;
		}
		if (mPendingCleanupAction != null) {
			mAnimatorCleanupMap.put(animator, mPendingCleanupAction);
			mPendingCleanupAction = null;
		}
		if (mPendingOnStartAction != null) {
			mAnimatorOnStartMap.put(animator, mPendingOnStartAction);
			mPendingOnStartAction = null;
		}
		if (mPendingOnEndAction != null) {
			mAnimatorOnEndMap.put(animator, mPendingOnEndAction);
			mPendingOnEndAction = null;
		}
		animator.addUpdateListener(mAnimatorEventListener);
		animator.addListener(mAnimatorEventListener);
		if (mStartDelaySet) {
			animator.setStartDelay(mStartDelay);
		}
		if (mDurationSet) {
			animator.setDuration(mDuration);
		}
		if (mInterpolatorSet) {
			animator.setInterpolator(mInterpolator);
		}
		animator.start();
	}

	/**
	 * Utility function, called by the various x(), y(), etc. methods. This stores the
	 * constant name for the property along with the from/delta values that will be used to
	 * calculate and set the property during the animation. This structure is added to the
	 * pending animations, awaiting the eventual start() of the underlying animator. A
	 * Runnable is posted to start the animation, and any pending such Runnable is canceled
	 * (which enables us to end up starting just one animator for all of the properties
	 * specified at one time).
	 *
	 * @param constantName The specifier for the property being animated
	 * @param toValue The value to which the property will animate
	 */
	private void animateProperty(int constantName, float toValue) {
		float fromValue = getValue(constantName);
		float deltaValue = toValue - fromValue;
		animatePropertyBy(constantName, fromValue, deltaValue);
	}

	/**
	 * Utility function, called by the various xBy(), yBy(), etc. methods. This method is
	 * just like animateProperty(), except the value is an offset from the property's
	 * current value, instead of an absolute "to" value.
	 *
	 * @param constantName The specifier for the property being animated
	 * @param byValue The amount by which the property will change
	 */
	private void animatePropertyBy(int constantName, float byValue) {
		float fromValue = getValue(constantName);
		animatePropertyBy(constantName, fromValue, byValue);
	}

	/**
	 * Utility function, called by animateProperty() and animatePropertyBy(), which handles the
	 * details of adding a pending animation and posting the request to start the animation.
	 *
	 * @param constantName The specifier for the property being animated
	 * @param startValue The starting value of the property
	 * @param byValue The amount by which the property will change
	 */
	private void animatePropertyBy(int constantName, float startValue, float byValue) {
		// If this animation performs no/minimal change, just don't do it.
		// Layout animations can be expensive!!
		if (byValue < 0.01f && byValue > -0.01f)
		{
			return;
		}

		// First, cancel any existing animations on this property
		if (mAnimatorMap.size() > 0) {
			Animator animatorToCancel = null;
			Set<Animator> animatorSet = mAnimatorMap.keySet();
			for (Animator runningAnim : animatorSet) {
				PropertyBundle bundle = mAnimatorMap.get(runningAnim);
				if (bundle.cancel(constantName)) {
					// property was canceled - cancel the animation if it's now empty
					// Note that it's safe to break out here because every new animation
					// on a property will cancel a previous animation on that property, so
					// there can only ever be one such animation running.
					if (bundle.mPropertyMask == NONE) {
						// the animation is no longer changing anything - cancel it
						animatorToCancel = runningAnim;
						break;
					}
				}
			}
			if (animatorToCancel != null) {
				animatorToCancel.cancel();
			}
		}

		NameValuesHolder nameValuePair = new NameValuesHolder(constantName, startValue, byValue);
		mPendingAnimations.add(nameValuePair);
		mView.removeCallbacks(mAnimationStarter);
		mView.postOnAnimation(mAnimationStarter);
	}

	/**
	 * This method handles setting the property values directly in the View object's fields.
	 * propertyConstant tells it which property should be set, value is the value to set
	 * the property to.
	 *
	 * @param propertyConstant The property to be set
	 * @param value The value to set the property to
	 */
	private void setValue(int propertyConstant, float value) {
		final ViewGroup.LayoutParams params = mView.getLayoutParams();
		switch (propertyConstant)
		{
			case WIDTH:
				params.width = (int)value;
				break;
			case HEIGHT:
				params.height = (int)value;
				break;
		}
		mView.setLayoutParams(params);
	}

	/**
	 * This method gets the value of the named property from the View object.
	 *
	 * @param propertyConstant The property whose value should be returned
	 * @return float The value of the named property
	 */
	private float getValue(int propertyConstant) {
		switch (propertyConstant) {
			case WIDTH:
				return mView.getMeasuredWidth();
			case HEIGHT:
				return mView.getMeasuredHeight();
		}
		return 0;
	}

	/**
	 * Utility class that handles the various Animator events. The only ones we care
	 * about are the end event (which we use to clean up the animator map when an animator
	 * finishes) and the update event (which we use to calculate the current value of each
	 * property and then set it on the view object).
	 */
	private class AnimatorEventListener
			implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
		@Override
		public void onAnimationStart(Animator animation) {
			if (mAnimatorSetupMap != null) {
				Runnable r = mAnimatorSetupMap.get(animation);
				if (r != null) {
					r.run();
				}
				mAnimatorSetupMap.remove(animation);
			}
			if (mAnimatorOnStartMap != null) {
				Runnable r = mAnimatorOnStartMap.get(animation);
				if (r != null) {
					r.run();
				}
				mAnimatorOnStartMap.remove(animation);
			}
			if (mListener != null) {
				mListener.onAnimationStart(animation);
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			if (mListener != null) {
				mListener.onAnimationCancel(animation);
			}
			if (mAnimatorOnEndMap != null) {
				mAnimatorOnEndMap.remove(animation);
			}
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			if (mListener != null) {
				mListener.onAnimationRepeat(animation);
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			mView.setHasTransientState(false);
			if (mListener != null) {
				mListener.onAnimationEnd(animation);
			}
			if (mAnimatorOnEndMap != null) {
				Runnable r = mAnimatorOnEndMap.get(animation);
				if (r != null) {
					r.run();
				}
				mAnimatorOnEndMap.remove(animation);
			}
			if (mAnimatorCleanupMap != null) {
				Runnable r = mAnimatorCleanupMap.get(animation);
				if (r != null) {
					r.run();
				}
				mAnimatorCleanupMap.remove(animation);
			}
			mAnimatorMap.remove(animation);
		}

		/**
		 * Calculate the current value for each property and set it on the view. Invalidate
		 * the view object appropriately, depending on which properties are being animated.
		 *
		 * @param animation The animator associated with the properties that need to be
		 * set. This animator holds the animation fraction which we will use to calculate
		 * the current value of each property.
		 */
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			PropertyBundle propertyBundle = mAnimatorMap.get(animation);
			if (propertyBundle == null) {
				// Shouldn't happen, but just to play it safe
				return;
			}

			float fraction = animation.getAnimatedFraction();
			ArrayList<NameValuesHolder> valueList = propertyBundle.mNameValuesHolder;
			if (valueList != null) {
				int count = valueList.size();
				for (int i = 0; i < count; ++i) {
					NameValuesHolder values = valueList.get(i);
					float value = values.mFromValue + fraction * values.mDeltaValue;
					setValue(values.mNameConstant, value);
				}
			}

			if (mUpdateListener != null) {
				mUpdateListener.onAnimationUpdate(animation);
			}
		}
	}
}
