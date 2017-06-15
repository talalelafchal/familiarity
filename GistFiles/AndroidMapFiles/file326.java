import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.PointerCoords;

/**
 * <code>OnTouchListener</code> that interprets multi-touch events to provide accurate and realistic pan
 * and zoom metrics for the calling activity to use. Pass it your MotionEvents to update its values.
 *
 * @author Thomas Montana
 */
class PanAndZoomController implements View.OnTouchListener {
    private static final String TAG = "PanAndZoomController";

    /**
     * The minimal zoom factor a user can get.
     */
    public static final float MIN_ZOOM_FACTOR = 0.3f;
    /**
     * The maximal zoom factor a user can get.
     */
    public static final float MAX_ZOOM_FACTOR = 2.5f;

    /**
     * The listener that will react to every pan and zoom properties update.
     */
    private PanAndZoomListener listener;

    /**
     * Current zoom factor (the one that is being live-updated during a multi-touch action)
     */
    private float curZoomFactor = 1.f;
    /**
     * Current zoom translation (being live-updated during a multi-touch action). It helps
     * both keep the zoom centered around the initial point and move the map around (pan and
     * zoom).
     */
    private PointerCoords curZoomTranslate = new PointerCoords();
    /**
     * Zoom factor accumulated from all previous pinch and zoom actions.
     */
    private float zoomFactor = 1.f;
    /**
     * Translation accumulated from all previous pan and zoom actions.
     */
    private PointerCoords zoomTranslate = new PointerCoords();

    /**
     * Number of pointers on the screen at last <code>MotionEvent</code> caught.
     */
    private int prevPointerCount = 0;
    /**
     * Coordinates of the zoom pivot on this current multi touch action.
     */
    private PointerCoords zoomPivot = new PointerCoords();
    /**
     * Coordinates of the two pointers on the screen at last <code>MotionEvent</code> caught.
     */
    private PointerCoords prevCoords1 = new PointerCoords(), prevCoords2 = new PointerCoords();


    /**
     * Public constructor.
     * @param listener The class that will receive the signal that pan and zoom properties have changed.
     *                 Usually it refreshes the zoomed views according to the new values.
     */
    public PanAndZoomController(PanAndZoomListener listener) {
        this.listener = listener;
    }


    /**
     * Set the zoom and translate values such that the rectangle defined by its boundaries
     * fits entirely in the screen.
     *
     * @param top top border of the rectangle
     * @param right right border of the rectangle
     * @param bottom bottom border of the rectangle
     * @param left left border of the rectangle
     * @param width the width of the zoomable view
     * @param height the height of the zoomable view
     */
    public void setOptimalValues(int top, int right, int bottom, int left, int width, int height) {
        float optimalZoomW = (float) width / (right - left);
        float optimalZoomH = (float) height / (bottom - top);

        zoomFactor = (optimalZoomH > optimalZoomW) ? optimalZoomW : optimalZoomH;

        /*
         * No need to overzoom the map if its contents fit the viewport.
         */
        if (zoomFactor > 1) zoomFactor = 1.0f;

        zoomTranslate.x = -zoomFactor * left;
        zoomTranslate.y = -zoomFactor * top;
        listener.onPanAndZoom();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*
         * Store the number of pointers (fingers) touching the screen.
         */
        int pointerCount = event.getPointerCount();

        if (pointerCount == 2) {
            /*
             * Get the coordinates of the two pointers.
             */
            PointerCoords p1 = new PointerCoords(), p2 = new PointerCoords();
            event.getPointerCoords(0, p1);
            event.getPointerCoords(1, p2);
            /*
             * Coordinates of the previous center of the two pointers (or the only pointer)
             */
            PointerCoords curCenter = center(p1, p2);

            /*
             * The multi-touch event has already started, we can therefore compare pointer coordinates with the
             * previous ones.
             */
            if (prevPointerCount == 2) {
                float prevDist = distance(prevCoords1, prevCoords2);
                float newDist = distance(p1, p2);

                curZoomFactor *= newDist / prevDist;
                curZoomFactor = Math.max(Math.min(curZoomFactor, MAX_ZOOM_FACTOR / zoomFactor), MIN_ZOOM_FACTOR / zoomFactor);
            } else {
                /*
                 * This is the first MotionEvent of this multi-touch action, so we set the zoom pivot but
                 * do not compare any value with previous ones.
                 */
                zoomPivot = center(p1, p2);
            }
            prevCoords1 = p1;
            prevCoords2 = p2;

            curZoomTranslate.x = zoomPivot.x - curZoomFactor * zoomPivot.x + curCenter.x - zoomPivot.x;
            curZoomTranslate.y = zoomPivot.y - curZoomFactor * zoomPivot.y + curCenter.y - zoomPivot.y;
            listener.onPanAndZoom();
        } else if (pointerCount == 1) {
            /*
             * The multi-touch event is over, store the new values inside the global zoom
             * and translation fields and reset the "current" values.
             */
            zoomTranslate.x = zoomTranslate.x * curZoomFactor + curZoomTranslate.x;
            zoomTranslate.y = zoomTranslate.y * curZoomFactor + curZoomTranslate.y;
            zoomFactor *= curZoomFactor;
            curZoomTranslate.x = 0;
            curZoomTranslate.y = 0;
            curZoomFactor = 1;
        } else {
            prevPointerCount = pointerCount;
            return false;
        }

        prevPointerCount = pointerCount;
        return true;
    }

