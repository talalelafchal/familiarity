public void setCameraLayout(int width, int height) {
    float newProportion = (float) width / (float) height;
    // Get the width of the screen
    Point point = new Point();
    this.activity.getWindowManager().getDefaultDisplay().getSize(point);
    int screenWidth = point.x;
    int screenHeight = point.y;
    float screenProportion = (float) screenWidth / (float) screenHeight;
    // Get the SurfaceView layout parameters
    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this
            .getLayoutParams();
    float scaleFactor = 1;
    /*
     * assume width is smaller than height in screen and in input
	 * parameters. Therefore if newProportion > screenProportion then
	 * The desire proportion is more wider than higher therefore we match it against
	 * screen width and scale it height with the new proportion
	 *
	 */
    if (newProportion > screenProportion) {
        lp.width = screenWidth;
        lp.height = (int) ((float) screenWidth * (1 / newProportion));
        scaleFactor = (screenHeight / lp.height); // calculate the factor to make it full screen
    } else {
        lp.width = (int) (newProportion * (float) screenHeight);
        lp.height = screenHeight;
        scaleFactor = screenWidth / lp.width; // calculate the factor to make it full screen.

    }
    lp.width = (int) (lp.width * scaleFactor);
    lp.height = (int) (lp.height * scaleFactor);
    adjustFooterToFullScreen(screenHeight, lp);
    lp.gravity = Gravity.CENTER;
    sv.setLayoutParams(lp);
    this.footer.setBottom(sv.getBottom());
    this.footer.setLayoutParams(
            new LinearLayout.LayoutParams(
                    screenWidth,
                    this.footer.getHeight()
            )
    );

}