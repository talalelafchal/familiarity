private GifFlowControl flowControl;

@Override
public void onAttach(Activity activity) {
  super.onAttach(activity);
  flowControl = (GifFlowControl)activity;
}