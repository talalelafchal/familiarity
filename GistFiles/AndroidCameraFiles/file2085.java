ViewTreeObserver vto = mRelativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            boolean flag = false;

            @Override
            public void onGlobalLayout() {
                if (flag == false) {
                    Rotate3dAnimation anmi =
                            new Rotate3dAnimation(-180, -90, mRelativeLayout.getWidth() / 2, 0, 0, false);
                    anmi.setDuration(350);
                    anmi.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mFinishIconImageView.setImageResource(R.drawable.finish_clear_icon);
                            Rotate3dAnimation anmi =
                                    new Rotate3dAnimation(-90, -0, mRelativeLayout.getWidth() / 2, 0, 0, false);
                            anmi.setDuration(350);
                            mRelativeLayout.startAnimation(anmi);
                        }
                    });
                    mRelativeLayout.startAnimation(anmi);
                    flag = true;
                }
            }
        });