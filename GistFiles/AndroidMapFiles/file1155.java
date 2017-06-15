public class ExamplaryMapActivity extends RoboMapActivity {
  	@InjectView(R.id.mapview)
	private MapView mapView;

	@Override
	protected void onCreate(Bundle bundle) {
		enableHWAccel(mapView, false);
	}

	@TargetApi(11)
	private static void enableHWAccel(MapView mapView, boolean enable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			int type = enable ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_SOFTWARE;
			mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}
}