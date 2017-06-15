			itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mCamera.isExpanded) {
					expandView(expandableLayout);
				} else {
					collapseView(expandableLayout);
				}
			}
		});
	
	private void expandView(final View v, int duration) {
		v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views with a height of 0.
		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		mCamera.isExpanded = true;

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? ViewGroup.LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};


		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
		expandButton.animate().rotation(180f).setDuration((long) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)).start();
	}

	private void expandView(View v) {
		expandView(v, 500);
	}

	private void collapseView(final View v, int duration) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					mCamera.isExpanded = false;
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);

		expandButton.animate().rotation(0).setDuration((long) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)).start();
	}

	public void updateOnItem(Item item) {

		if (item.isExpanded) {
			expandView(expandableLayout, 0);
		} else {
			collapseView(expandableLayout, 0);
		}
}