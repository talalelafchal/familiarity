//region MediaPlayer.OnVideoSizeChangedListener
@Override
public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
{
  setFitToFillAspectRatio(mSurfaceViewContainer, mSurfaceView, width, height);
}
//endregion

private void setFitToFillAspectRatio(View container, View inner, int videoWidth, int videoHeight) {
  Point containerSize = new Point(container.getWidth(), container.getHeight());
  Point innerSize = new Point(0, 0);
  if (videoWidth > videoHeight) {
    innerSize.x = containerSize.x;
    innerSize.y = containerSize.x * videoHeight / videoWidth;
  } else {
    innerSize.x = containerSize.y * videoWidth / videoHeight;
    innerSize.y = containerSize.y;
  }
  setViewGroupSize(inner, innerSize);
}

private void setViewGroupSize(View view, Point size) {
  ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
  if (layoutParams != null) {
    layoutParams.width = size.x;
    layoutParams.height = size.y;
  }
  view.setLayoutParams(layoutParams);
}
