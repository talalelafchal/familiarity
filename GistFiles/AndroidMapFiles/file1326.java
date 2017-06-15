package com.example.mapsforgesample;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * Created by mor on 2017/05/29.
 */

public class MarkerWithBubble extends Marker {

    private static final String TAG = MarkerWithBubble.class.getSimpleName();

    private static final int BALLOON_VERTICAL_OFFSET = 100;
    private static final int DEFAULT_TEXT_SIZE = 15;

    private MapView mMapView;

    private Marker mBalloonMarker;

    private String mText;
    private int mTextSize;
    private int mColor;

    /**
     * @param latLong          the initial geographical coordinates of this marker (may be null).
     * @param bitmap           the initial {@code Bitmap} of this marker (may be null).
     * @param horizontalOffset the horizontal marker offset.
     * @param verticalOffset   the vertical marker offset.
     */
    public MarkerWithBubble(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset, MapView mapview, String text) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
        mMapView = mapview;
        mText = text;
        mColor = Color.BLACK;
        mTextSize = DEFAULT_TEXT_SIZE;
        mBalloonMarker = null;
    }

    public void setText(String text) {
        mText = text;
    }
    public String getText() {
        return mText;
    }

    public void setTextColor(int color) {
        mColor = color;
    }
    public int getTextColor() {
        return mColor;
    }

    public void setTextSize(int size) {
        mTextSize = size;
    }
    public int getTextSize() {
        return mTextSize;
    }

    @Override
    public boolean onTap(LatLong geoPoint, Point viewPosition, Point tapPoint) {

        Log.d(TAG, "LoaLong: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
        Log.d(TAG, "viewPos: " + viewPosition.x + ", " + viewPosition.y);
        Log.d(TAG, "tapPos : " + tapPoint.x + ", " + tapPoint.y);

        if (contains(viewPosition, tapPoint)) {
            Log.d(TAG, "contains: " + true);

            if (! TextUtils.isEmpty(mText)) {
                Log.d(TAG, "text is exist");

                if (null == mBalloonMarker) {
                    Log.d(TAG, "balloon is null");

                    Bitmap bmp = createBalloon(mMapView.getContext());
                    mBalloonMarker = new Marker(MarkerWithBubble.this.getLatLong(), bmp, 0, - bmp.getHeight() / 2 - BALLOON_VERTICAL_OFFSET );
                    mMapView.getLayerManager().getLayers().add(mBalloonMarker);
                } else {

                    if (null != mBalloonMarker) {
                        mBalloonMarker.setVisible(!mBalloonMarker.isVisible());
                    }
                }
            }
            Log.d(TAG, "text is null");
        } else {
            Log.d(TAG, "contains: " + false);

            if (null != mBalloonMarker) {
                mBalloonMarker.setVisible(false);
            }
        }
        return super.onTap(geoPoint, viewPosition, tapPoint);
    }

    private Bitmap createBalloon(Context c) {
        TextView tv = (TextView) LayoutInflater.from(mMapView.getContext()).inflate(R.layout.popup_marker, null);
        tv.setTextColor(mColor);
        tv.setTextSize(mTextSize);
        tv.setText(mText);

        return Utils.viewToBitmap(c, tv);
    }
}