    /**
     * Simple utility used to calculate distance between two pointers on the screen.
     * @param p1 the first pointer
     * @param p2 the second pointer
     * @return the distance between p1 and p2 as a float
     */
    private static float distance(PointerCoords p1, PointerCoords p2) {
        float x1 = p1.x;
        float y1 = p1.y;
        float x2 = p2.x;
        float y2 = p2.y;
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Simple utility used to get the coordinates of the center of two pointers on the screen.
     * @param p1 the first pointer
     * @param p2 the second pointer
     * @return a new PointerCoords object corresponding to the center of the two passed pointers
     */
    private PointerCoords center(PointerCoords p1, PointerCoords p2) {
        PointerCoords center = new PointerCoords();
        center.x = (p1.x + p2.x) / 2;
        center.y = (p1.y + p2.y) / 2;
        return center;
    }



    /**
     * Calculates the value of a dimension with the current zoom properties.
     * @param d the original dimension
     * @return the zoomed dimension
     */
    public int zoom(float d) {
        return (int) (this.zoomFactor * this.curZoomFactor * d);
    }

    /**
     * Calculates the new x coordinate by applying the zoom and pan properties.
     * @param x the x coordinate to transform
     * @return the panned and zoomed coordinate
     */
    public int zoomAndTranslateX(float x) {
        return (int) (this.curZoomFactor * (this.zoomFactor * x + this.zoomTranslate.x) + this.curZoomTranslate.x);
    }

    /**
     * Calculates the new y coordinate by applying the zoom and pan properties.
     * @param y the y coordinate to transform
     * @return the panned and zoomed coordinate
     */
    public int zoomAndTranslateY(float y) {
        return (int) (this.curZoomFactor * (this.zoomFactor * y + this.zoomTranslate.y) + this.curZoomTranslate.y);
    }

    /**
     * Inverse function of zoomAndTranslateX
     * @param zoomedAndTranslatedX the x value we want to get the original coordinate from
     * @return the original x coordinate
     */
    public int invZoomAndTranslateX(float zoomedAndTranslatedX) {
        return (int) ((((zoomedAndTranslatedX - curZoomTranslate.x) / curZoomFactor) - zoomTranslate.x) / zoomFactor);
    }

    /**
     * Inverse function of zoomAndTranslateY
     * @param zoomedAndTranslatedY the y value we want to get the original coordinate from
     * @return the original y coordinate
     */
    public int invZoomAndTranslateY(float zoomedAndTranslatedY) {
        return (int) ((((zoomedAndTranslatedY - curZoomTranslate.y) / curZoomFactor) - zoomTranslate.y) / zoomFactor);
    }


    /**
     * The interface any class that wants to use <code>PanAndZoomController</code> has to implement.
     */
    public interface PanAndZoomListener {
        /**
         * Callback method called by the <code>PanAndZoomController</code> when the pan and zoom properties have changed.
         */
        public void onPanAndZoom();
    }
}
