package com.dianxinos.wifimgr.widget;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class FlipAnimation {
    private static final int DEFAULT_DURATION = 600;
    private static final Interpolator DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
    public static final int DIRECTION_X = 0;
    public static final int DIRECTION_Y = 1;

    public static void flip(ViewGroup parentView) {
        flip(DEFAULT_DURATION, DIRECTION_Y, parentView);
    }

    public static void flip(int direction, ViewGroup parentView) {
        flip(DEFAULT_DURATION, direction, parentView);
    }

    public static void flip(int duration, int direction, ViewGroup parentView) {
        int childCount = parentView.getChildCount();
        if (childCount == 0) {
            return;
        }
        View frontView = parentView.getChildAt(parentView.getChildCount() - 1);
        FlipAnimator animator = new FlipAnimator(direction, frontView);
        animator.setInterpolator(DEFAULT_INTERPOLATOR);
        animator.setDuration(duration);
        parentView.startAnimation(animator);
    }

    private static class FlipAnimator extends Animation {
        private Camera camera;

        private float centerX;

        private float centerY;

        private int direction;
        private View frontView;

        public FlipAnimator(int direction, View frontView) {
            this.direction = direction;
            this.frontView = frontView;
            setFillAfter(true);
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
            this.centerX = width / 2;
            this.centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final double radians = Math.PI * interpolatedTime;
            float degrees = (float) (180.0 * radians / Math.PI);

            if (interpolatedTime >= 0.5f) {
                degrees += 180.f;
                frontView.setVisibility(View.GONE);
            }

            final Matrix matrix = t.getMatrix();

            camera.save();
            camera.translate(0.0f, 0.0f, (float) (150.0 * Math.sin(radians)));
            if (direction == DIRECTION_X) {
                camera.rotateX(degrees);
                camera.rotateY(0);
            } else {
                camera.rotateX(0);
                camera.rotateY(degrees);
            }
            camera.rotateZ(0);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}
