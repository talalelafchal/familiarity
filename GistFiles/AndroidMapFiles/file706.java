ImageView view = new ImageView(context) {
	final RectF r = new RectF();
 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d != null) {
			r.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			getImageMatrix().mapRect(r);
			setMeasuredDimension((int) r.width(), (int) r.height());
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
};
view.setScaleType(ScaleType.CENTER_CROP);