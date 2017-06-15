public class DoubleTapZoomMapView extends MapView {

    private static final long TIME_INITIAL = -1;
    private static final long TIME_DOUBLE_TAP = 250;

    private long lastTouchTime = TIME_INITIAL;
    
    public DoubleTapZoomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleTapZoomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DoubleTapZoomMapView(Context context, String apiKey) {
        super(context, apiKey);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            long thisTime = System.currentTimeMillis();
            if (thisTime - lastTouchTime <= TIME_DOUBLE_TAP) {
                this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
                lastTouchTime = TIME_INITIAL;
            } else {
                lastTouchTime = thisTime;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
    
}