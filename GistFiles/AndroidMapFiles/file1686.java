import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DraggableOverlay extends ItemizedOverlay<OverlayItem> {

    Resources resources;
    GestureDetector gesDetect;

    /**
     * アノテーションアイテムの画像
     */
    Bitmap marker;

    /**
     * 描画ポイント
     */
    GeoPoint point;

    /**
     * ドラッグ時の地図の中心点(ドラッグ中は地図をスクロールさせないようにする)
     */
    GeoPoint touchCenterPoint;

    /**
     * 長押ししたことを示すフラグ
     */
    boolean longPressFlag;

    private static final int dragUpPoint = 80;
    private static final int dropUpPoint = 65;



    public DraggableOverlay(Context context, Resources resources, int resourcesId, GeoPoint point) {
        super(boundCenter(resources.getDrawable(resourcesId)));

        this.point = point;
        this.gesDetect = new GestureDetector(context, onGestureListener);
        this.marker = BitmapFactory.decodeResource(context.getResources(), resourcesId);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return new OverlayItem(point, null, null);
    }

    @Override
    public int size() {
        return point == null ? 0 : 1;
    }

    @Override
    public void draw(Canvas canvas, MapView mapview, boolean flag) {
        Point screenPoint = new Point();
        mapview.getProjection().toPixels(point, screenPoint);
        canvas.drawBitmap(
                marker,
                screenPoint.x - (marker.getWidth() / 2),
                screenPoint.y - (marker.getHeight() / 2),
                null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionevent, MapView mapview) {
        gesDetect.onTouchEvent(motionevent);

        if(motionevent.getAction() == MotionEvent.ACTION_MOVE){
            if(longPressFlag == true){
                // ドラッグ中の処理
                inDrag(motionevent, mapview);
            }
        } else if (motionevent.getAction() == MotionEvent.ACTION_UP){
            if (longPressFlag) {
                // ドラッグが完了したあとの処理
                afterDrop(motionevent, mapview);
            }
            longPressFlag = false;
        }
        if (touchCenterPoint != null){
            mapview.getController().animateTo(touchCenterPoint);
            return true;
        }
        return super.onTouchEvent(motionevent, mapview);
    }

    /**
     * オーバーレイアイテムを移動させているときの処理
     */
    private void inDrag(MotionEvent motionEvent, MapView mapView) {
        // 地図のスクロールを止めるために、現在の地図の中心を記録
        touchCenterPoint = mapView.getMapCenter();
        // オーバーレイアイテムの中心点を変更
        point = mapView.getProjection().fromPixels((int) motionEvent.getX(), (int) motionEvent.getY() - dragUpPoint);
        // オーバーレイアイテムを再描画
        mapView.invalidate();
    }

    /**
     * オーバーレイアイテムを移動させてドロップしたあとの処理
     *
     * @param mapView
     */
    private void afterDrop(MotionEvent motionEvent, MapView mapView) {
        point = mapView.getProjection().fromPixels((int) motionEvent.getX(), (int) motionEvent.getY() - dropUpPoint);
        mapView.getController().animateTo(point);
        touchCenterPoint = null;
    }

    /**
     * 複雑なタッチイベントを検知 (ここでは、長押しイベントを取得)
     */
    private final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            longPressFlag = true;
            super.onLongPress(e);
        }
    };
}