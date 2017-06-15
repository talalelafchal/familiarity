// 翻转之后填充原来图片的镜像,需要获取镜像图片，要不然之前的翻转会把图片水平倒回去
private Bitmap mTargetBitMap = createMirrorBitmap(resId);
        
private Bitmap createMirrorBitmap(final int resId) {
        Bitmap srcBp = BitmapFactory.decodeResource(getResources(), resId);
        Matrix m = new Matrix();
        m.postScale(-1, 1);
        return Bitmap.createBitmap(srcBp, 0, 0, srcBp.getWidth(), srcBp.getHeight(), m, true);
}

private Runnable mStopProgressRunnable = new Runnable() {
        @Override
        public void run() {

            ObjectAnimator objAni = ObjectAnimator.ofFloat(mIconImg, "rotationY", 0, 180);
            objAni.addUpdateListener(new AnimatorUpdateListener() {
                
                boolean flag = true;

                @Override
                public void onAnimationUpdate(ValueAnimator valueAni) {
                    float y = (Float) valueAni.getAnimatedValue();
                    // Logger.v(TAG, "y = " + y);
                    if (flag && y >= 90) {
                        mIconImg.setImageBitmap(mTargetBitMap);
                        flag = false;
                    }
                }
            });
            objAni.setDuration(ANI_ROTATE_TIME).start();
        }
    };