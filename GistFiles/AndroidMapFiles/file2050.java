public class HogeOverlay extends Overlay implements OnGestureListener {
    private GestureDetector gestureDetector = null;

    public HogeOverlay() {
        gestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e, MapView mapView) {
        // GestureDetector にタッチイベントを詳細化してもらう
        gestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e, mapView);
    }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) { return false; }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        
        /* ここでタップ時の処理をする */
        
        return false;
    }
    
    @Override
    protected boolean onTap(GeoPoint p, MapView mapView) {
        /* これは使わない */
        return false;
    }
}