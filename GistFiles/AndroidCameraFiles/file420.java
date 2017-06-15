package com.insiteo.lbs.map.render;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.insiteo.lbs.R;
import com.insiteo.lbs.common.CommonConstants;
import com.insiteo.lbs.common.utils.ISUtils;
import com.insiteo.lbs.common.utils.geometry.ISPointD;
import com.insiteo.lbs.common.utils.geometry.ISPosition;
import com.insiteo.lbs.map.ISMapView;
import com.insiteo.lbs.map.entities.ISMap;
import com.insiteo.lbs.map.entities.ISZonePoi;
import com.insiteo.lbs.map.utils.ISCoordConverter;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.NPOTTexture;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.Overlay;

import java.util.concurrent.Semaphore;

/**
 * Concrete implementation of a basic {@link ISIRTO}  Object.
 *
 * @author Insiteo
 */
public class ISGenericRTO implements ISIRTO {

    //******************************************************************************************************************
    // 	DRAWING 3D
    // *****************************************************************************************************************
    private final static int MARKER_DEPTH_OFFSET = 10;
    private final static int ANNOTATION_DEPTH_OFFSET = 8;
    private final static int ACTION_DEPTH_OFFSET = 8;
    private final static int INDICATOR_DEPTH_OFFSET = 8;
    private final static String ACTION_TEXTURE = "GENERIC_RTO_ACTION_TEXTURE";
    private final static String ACTION_TOUCHED_TEXTURE = "GENERIC_RTO_ACTION_TOUCHED_TEXTURE ";
    private final static String INDICATOR_TEXTURE = "GENERIC_RTO_INDICATOR_TEXTURE";
    private final static String INDICATOR_TOUCHED_TEXTURE = "GENERIC_RTO_INDICATOR_TOUCHED_TEXTURE";
    private static int ID = 0;
    /* List of static fields used because the corresponding textures are created only once */
    private static int mMarkerBitmapWidth;
    private static int mMarkerBitmapHeight;
    private static int mIndicatorWidth;
    private static int mIndicatorHeight;
    private static int mActionWidth;
    private static int mActionHeight;
    protected int mPinResId;
    protected boolean mIsLabelDisplayed = true;
    protected boolean mIsWindowDisplayed = false;
    protected boolean mIsActionDisplayed = true;
    protected boolean mIsIndicatorDisplayed = false;
    protected ISERenderMode mRenderingMode;
    protected static Bitmap sMarkerBitmap, sActionBitmap, sIndicatorBitmap;
    protected float mWindowRadius, mWindowAttacherSize;
    protected RectF m2DIconRect;
    protected RectF mWindowRect;

    //******************************************************************************************************************
    // 	Constructors
    // *****************************************************************************************************************
    protected RectF mActionMainRect, mActionBorderRect;
    protected RectF mActionIconRect;
    protected RectF mIndicatorMainRect, mIndicatorIconRect;


    //******************************************************************************************************************
    // Public method
    // *****************************************************************************************************************
    protected Paint mWindowPaint, mLabelPaint;
    protected Point mWindowMargin, mActionMargin, mNameMargin, mIndicatorMargin;
    protected boolean mIsTouchable = true;
    protected boolean mIsDraggable = false;
    protected boolean mIsDragged = false;
    protected volatile boolean mIsMarkerTouched = false;
    protected boolean mIsMarkerClicked = false;
    protected volatile boolean mIsAnnotationTouched = false;
    protected boolean mIsAnnotationClicked = false;
    protected volatile boolean mIsActionTouched = false;
    protected boolean mIsActionClicked = false;
    private String mLabel;
    private String mAnnotationLabel = "";
    private int mId;
    private ISZonePoi mZonePoi;
    private ISPosition mPosition;

    //******************************************************************************************************************
    //
    // *****************************************************************************************************************
    private SimpleVector mZoneOffset;
    private int mAnnotationBkgNormalColor, mAnnotationBkgPressedColor;
    private int mActionColorNormal, mActionColorPressed;
    private int mAnnotationTextColor;
    private int mLabelTextColor, mLabelStrokeColor;
    private float mLastAngle;
    private double mLastRatio;
    private Point mLastPos = new Point();
    private Semaphore mLock = new Semaphore(1);
    private ISMap mMap;
    private ISWorld mWorld;

   /* *//**
     * Returns <code>true</code> if the marker was touched
     * @return
     *//*
    public boolean mIsTouched() {
		return mIsMarkerTouched;
	}*/
    private FrameBuffer mFrameBuffer;
    private String MARKER_TEXTURE;
    private String LABEL_TEXTURE;
    private String ANNOTATION_TEXTURE;

    //******************************************************************************************************************
    // 	Resources Handling
    // *****************************************************************************************************************
    private String ANNOTATION_TOUCHED_TEXTURE;
    // Represent the global 3D object, it has no visual representation but is used to agglomerate several 3D objects
    private ISObject3D m3DObject;
    private Overlay mMarkerOverlay;
    private Overlay mLabelOverlay;
    private Overlay mAnnotationOverlay;
    private Overlay mActionOverlay;
    private Overlay mIndicatorOverlay;
    private Rect mLabelDisplayRect;

    //******************************************************************************************************************
    // 	DRAWING 2D
    // *****************************************************************************************************************
    private Rect mMarkerDisplayRect;
    private Rect mAnnotationDisplayRect;
    private Rect mIndicatorDisplayRect;
    private Rect mActionDisplayRect;
    //******************************************************************************************************************
    // 	Touch Management
    // *****************************************************************************************************************
    private Point mTouchPoint = new Point();
    /**
     * Equivalent to {@link ISGenericRTO#ISGenericRTO(com.insiteo.lbs.common.utils.geometry.ISPosition, String, boolean, boolean)} with no {@link com.insiteo.lbs.common.utils.geometry.ISPosition}, no name and
     * the window and the hidden and the label displayed.
     */
    public ISGenericRTO() {
        this(null, null, false, false);
    }

    /**
     * Equivalent to {@link ISGenericRTO#ISGenericRTO(com.insiteo.lbs.common.utils.geometry.ISPosition, String, boolean, boolean)} with the window and the hidden and the label displayed.
     *
     * @param pos  the {@link com.insiteo.lbs.common.utils.geometry.ISPosition} where the {@link ISIRTO} should be drawn (this is not mandatory when the {@link ISIRTO} is drawn in a {@link com.insiteo.lbs.map.entities.ISZone}).
     * @param name the name of the {@link ISIRTO}.
     */
    public ISGenericRTO(ISPosition pos, String name) {
        this(pos, name, false, true);
    }

    /**
     * Default constructor for {@link ISGenericRTO}
     *
     * @param pos             the {@link com.insiteo.lbs.common.utils.geometry.ISPosition} where the {@link ISIRTO} should be drawn (this is not mandatory when the {@link ISIRTO} is drawn in a {@link com.insiteo.lbs.map.entities.ISZone}).
     * @param name            the name of the {@link ISIRTO}.
     * @param windowDisplayed <code>true</code> if the window should be displayed initially.
     * @param labelDisplayed  <code>true</code> if the label should be displayed initially.
     */
    public ISGenericRTO(ISPosition pos, String name, boolean windowDisplayed, boolean labelDisplayed) {
        mId = ID;
        ID++;
        mPosition = pos;
        mLabel = name;
        mIsWindowDisplayed = windowDisplayed;
        mIsLabelDisplayed = labelDisplayed;
    }

    /**
     * Returns the drawing {@link com.insiteo.lbs.common.utils.geometry.ISPosition}.
     *
     * @return the drawing {@link com.insiteo.lbs.common.utils.geometry.ISPosition}.
     */
    public ISPosition getPosition() {
        return mPosition;
    }

    /**
     * Sets the {@link com.insiteo.lbs.common.utils.geometry.ISPosition} where the {@link ISGenericRTO} should be drawn.
     *
     * @param position the drawing {@link com.insiteo.lbs.common.utils.geometry.ISPosition}.
     */
    public void setPosition(ISPosition position) {
        mPosition = position;
    }

    /**
     * Returns the {@link ISIRTO} label.
     *
     * @return the {@link ISIRTO} label.
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Sets the {@link ISIRTO} label.
     *
     * @param label for the {@link ISIRTO}.
     */
    public void setLabel(String label) {
        mLabel = label;
    }

    /**
     * Returns the {@link ISIRTO} annotation.
     *
     * @return the {@link ISIRTO} annotation.
     */
    public String getAnnotationLabel() {
        return mAnnotationLabel;
    }

    /**
     * Sets the {@link ISIRTO} annotation.
     *
     * @param annotation for the {@link ISIRTO}.
     */
    public void setAnnotationLabel(String annotation) {
        mAnnotationLabel = annotation;
    }

    /**
     * Returns whether the name should be displayed.
     *
     * @return <code>true</code> if the name should be displayed, <code>false</code> otherwise.
     */
    public boolean isLabelDisplayed() {
        return mIsLabelDisplayed;
    }

    /**
     * Sets whether the name should be displayed.
     *
     * @param displayed true if the name should be displayed.
     */
    public void setLabelDisplayed(boolean displayed) {
        mIsLabelDisplayed = displayed;
    }

    /**
     * Returns whether the window is displayed.
     *
     * @return <code>true</code> if it is displayed, <code>false</code> otherwise.
     */
    public boolean isWindowDisplayed() {
        return mIsWindowDisplayed;
    }

    /**
     * Sets whether the window should be displayed.
     *
     * @param displayed <code>true</code> if it should be displayed, <code>false</code> otherwise.
     */
    public void setWindowDisplayed(boolean displayed) {
        mIsWindowDisplayed = displayed;
    }

    /**
     * Returns whether the action is displayed.
     *
     * @return <code>true</code> if it is displayed, <code>false</code> otherwise.
     */
    public boolean isActionDisplayed() {
        return mIsActionDisplayed;
    }

    /**
     * Sets whether the action should be displayed.
     *
     * @param displayed <code>true</code> if it should be displayed, <code>false</code> otherwise.
     */
    public void setActionDisplayed(boolean displayed) {
        mIsActionDisplayed = displayed;
    }

    /**
     * Returns whether the indicator is displayed.
     *
     * @return <code>true</code> if it is displayed, <code>false</code> otherwise.
     */
    public boolean isIndicatorDisplayed() {
        return mIsIndicatorDisplayed;
    }

    /**
     * Sets whether the indicator should be displayed.
     *
     * @param displayed <code>true</code> if it should be displayed, <code>false</code> otherwise.
     */
    public void setIndicatorDisplayed(boolean displayed) {
        mIsIndicatorDisplayed = displayed;
    }

    /**
     * Returns <code>true</code> if the last touch event was a click on the action button.
     *
     * @return <code>true</code> if the action button was just clicked.
     */
    public boolean isActionClicked() {
        boolean res = mIsActionClicked;
        mIsActionClicked = false;
        return res;
    }

    /**
     * Returns <code>true</code> if the last touch event was a click on the annotation.
     *
     * @return <code>true</code> if the annotation was just clicked.
     */
    public boolean isAnnotationClicked() {
        boolean res = mIsAnnotationClicked;
        mIsAnnotationClicked = false;
        return res;
    }

    @Override
    public int getRtoID() {
        return mId;
    }

    /**
     * Sets the if that should be used for this {@link ISIRTO}.
     *
     * @param id to use.
     */
    public void setRtoID(int id) {
        mId = id;
    }

    @Override
    public ISZonePoi getZonePoi() {
        return mZonePoi;
    }

    @Override
    public void setZonePoi(ISZonePoi zonePoi) {
        mZonePoi = zonePoi;
    }

    /**
     * Returns the drawing {@link com.insiteo.lbs.common.utils.geometry.ISPosition#getMapID()} if it is set or {@link CommonConstants#NULL_ID}.
     */
    @Override
    public int getMapID() {
        return (mPosition != null) ? mPosition.getMapID() : CommonConstants.NULL_ID;
    }

    @Override
    public SimpleVector getZoneOffset() {
        return mZoneOffset;
    }

    @Override
    public void setZoneOffset(SimpleVector zoneOffset) {
        mZoneOffset = zoneOffset;
    }

    @Override
    public ISERenderMode getRenderMode() {
        return mRenderingMode;
    }

    @Override
    public void setRenderMode(ISERenderMode renderingMode) {
        mRenderingMode = renderingMode;
    }

    /**
     * Returns <code>true</code> if this {@link ISGenericRTO} is touchable (ie will react to touch event).
     *
     * @return <code>true</code> if this instance is touchable.
     */
    public boolean isTouchable() {
        return mIsTouchable;
    }

    /**
     * Sets whether or not this {@link ISGenericRTO} is touchable. If set to <code>false</code> none of the IRTO callback will be triggered for this IRTO.
     *
     * @param isTouchable <code>true</code> if the {@link ISGenericRTO} is touchable.
     */
    public void setTouchable(boolean isTouchable) {
        mIsTouchable = isTouchable;
    }

    /**
     * Returns True if this GenericRTO is touchable (ie will react to touch event).
     *
     * @return True if this instance is touchable.
     */
    public boolean isDraggable() {
        return mIsDraggable;
    }

    /**
     * Sets whether or not this GenericRTO is draggable. If set to false none of the IRTO callback will be triggered for this IRTO.
     *
     * @param isDraggable <code>true</code> if this {@link ISGenericRTO} is draggable, <code>false</code> otherwise.
     */
    public void setDraggable(boolean isDraggable) {
        mIsDraggable = isDraggable;
    }

    /**
     * Returns if this {@link ISIRTO} is currently being dragged.
     *
     * @return true if the {@link ISIRTO} is being dragged.
     */
    public boolean isDragged() {
        return mIsDragged;
    }

    /**
     * Method that can be used to avoid toggling the window on {@link ISIRTO} click.
     *
     * @return false to avoid the toggle.
     */
    protected boolean shouldToggleWindowOnMarkerClick() {
        return true;
    }

    @Override
    public ISIRTO cloneRTO() {
        ISGenericRTO p = null;
        try {
            p = (ISGenericRTO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * This methods gets called when the {@link ISIRTO} is added to its {@link ISIRenderer}.
     * It calls {@link ISGenericRTO#set2DResources(Resources)} or {@link ISGenericRTO#set3DResources(Resources)}
     * depending the current {@link ISERenderMode}.
     */
    @Override
    public void setResources(Resources resources) {
        if (resources != null && mRenderingMode != null) {
            if (mRenderingMode == ISERenderMode.MODE_2D) {
                set2DResources(resources);
            } else {
                set3DResources(resources);
            }
        }
    }

    /**
     * Sets the resources that should be used when drawing this {@link ISIRTO} in a 2D context.
     *
     * @param resources {@link Resources} to access {@link Bitmap}, {@link Color} and all other data from the project resource folder.
     */
    protected void set2DResources(Resources resources) {
        if (sMarkerBitmap == null)
            sMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_marker);
        if (sActionBitmap == null)
            sActionBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_action);
        if (sIndicatorBitmap == null)
            sIndicatorBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_indicator);

        m2DIconRect = new RectF();
        mWindowRect = new RectF();

        mActionMainRect = new RectF();
        mActionBorderRect = new RectF();
        mActionIconRect = new RectF();

        mIndicatorMainRect = new RectF();
        mIndicatorIconRect = new RectF();

        mWindowPaint = new Paint();

        mWindowPaint.setTextSize(resources.getDimension(R.dimen.generic_rto_window_text_size));
        mWindowPaint.setStrokeWidth(resources.getDimension(R.dimen.generic_rto_window_stroke));
        mWindowPaint.setAntiAlias(true);

        mLabelPaint = new Paint();
        mLabelPaint.setTextSize(resources.getDimension(R.dimen.generic_rto_label_text_size));
        mLabelPaint.setStrokeWidth(resources.getDimension(R.dimen.generic_rto_label_stroke));
        mLabelPaint.setAntiAlias(true);

        mWindowRadius = resources.getDimension(R.dimen.generic_rto_radius);
        mWindowAttacherSize = resources.getDimension(R.dimen.generic_rto_window_attacher_size);

        mAnnotationBkgNormalColor = resources.getColor(R.color.generic_rto_annotation_bkg_normal);
        mAnnotationBkgPressedColor = resources.getColor(R.color.generic_rto_annotation_bkg_pressed);
        mAnnotationTextColor = resources.getColor(R.color.generic_rto_annotation_text_color);

        mActionColorNormal = resources.getColor(R.color.generic_rto_action_normal);
        mActionColorPressed = resources.getColor(R.color.generic_rto_action_pressed);
        mLabelStrokeColor = resources.getColor(R.color.generic_rto_label_stroke_color);
        mLabelTextColor = resources.getColor(R.color.generic_rto_3d_name_color);

        setWindowMargin(new Point(0, 0));
        setActionMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_action_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_action_anchor_y)));
        setNameMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_name_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_name_anchor_y)));
        setIndicatorMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_indicator_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_indicator_anchor_y)));
    }

    /**
     * Sets the resources that should be used when drawing this {@link ISIRTO} in a 3D context.
     *
     * @param resources {@link Resources} to access {@link Bitmap}, {@link Color} and all other data from the project resource folder.
     */
    protected void set3DResources(Resources resources) {
        m3DObject = new ISObject3D();

        mPinResId = R.drawable.ic_generic_rto_marker;

        MARKER_TEXTURE = mPinResId + "GENERIC_RTO_MARKER_TEXTURE";
        LABEL_TEXTURE = hashCode() + "_LABEL_TEXTURE";
        ANNOTATION_TEXTURE = hashCode() + "_ANNOTATION_TEXTURE";
        ANNOTATION_TOUCHED_TEXTURE = hashCode() + "_ANNOTATION_TOUCHED_TEXTURE";

        if (sMarkerBitmap == null)
            sMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_marker);
        if (sActionBitmap == null)
            sActionBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_action);
        if (sIndicatorBitmap == null)
            sIndicatorBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_generic_rto_indicator);

        mWindowPaint = new Paint();

        mWindowPaint.setTextSize(resources.getDimension(R.dimen.generic_rto_window_text_size));
        mWindowPaint.setStrokeWidth(resources.getDimension(R.dimen.generic_rto_window_stroke));
        mWindowPaint.setAntiAlias(true);

        mLabelPaint = new Paint();
        mLabelPaint.setTextSize(resources.getDimension(R.dimen.generic_rto_label_text_size));
        mLabelPaint.setStrokeWidth(resources.getDimension(R.dimen.generic_rto_label_stroke));
        mLabelPaint.setAntiAlias(true);

        mWindowRadius = resources.getDimension(R.dimen.generic_rto_radius);
        mWindowAttacherSize = resources.getDimension(R.dimen.generic_rto_window_attacher_size);

        mAnnotationBkgNormalColor = resources.getColor(R.color.generic_rto_annotation_bkg_normal);
        mAnnotationBkgPressedColor = resources.getColor(R.color.generic_rto_annotation_bkg_pressed);
        mAnnotationTextColor = resources.getColor(R.color.generic_rto_annotation_text_color);

        mActionColorNormal = resources.getColor(R.color.generic_rto_action_normal);
        mActionColorPressed = resources.getColor(R.color.generic_rto_action_pressed);
        mLabelStrokeColor = resources.getColor(R.color.generic_rto_label_stroke_color);
        mLabelTextColor = resources.getColor(R.color.generic_rto_3d_name_color);

        setWindowMargin(new Point(0, 0));
        setActionMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_action_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_action_anchor_y)));
        setNameMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_name_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_name_anchor_y)));
        setIndicatorMargin(new Point((int) resources.getDimension(R.dimen.generic_rto_indicator_anchor_x), (int) resources.getDimension(R.dimen.generic_rto_indicator_anchor_y)));

        createNPOTMarkerTexture();

    }

    /**
     * This method gets called when the IRTO gets removed from its {@link ISIRenderer}.
     * It calls {@link ISGenericRTO#free2DResources()} or {@link ISGenericRTO#free3DResources()}
     * depending the current {@link ISERenderMode}.
     */
    @Override
    public void freeResources() {
        if (mRenderingMode == ISERenderMode.MODE_2D) {
            free2DResources();
        } else if (mRenderingMode == ISERenderMode.MODE_3D) {
            free3DResources();
        }
    }

    /**
     * Gets called when the resources used for 2D rendering should be disposed.
     */
    protected void free2DResources() {
        m2DIconRect = null;
        mWindowRect = null;
        mWindowPaint = null;
    }

    /**
     * Gets called when the resources used for 3D rendering should be disposed.
     */
    protected void free3DResources() {
        mMarkerOverlay = null;
        mAnnotationOverlay = null;
        mActionOverlay = null;
        mIndicatorOverlay = null;
        mLabelOverlay = null;
        m3DObject = null;
    }

    @Override
    public void setDisplayEnabled(boolean enabled) {
        if (mRenderingMode == ISERenderMode.MODE_3D) {
            if (mMarkerOverlay != null) mMarkerOverlay.setVisibility(enabled);
            if (mAnnotationOverlay != null) mAnnotationOverlay.setVisibility(enabled);
            if (mActionOverlay != null) mActionOverlay.setVisibility(enabled);
            if (mIndicatorOverlay != null) mIndicatorOverlay.setVisibility(enabled);
            if (mLabelOverlay != null) mLabelOverlay.setVisibility(enabled);
        }
    }

    /**
     * Draws this {@link ISGenericRTO} on the {@link ISMapView} {@link Canvas}.
     */
    @Override
    public void render2D(Canvas aCanvas, double aRatio, Point aOffset, float aAngle) {
        try {
            mLock.acquire();
            mLastRatio = aRatio;

            int newOffsetX;
            int newOffsetY;

            if (mPosition != null) {
                newOffsetX = (int) (aOffset.x + mPosition.getX() * aRatio);
                newOffsetY = (int) (aOffset.y + mPosition.getY() * aRatio);
            } else {
                newOffsetX = aOffset.x;
                newOffsetY = aOffset.y;
            }

            //rotate canvas back if needed
            if (aAngle != 0f) {
                aCanvas.save();
                aCanvas.rotate(-aAngle, newOffsetX, newOffsetY);
            }

            Point drawingPosition = get2DDrawingPosition(aRatio, aOffset);
            drawMarker(aCanvas, drawingPosition);
            if (mIsLabelDisplayed)
                drawLabel(aCanvas, drawingPosition, sMarkerBitmap.getHeight() / 2);
            if (mIsWindowDisplayed) drawWindow(aCanvas, drawingPosition);

            //restore canvas if needed
            if (aAngle != 0f) {
                aCanvas.restore();
            }

            mLastAngle = aAngle;
            mLastPos.set(newOffsetX, newOffsetY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.release();
        }
    }

    /**
     * Returns the position where the {@link ISGenericRTO} should be drawn.
     *
     * @param aRatio  the ratio used when drawing.
     * @param aOffset the drawing offset
     * @return the {@link Point} where the {@link ISGenericRTO} should be drawn.
     */
    protected Point get2DDrawingPosition(double aRatio, Point aOffset) {
        Point drawingPosition;
        if (mPosition != null) {
            int drawingPosX = aOffset.x + (int) (mPosition.getX() * aRatio);
            int drawingPosY = aOffset.y + (int) (mPosition.getY() * aRatio);

            drawingPosition = new Point(drawingPosX, drawingPosY);
        } else {
            int drawingPosX = aOffset.x;
            int drawingPosY = aOffset.y;

            drawingPosition = new Point(drawingPosX, drawingPosY);
        }
        return drawingPosition;
    }

    /**
     * Draws the bitmap associated to this GenericRTO.
     *
     * @param canvas          The destination Canvas where this object should be drawn to.
     * @param drawingPosition the position where it should be drawn on the canvas.
     */
    public void drawMarker(Canvas canvas, Point drawingPosition) {
        m2DIconRect.left = drawingPosition.x - sMarkerBitmap.getWidth() / 2;
        m2DIconRect.top = drawingPosition.y - sMarkerBitmap.getHeight() / 2;
        m2DIconRect.right = m2DIconRect.left + sMarkerBitmap.getWidth();
        m2DIconRect.bottom = m2DIconRect.top + sMarkerBitmap.getHeight();

        canvas.drawBitmap(sMarkerBitmap, null, m2DIconRect, null);
    }

    /**
     * Draws the window and the label of the {@link ISGenericRTO}.
     *
     * @param canvas          where the name should be drawn.
     * @param drawingPosition the {@link ISGenericRTO} drawing position. The position where the annotation will be drawn depends on this last position and on the name
     *                        anchor (see {@link ISGenericRTO#setWindowMargin(Point)}).
     */
    public void drawWindow(Canvas canvas, Point drawingPosition) {

        Rect textBounds = new Rect();
        mWindowPaint.getTextBounds(mAnnotationLabel, 0, mAnnotationLabel.length(), textBounds);
        int textWidth = textBounds.width();
        int textHeight = textBounds.height();

        int actionWidth = mIsActionDisplayed ? (2 * mActionMargin.x + sActionBitmap.getWidth()) : 0;
        int indicatorWidth = mIsIndicatorDisplayed ? (2 * mIndicatorMargin.x + sIndicatorBitmap.getWidth()) : 0;

        int rectWidth = textWidth + 2 * mNameMargin.x + actionWidth + indicatorWidth;
        int rectHeight = Math.max(textHeight + 2 * mNameMargin.y, sActionBitmap.getHeight() + 2 * mActionMargin.y);

        // 1 - Draw the Window background
        mWindowRect.bottom = drawingPosition.y - (sMarkerBitmap.getHeight() / 2 + mWindowAttacherSize) - mWindowMargin.x;
        mWindowRect.left = drawingPosition.x - rectWidth / 2 + mWindowMargin.y;
        mWindowRect.top = mWindowRect.bottom - rectHeight;
        mWindowRect.right = mWindowRect.left + rectWidth;

        drawAttacher(canvas, drawingPosition, mWindowRect);

        mWindowPaint.setStyle(Style.FILL);
        mWindowPaint.setColor((mIsAnnotationTouched) ? mAnnotationBkgPressedColor : mAnnotationBkgNormalColor);
        canvas.drawRoundRect(mWindowRect, mWindowRadius, mWindowRadius, mWindowPaint);


        float textX = mWindowRect.left + actionWidth + mNameMargin.x;
        float textY = mWindowRect.bottom - ((rectHeight - textHeight) / 2);

        mWindowPaint.setColor(mAnnotationTextColor);
        canvas.drawText(mAnnotationLabel, textX, textY, mWindowPaint);

        // 2 - Draw the action icon
        if (mIsActionDisplayed) drawAction(canvas, mWindowRect);

        // 3 - Draw the indicator icon
        if (mIsIndicatorDisplayed) drawIndicator(canvas, mWindowRect);


    }

    /**
     * Method that will draw the window attacher.
     *
     * @param canvas          where the attacher will be drawn.
     * @param drawingPosition is the position where the {@link ISIRTO} should drawn. By defaults it represents the marker's center.
     * @param windowRect      the {@link RectF} representing the window.
     */
    protected void drawAttacher(Canvas canvas, Point drawingPosition, RectF windowRect) {
        PointF p1, p2, p3;

        p1 = new PointF(drawingPosition.x - mWindowAttacherSize / 2, windowRect.bottom - mWindowPaint.getStrokeWidth() - 4);
        p2 = new PointF(drawingPosition.x + mWindowAttacherSize / 2, windowRect.bottom - mWindowPaint.getStrokeWidth() - 4);
        p3 = new PointF(drawingPosition.x, windowRect.bottom + mWindowAttacherSize + mWindowPaint.getStrokeWidth());

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        mWindowPaint.setStyle(Style.FILL);
        mWindowPaint.setColor((mIsAnnotationTouched) ? mAnnotationBkgPressedColor : mAnnotationBkgNormalColor);
        canvas.drawPath(path, mWindowPaint);

    }

    /**
     * Method that will draw the action in the window.
     *
     * @param canvas     where the action will be drawn.
     * @param windowRect the {@link RectF} representing the window.
     */
    protected void drawAction(Canvas canvas, RectF windowRect) {
        mActionBorderRect.bottom = mWindowRect.bottom;
        mActionBorderRect.left = mWindowRect.left;
        mActionBorderRect.top = mWindowRect.top;
        mActionBorderRect.right = mWindowRect.left + mWindowRadius * 2;

        mActionMainRect.bottom = mWindowRect.bottom;
        mActionMainRect.left = mWindowRect.left + mWindowRadius;
        mActionMainRect.top = mWindowRect.top;
        mActionMainRect.right = mActionMainRect.left + (sActionBitmap.getWidth() + mActionMargin.x * 2 - mWindowRadius);

        mWindowPaint.setColor((mIsActionTouched) ? mActionColorPressed : mActionColorNormal);
        canvas.drawRoundRect(mActionBorderRect, mWindowRadius, mWindowRadius, mWindowPaint);
        canvas.drawRect(mActionMainRect, mWindowPaint);

        mActionIconRect.bottom = mWindowRect.bottom - ((windowRect.height() - sActionBitmap.getHeight()) / 2);
        mActionIconRect.left = mWindowRect.left + mActionMargin.x;
        mActionIconRect.top = mActionIconRect.bottom - sActionBitmap.getHeight();
        mActionIconRect.right = mActionIconRect.left + sActionBitmap.getWidth();
        canvas.drawBitmap(sActionBitmap, null, mActionIconRect, null);
    }

    /**
     * Method that will draw the indicator in the window.
     *
     * @param canvas     where the indicator will be drawn.
     * @param windowRect the {@link RectF} representing the window.
     */
    protected void drawIndicator(Canvas canvas, RectF windowRect) {
        mIndicatorIconRect.bottom = mWindowRect.bottom - ((windowRect.height() - sIndicatorBitmap.getHeight()) / 2);
        mIndicatorIconRect.right = mWindowRect.right - mIndicatorMargin.x;
        mIndicatorIconRect.top = mIndicatorIconRect.bottom - sIndicatorBitmap.getHeight();
        mIndicatorIconRect.left = mWindowRect.right - sIndicatorBitmap.getWidth();
        canvas.drawBitmap(sIndicatorBitmap, null, mIndicatorIconRect, null);
    }

    /**
     * Method that will draw the label under the image. You can override this method to draw the label somewhere else or not to draw it.
     *
     * @param canvas          where the label will be drawn.
     * @param drawingPosition is the position where the {@link ISIRTO} should drawn. By defaults it represents the marker's center.
     */
    protected void drawLabel(Canvas canvas, Point drawingPosition, int verticalPadding) {
        int textWidth = (int) mLabelPaint.measureText(mLabel);
        int textHeight = (int) mLabelPaint.getTextSize();

        int textX = drawingPosition.x - textWidth / 2;
        int textY = drawingPosition.y + verticalPadding + textHeight;

        mLabelPaint.setStyle(Style.FILL_AND_STROKE);
        mLabelPaint.setColor(mLabelStrokeColor);
        canvas.drawText(mLabel, textX, textY, mLabelPaint);

        mLabelPaint.setStyle(Style.FILL);
        mLabelPaint.setColor(mLabelTextColor);
        canvas.drawText(mLabel, textX, textY, mLabelPaint);
    }

    /**
     * Sets the anchor to use when drawing the {@link ISGenericRTO} name.
     *
     * @param margin that should be used when drawing the name.
     */
    public void setWindowMargin(Point margin) {
        mWindowMargin = margin;
    }

    /**
     * Sets the margin that should be used on the action button.
     *
     * @param margin the margin that should be used.
     */
    public void setActionMargin(Point margin) {
        mActionMargin = margin;
    }

    /**
     * Sets the margin that should be used on the name.
     *
     * @param margin that should be used.
     */
    public void setNameMargin(Point margin) {
        mNameMargin = margin;
    }

    /**
     * Sets the margin that should be used on the indicator.
     *
     * @param margin the margin that should be used.
     */
    public void setIndicatorMargin(Point margin) {
        mIndicatorMargin = margin;
    }

    private int getWindowHeight() {
        Rect textBounds = new Rect();
        mWindowPaint.getTextBounds(mAnnotationLabel, 0, mAnnotationLabel.length(), textBounds);

        return Math.max(textBounds.height() + ((mNameMargin != null) ? 2 * mNameMargin.y : 0),
                Math.max(mActionHeight, ((sActionBitmap != null) ? sActionBitmap.getHeight() : 0) + ((mActionMargin != null) ? 2 * mActionMargin.y : 0)));
    }

    /**
     * Creates the marker {@link Texture}. As the same should be used for all RTO we check if it already exist
     */
    protected void createNPOTMarkerTexture() {
        if (!TextureManager.getInstance().containsTexture(MARKER_TEXTURE)) {
            mMarkerBitmapWidth = sMarkerBitmap.getWidth();
            mMarkerBitmapHeight = sMarkerBitmap.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(mMarkerBitmapWidth, mMarkerBitmapHeight, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);

            RectF markerIconRect = new RectF(
                    0,
                    0,
                    mMarkerBitmapWidth,
                    mMarkerBitmapHeight);

            canvas.drawBitmap(sMarkerBitmap, null, markerIconRect, null);

            Texture markerTexture = ISUtils.createNPOTTexture(bitmap);
            TextureManager.getInstance().addTexture(MARKER_TEXTURE, markerTexture);
        }

        if (sMarkerBitmap != null) sMarkerBitmap.recycle();
        sMarkerBitmap = null;

        mMarkerDisplayRect = new Rect(
                0,
                0,
                mMarkerBitmapWidth,
                mMarkerBitmapHeight);

    }

    private void createNPOTLabelTexture() {

        int width = (int) mLabelPaint.measureText(mLabel);
        int height = (int) mLabelPaint.getTextSize();

        width += (width % 2 == 0) ? 0 : 1;
        height += (height % 2 == 0) ? 0 : 1;


        Bitmap labelBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(labelBitmap);

        mLabelDisplayRect = new Rect(0, 0, width, height);

        mLabelPaint.setStyle(Style.FILL_AND_STROKE);
        mLabelPaint.setColor(mLabelStrokeColor);
        canvas.drawText(mLabel, 0, height - 4, mLabelPaint);

        mLabelPaint.setStyle(Style.FILL);
        mLabelPaint.setColor(mLabelTextColor);
        canvas.drawText(mLabel, 0, height - 4, mLabelPaint);


        Texture labelTexture = ISUtils.createNPOTTexture(labelBitmap);

        if (TextureManager.getInstance().containsTexture(LABEL_TEXTURE)) {
            TextureManager.getInstance().replaceTexture(LABEL_TEXTURE, labelTexture);
        } else {
            TextureManager.getInstance().addTexture(LABEL_TEXTURE, labelTexture);
        }
    }

    /**
     * Create the 2 {@link Texture} that will be used for the annotation view. The default one and the touched one.
     */
    private void createNPOTAnnotationTexture() {
        Rect textBounds = new Rect();
        mWindowPaint.getTextBounds(mAnnotationLabel, 0, mAnnotationLabel.length(), textBounds);

        mAnnotationDisplayRect = new Rect(0, 0, (int) (textBounds.width() + ((mNameMargin != null) ? 2 * mNameMargin.x : 0) + 2 * mWindowRadius), (int) (getWindowHeight() + mWindowAttacherSize));
        if (mAnnotationDisplayRect.width() % 2 != 0) mAnnotationDisplayRect.right += 1;
        if (mAnnotationDisplayRect.height() % 2 != 0) mAnnotationDisplayRect.bottom += 1;

        Bitmap annotationTextureBm = Bitmap.createBitmap(mAnnotationDisplayRect.width(), mAnnotationDisplayRect.height(), Bitmap.Config.ARGB_4444);

        Canvas annotationCanvas = new Canvas(annotationTextureBm);

        // 1 - Draw the Window background
        RectF annotationRect = new RectF(
                (annotationTextureBm.getWidth() - mAnnotationDisplayRect.width()) / 2,
                (annotationTextureBm.getHeight() - mAnnotationDisplayRect.height()) / 2,
                (annotationTextureBm.getWidth() - mAnnotationDisplayRect.width()) / 2 + mAnnotationDisplayRect.width(),
                (annotationTextureBm.getHeight() - mAnnotationDisplayRect.height()) / 2 + mAnnotationDisplayRect.height() - mWindowAttacherSize);

        // 2 - Draw the attacher
        PointF p1 = new PointF((annotationTextureBm.getWidth() - mWindowAttacherSize) / 2, annotationRect.bottom);
        PointF p2 = new PointF((annotationTextureBm.getWidth() + mWindowAttacherSize) / 2, annotationRect.bottom);
        PointF p3 = new PointF(annotationTextureBm.getWidth() / 2, annotationRect.bottom + mWindowAttacherSize);

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        float textX = annotationRect.left + ((mNameMargin != null) ? mNameMargin.x : 0);
        float textY = annotationRect.bottom - ((annotationRect.height() - textBounds.height()) / 2);

        mWindowPaint.setStyle(Style.FILL);
        mWindowPaint.setStrokeWidth(0);
        mWindowPaint.setStrokeMiter(0);

        mWindowPaint.setColor(mAnnotationBkgNormalColor);
        annotationCanvas.drawPath(path, mWindowPaint);
        annotationCanvas.drawRoundRect(annotationRect, mWindowRadius, mWindowRadius, mWindowPaint);

        mWindowPaint.setColor(mAnnotationTextColor);
        annotationCanvas.drawText(mAnnotationLabel, textX, textY, mWindowPaint);

        NPOTTexture windowTexture = ISUtils.createNPOTTexture(annotationTextureBm);
        if (TextureManager.getInstance().containsTexture(ANNOTATION_TEXTURE)) {
            TextureManager.getInstance().replaceTexture(ANNOTATION_TEXTURE, windowTexture);
        } else {
            TextureManager.getInstance().addTexture(ANNOTATION_TEXTURE, windowTexture);
        }

        annotationCanvas.drawColor(Color.TRANSPARENT);
        mWindowPaint.setColor(mAnnotationBkgPressedColor);
        annotationCanvas.drawPath(path, mWindowPaint);
        annotationCanvas.drawRoundRect(annotationRect, mWindowRadius, mWindowRadius, mWindowPaint);

        mWindowPaint.setColor(mAnnotationTextColor);
        annotationCanvas.drawText(mAnnotationLabel, textX, textY, mWindowPaint);

        Texture windowTouchedTexture = ISUtils.createNPOTTexture(annotationTextureBm);
        if (TextureManager.getInstance().containsTexture(ANNOTATION_TOUCHED_TEXTURE)) {
            TextureManager.getInstance().replaceTexture(ANNOTATION_TOUCHED_TEXTURE, windowTouchedTexture);
        } else {
            TextureManager.getInstance().addTexture(ANNOTATION_TOUCHED_TEXTURE, windowTouchedTexture);
        }
    }

    private void createNPOTActionTextures() {
        if (!TextureManager.getInstance().containsTexture(ACTION_TEXTURE)) {

            mActionWidth = (int) (sActionBitmap.getWidth() + ((mActionMargin != null) ? mActionMargin.x * 2 : 0) + 2 * mWindowRadius);
            mActionHeight = sActionBitmap.getHeight() + ((mActionMargin != null) ? mActionMargin.y * 2 : 0);

            Bitmap actionTextureBm = Bitmap.createBitmap(mActionWidth, mActionHeight, Bitmap.Config.ARGB_4444);
            Canvas actionCanvas = new Canvas(actionTextureBm);


            RectF actionBorderLeftRect = new RectF(
                    0,
                    0,
                    mWindowRadius * 2,
                    mActionHeight);

            RectF actionMainRect = new RectF(
                    mWindowRadius,
                    0,
                    mWindowRadius + sActionBitmap.getWidth() + ((mActionMargin != null) ? mActionMargin.x * 2 : 0),
                    mActionHeight);

            RectF actionBorderRightRect = new RectF(
                    mActionWidth - 2 * mWindowRadius,
                    0,
                    mActionWidth,
                    mActionHeight);


            int iconLeft = (int) mWindowRadius + ((mActionMargin != null) ? mActionMargin.x : 0);
            int iconTop = ((mActionMargin != null) ? mActionMargin.y : 0);
            RectF actionIconRect = new RectF(
                    iconLeft,
                    iconTop,
                    iconLeft + sActionBitmap.getWidth(),
                    iconTop + sActionBitmap.getHeight());


            mWindowPaint.setColor(mActionColorNormal);
            actionCanvas.drawRoundRect(actionBorderLeftRect, mWindowRadius, mWindowRadius, mWindowPaint);
            actionCanvas.drawRect(actionMainRect, mWindowPaint);
            mWindowPaint.setColor(mAnnotationBkgNormalColor);
            actionCanvas.drawRect(actionBorderRightRect, mWindowPaint);
            actionCanvas.drawBitmap(sActionBitmap, null, actionIconRect, null);

            Texture actionTexture = ISUtils.createNPOTTexture(actionTextureBm);
            TextureManager.getInstance().addTexture(ACTION_TEXTURE, actionTexture);

            actionCanvas.drawColor(Color.TRANSPARENT);

            mWindowPaint.setColor(mActionColorPressed);
            actionCanvas.drawRoundRect(actionBorderLeftRect, mWindowRadius, mWindowRadius, mWindowPaint);
            actionCanvas.drawRect(actionMainRect, mWindowPaint);
            mWindowPaint.setColor(mAnnotationBkgNormalColor);
            actionCanvas.drawRect(actionBorderRightRect, mWindowPaint);
            actionCanvas.drawBitmap(sActionBitmap, null, actionIconRect, null);

            Texture actionTouchedTexture = ISUtils.createNPOTTexture(actionTextureBm);
            TextureManager.getInstance().addTexture(ACTION_TOUCHED_TEXTURE, actionTouchedTexture);

        }

        if (sActionBitmap != null) sActionBitmap.recycle();
        sActionBitmap = null;

        mActionDisplayRect = new Rect(0, 0, mActionWidth, mActionHeight);
    }

    private void createNPOTIndicatorTexture() {
        if (!TextureManager.getInstance().containsTexture(INDICATOR_TEXTURE)) {

            mIndicatorWidth = (int) (sIndicatorBitmap.getWidth() + 2 * mWindowRadius);
            mIndicatorHeight = getWindowHeight();

            Bitmap indicatorTextureBm = Bitmap.createBitmap(mIndicatorWidth, mIndicatorHeight, Bitmap.Config.ARGB_4444);
            Canvas indicatorCanvas = new Canvas(indicatorTextureBm);

            RectF indicatorMainRect = new RectF(
                    0,
                    0,
                    mIndicatorWidth,
                    mIndicatorHeight);

            int iconLeft = (int) indicatorMainRect.width() / 2 - sIndicatorBitmap.getWidth() / 2;
            int iconTop = (int) indicatorMainRect.height() / 2 - sIndicatorBitmap.getHeight() / 2;
            RectF indicatorIconRect = new RectF(
                    iconLeft,
                    iconTop,
                    iconLeft + sIndicatorBitmap.getWidth(),
                    iconTop + sIndicatorBitmap.getHeight());

            mWindowPaint.setStyle(Style.FILL);

            mWindowPaint.setColor(mAnnotationBkgNormalColor);
            indicatorCanvas.drawRoundRect(indicatorMainRect, mWindowRadius, mWindowRadius, mWindowPaint);
            indicatorCanvas.drawBitmap(sIndicatorBitmap, null, indicatorIconRect, null);

            Texture indicatorTexture = ISUtils.createNPOTTexture(indicatorTextureBm);
            TextureManager.getInstance().addTexture(INDICATOR_TEXTURE, indicatorTexture);

            mWindowPaint.setColor(mAnnotationBkgPressedColor);
            indicatorCanvas.drawRoundRect(indicatorMainRect, mWindowRadius, mWindowRadius, mWindowPaint);
            indicatorCanvas.drawBitmap(sIndicatorBitmap, null, indicatorIconRect, null);

            Texture indicatorTouchedTexture = ISUtils.createNPOTTexture(indicatorTextureBm);
            TextureManager.getInstance().addTexture(INDICATOR_TOUCHED_TEXTURE, indicatorTouchedTexture);
        }

        if (sIndicatorBitmap != null) sIndicatorBitmap.recycle();
        sIndicatorBitmap = null;

        mIndicatorDisplayRect = new Rect(0, 0, mIndicatorWidth, mIndicatorHeight);
    }

    protected void createMarker(ISWorld world) {
        mMarkerOverlay = world.createOverlay(0, 0, mMarkerBitmapWidth, mMarkerBitmapHeight, MARKER_TEXTURE, 20);
        mMarkerOverlay.setDepth(Config.nearPlane + MARKER_DEPTH_OFFSET);
        mMarkerOverlay.setVisibility(false);
        mMarkerOverlay.getObject3D().setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
    }

    private void createLabel(ISWorld world) {
        if (mLabel != null) createNPOTLabelTexture();
        mLabelOverlay = world.createOverlay(0, 0, mLabelDisplayRect.width(), mLabelDisplayRect.height(), LABEL_TEXTURE, 20);
        mLabelOverlay.setDepth(Config.nearPlane + MARKER_DEPTH_OFFSET + 2);
        mLabelOverlay.setVisibility(false);
    }

    private void createAnnotation(ISWorld world) {
        createNPOTAnnotationTexture();

        mAnnotationOverlay = world.createOverlay(0, 0, mAnnotationDisplayRect.width(), mAnnotationDisplayRect.height(), ANNOTATION_TEXTURE, 20);
        mAnnotationOverlay.setDepth(Config.nearPlane + ANNOTATION_DEPTH_OFFSET);
        mAnnotationOverlay.setVisibility(false);
        mAnnotationOverlay.getObject3D().setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
    }

    private void createAction(ISWorld world) {
        createNPOTActionTextures();
        mActionOverlay = world.createOverlay(0, 0, 0, 0, ACTION_TEXTURE, 20);
        mActionOverlay.setDepth(Config.nearPlane + ACTION_DEPTH_OFFSET);
        mActionOverlay.setVisibility(false);
        mActionOverlay.getObject3D().setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
    }

    private void createIndicator(ISWorld world) {
        createNPOTIndicatorTexture();
        mIndicatorOverlay = world.createOverlay(0, 0, 0, 0, INDICATOR_TEXTURE, 20);
        mIndicatorOverlay.setDepth(Config.nearPlane + INDICATOR_DEPTH_OFFSET);
        mIndicatorOverlay.setVisibility(false);
        mIndicatorOverlay.getObject3D().setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
    }

    @Override
    public void render3D(ISWorld world, FrameBuffer frameBuffer, ISMap map, double ratio, float angle) {
        mMap = map;
        mWorld = world;
        mFrameBuffer = frameBuffer;

        if (mMarkerOverlay == null) createMarker(world);


        if (mPosition != null) {
            m3DObject.setOrigin(get3DDrawingPosition(map));
        }

        SimpleVector centerOnScreen = Interact2D.project3D2D(world.getCamera(), frameBuffer, m3DObject.getOrigin());

        if (centerOnScreen != null) {

            if (mMarkerOverlay != null) {
                mMarkerOverlay.setVisibility(true);

                mMarkerOverlay.setNewCoordinates((int) centerOnScreen.x - mMarkerBitmapWidth / 2, (int) centerOnScreen.y - mMarkerBitmapWidth / 2,
                        (int) centerOnScreen.x + mMarkerBitmapWidth / 2, (int) centerOnScreen.y + mMarkerBitmapWidth / 2);

                mMarkerDisplayRect.set((int) centerOnScreen.x - mMarkerBitmapWidth / 2, (int) centerOnScreen.y - mMarkerBitmapWidth / 2,
                        (int) centerOnScreen.x + mMarkerBitmapWidth / 2, (int) centerOnScreen.y + mMarkerBitmapWidth / 2);

                if (mIsLabelDisplayed && mLabel != null && !mLabel.isEmpty()) {
                    if (mLabelOverlay == null) createLabel(world);

                    int labelUpperLeftX = (int) (centerOnScreen.x - mLabelDisplayRect.width() / 2);
                    int labelUpperLeftY = (int) (centerOnScreen.y + mMarkerBitmapWidth / 2);

                    mLabelOverlay.setNewCoordinates(labelUpperLeftX, labelUpperLeftY,
                            labelUpperLeftX + mLabelDisplayRect.width(),
                            labelUpperLeftY + mLabelDisplayRect.height());

                    mLabelDisplayRect.set(labelUpperLeftX, labelUpperLeftY,
                            labelUpperLeftX + mLabelDisplayRect.width(),
                            labelUpperLeftY + mLabelDisplayRect.height());

                } else {
                    if (mLabelOverlay != null) mLabelOverlay.setVisibility(false);
                }

                render3DWindow(world, centerOnScreen);

            }
        }

        mLastRatio = ratio;
    }

    protected void render3DWindow(ISWorld world, SimpleVector centerOnScreen) {
        if (mIsWindowDisplayed) {

            if (mAnnotationOverlay == null) createAnnotation(world);

            mAnnotationOverlay.setVisibility(true);

            int windowUpperLeftX = (int) (centerOnScreen.x - mAnnotationDisplayRect.width() / 2);
            int windowUpperLeftY = (int) (centerOnScreen.y - mMarkerBitmapHeight / 2 - mAnnotationDisplayRect.height());

            mAnnotationOverlay.setNewCoordinates(windowUpperLeftX, windowUpperLeftY,
                    windowUpperLeftX + mAnnotationDisplayRect.width(),
                    windowUpperLeftY + mAnnotationDisplayRect.height());

            mAnnotationDisplayRect.set(windowUpperLeftX, windowUpperLeftY,
                    windowUpperLeftX + mAnnotationDisplayRect.width(),
                    windowUpperLeftY + mAnnotationDisplayRect.height());

            if (mIsAnnotationTouched) {
                mAnnotationOverlay.setTexture(ANNOTATION_TOUCHED_TEXTURE);
            } else {
                mAnnotationOverlay.setTexture(ANNOTATION_TEXTURE);
            }

            render3DAction(world);
            render3DIndicator(world);


        } else {
            if (mAnnotationOverlay != null) mAnnotationOverlay.setVisibility(false);
            if (mActionOverlay != null) mActionOverlay.setVisibility(false);
            if (mIndicatorOverlay != null) mIndicatorOverlay.setVisibility(false);
        }
    }

    protected void render3DAction(ISWorld world) {
        if (mIsActionDisplayed) {

            if (mActionOverlay == null) createAction(world);

            mActionOverlay.setVisibility(true);

            int actionUpperLeftX = (int) (mAnnotationDisplayRect.left - mActionDisplayRect.width() + mWindowRadius);
            int actionUpperLeftY = mAnnotationDisplayRect.top;

            mActionOverlay.setNewCoordinates(actionUpperLeftX, actionUpperLeftY, actionUpperLeftX + mActionDisplayRect.width(), actionUpperLeftY + mActionDisplayRect.height());

            mActionDisplayRect.set(actionUpperLeftX, actionUpperLeftY, actionUpperLeftX + mActionDisplayRect.width(), actionUpperLeftY + mActionDisplayRect.height());

            if (mIsActionTouched) {
                mActionOverlay.setTexture(ACTION_TOUCHED_TEXTURE);
            } else {
                mActionOverlay.setTexture(ACTION_TEXTURE);
            }
        } else {
            if (mActionOverlay != null) mActionOverlay.setVisibility(false);
        }
    }

    protected void render3DIndicator(ISWorld world) {

        if (mIsIndicatorDisplayed) {

            if (mIndicatorOverlay == null) createIndicator(world);

            mIndicatorOverlay.setVisibility(true);

            int indicatorUpperLeftX = (int) (mAnnotationDisplayRect.right - 2 * mWindowRadius);
            int indicatorUpperLeftY = mAnnotationDisplayRect.top;

            mIndicatorOverlay.setNewCoordinates(indicatorUpperLeftX, indicatorUpperLeftY,
                    indicatorUpperLeftX + mIndicatorDisplayRect.width(),
                    indicatorUpperLeftY + mIndicatorDisplayRect.height());

            mIndicatorDisplayRect.set(indicatorUpperLeftX, indicatorUpperLeftY,
                    indicatorUpperLeftX + mIndicatorDisplayRect.width(),
                    indicatorUpperLeftY + mIndicatorDisplayRect.height());

            if (mIsAnnotationTouched) {
                mIndicatorOverlay.setTexture(INDICATOR_TOUCHED_TEXTURE);
            } else {
                mIndicatorOverlay.setTexture(INDICATOR_TEXTURE);
            }
        } else {
            if (mIndicatorOverlay != null) mIndicatorOverlay.setVisibility(false);
        }
    }

    protected SimpleVector get3DDrawingPosition(ISMap map) {
        SimpleVector position = new SimpleVector(mPosition.getX() / map.getScale(), mPosition.getY() / map.getScale(), 0);
        if (mZoneOffset != null) {
            position.x += mZoneOffset.x / map.getScale();
            position.y += mZoneOffset.y / map.getScale();
            position.z += mZoneOffset.z / map.getScale();
        }

        return position;
    }

    @Override
    public ISObject3D get3DObject() {
        return m3DObject;
    }

    @Override
    public void remove3DObject(ISWorld world) {
        if (world != null) {
            if (m3DObject != null) {
                for (Object3D obj : m3DObject.getObjects()) {
                    world.removeObject(obj);
                    obj.clearObject();
                }
            }

            if (mMarkerOverlay != null) {
                world.destroyOverlay(mMarkerOverlay);
                mMarkerOverlay = null;
            }

            if (mLabelOverlay != null) {
                world.destroyOverlay(mLabelOverlay);
                mLabelOverlay = null;

                if (TextureManager.getInstance().containsTexture(LABEL_TEXTURE)) {
                    TextureManager.getInstance().removeTexture(LABEL_TEXTURE);
                }
            }

            if (mActionOverlay != null) {
                world.destroyOverlay(mActionOverlay);
                mActionOverlay = null;
            }

            if (mAnnotationOverlay != null) {
                world.destroyOverlay(mAnnotationOverlay);
                mAnnotationOverlay = null;

                if (TextureManager.getInstance().containsTexture(ANNOTATION_TEXTURE)) {
                    TextureManager.getInstance().removeTexture(ANNOTATION_TEXTURE);
                }
            }

            if (mIndicatorOverlay != null) {
                world.destroyOverlay(mIndicatorOverlay);
                mIndicatorOverlay = null;
            }
        }
    }

    @Override
    public ISETouchObjectResult onTouchDown(ISTouch aTouch) {
        ISETouchObjectResult touchResult = ISETouchObjectResult.RESULT_NOTHING;
        try {
            mLock.acquire();
            ISTouch rotatedTouch = new ISTouch(aTouch);
            // If the map is rotating the touch event needs to be rotated also
            if (mLastAngle != 0f) {
                rotatedTouch.rotate(mLastAngle, mLastPos.x, mLastPos.y);
            }

            if (mIsTouchable) {
                if (isTouchInsideMarker(rotatedTouch)) {
                    mIsMarkerTouched = true;

                    mTouchPoint.set(aTouch.getPosition().x, aTouch.getPosition().y);

                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                } else if (isTouchInsideAction(rotatedTouch)) {
                    mIsActionTouched = true;

                    mTouchPoint.set(aTouch.getPosition().x, aTouch.getPosition().y);
                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                } else if (isTouchInsideAnnotation(rotatedTouch)) {
                    mIsAnnotationTouched = true;

                    mTouchPoint.set(aTouch.getPosition().x, aTouch.getPosition().y);
                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.release();
        }

        return touchResult;
    }

    @Override
    public ISETouchObjectResult onTouchPointerDown(ISTouch aTouch) {
        return ISETouchObjectResult.RESULT_NOTHING;
    }

    @Override
    public ISETouchObjectResult onTouchMove(ISTouch aTouch) {
        ISETouchObjectResult touchResult = ISETouchObjectResult.RESULT_NOTHING;
        try {
            mLock.acquire();
            ISTouch rotatedTouch = new ISTouch(aTouch);
            if (mLastAngle != 0f) {
                rotatedTouch.rotate(mLastAngle, mLastPos.x, mLastPos.y);
            }

            if (mIsMarkerTouched) {
                if (mIsDragged) {

                    Point currentPoint = aTouch.getPosition();
                    if (mRenderingMode == ISERenderMode.MODE_2D) {


                        int dx = currentPoint.x - mTouchPoint.x;
                        int dy = currentPoint.y - mTouchPoint.y;

                        //move this RTO
                        ISPointD p = mPosition.getCoord();
                        p.x += dx / mLastRatio;
                        p.y += dy / mLastRatio;


                    } else {

                        SimpleVector mCurrentPoint3D = ISCoordConverter.convertScreenPointToWorldVector(mWorld, mFrameBuffer, aTouch.getPositionOrig());
                        mPosition.getCoord().x = mCurrentPoint3D.x * mMap.getScale();
                        mPosition.getCoord().y = mCurrentPoint3D.y * mMap.getScale();
                    }
                    mTouchPoint = currentPoint;

                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                } else if (isTouchInsideMarker(rotatedTouch)) {
                    touchResult = ISETouchObjectResult.RESULT_CONSUME;

                    if (mIsDraggable && mPosition != null) {
                        mIsDragged = true;

                        if (mRenderingMode == ISERenderMode.MODE_2D) {
                            Point currentPoint = aTouch.getPosition();

                            int dx = currentPoint.x - mTouchPoint.x;
                            int dy = currentPoint.y - mTouchPoint.y;

                            //move this RTO
                            ISPointD p = mPosition.getCoord();
                            p.x += dx / mLastRatio;
                            p.y += dy / mLastRatio;

                            mTouchPoint = currentPoint;

                            touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                        } else {

                            SimpleVector mCurrentPoint3D = ISCoordConverter.convertScreenPointToWorldVector(mWorld, mFrameBuffer, aTouch.getPositionOrig());
                            mPosition.getCoord().x = mCurrentPoint3D.x * mMap.getScale();
                            mPosition.getCoord().y = mCurrentPoint3D.y * mMap.getScale();
                        }


                    }
                } else {
                    mIsMarkerTouched = false;
                }
            }

            if (mIsActionTouched) {
                if (isTouchInsideAction(rotatedTouch)) {
                    touchResult = ISETouchObjectResult.RESULT_CONSUME;
                } else {
                    mIsActionTouched = false;
                }
            }

            if (mIsAnnotationTouched) {
                if (isTouchInsideAnnotation(rotatedTouch)) {
                    touchResult = ISETouchObjectResult.RESULT_CONSUME;
                } else {
                    mIsAnnotationTouched = false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.release();
        }

        return touchResult;
    }

    @Override
    public ISETouchObjectResult onTouchPointerUp(ISTouch aTouch) {
        return ISETouchObjectResult.RESULT_NOTHING;
    }

    @Override
    public ISETouchObjectResult onTouchUp(ISTouch aTouch) {
        ISETouchObjectResult touchResult = ISETouchObjectResult.RESULT_NOTHING;
        try {
            mLock.acquire();
            ISTouch rotatedTouch = new ISTouch(aTouch);
            // If the map is rotating the touch event needs to be rotated also
            if (mLastAngle != 0f) {
                rotatedTouch.rotate(mLastAngle, mLastPos.x, mLastPos.y);
            }

            if (mIsMarkerTouched) {
                if (isTouchInsideMarker(rotatedTouch)) {
                    mIsMarkerClicked = true;

                    if (shouldToggleWindowOnMarkerClick() /*&& !mIsDragged*/) {
                        mIsWindowDisplayed = !mIsWindowDisplayed;
                    }

                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                }
            } else if (mIsActionTouched) {
                if (isTouchInsideAction(rotatedTouch)) {
                    mIsActionClicked = true;
                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                }
            } else if (mIsAnnotationTouched) {
                if (isTouchInsideAnnotation(rotatedTouch)) {
                    mIsAnnotationClicked = true;
                    touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
                }
            }

            if (mIsDragged) {
                touchResult = ISETouchObjectResult.RESULT_NOTIFY_CONSUME;
            }

            mIsMarkerTouched = mIsDragged = mIsAnnotationTouched = mIsActionTouched = false;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.release();
        }
        return touchResult;
    }

    /**
     * Checks whether the {@link ISTouch} is inside the marker of the {@link ISGenericRTO}.
     *
     * @param touch to check.
     * @return true if the {@link ISTouch} is inside the {@link ISGenericRTO} marker.
     */
    public boolean isTouchInsideMarker(ISTouch touch) {
        if (mRenderingMode == ISERenderMode.MODE_3D) {
            boolean isInside = false;
            if (mMarkerDisplayRect != null && mMarkerOverlay != null) {
                if (mMarkerDisplayRect.contains(touch.getPositionOrig(0).x, touch.getPositionOrig(0).y)) {
                    isInside = mMarkerOverlay.getObject3D().wasTargetOfLastCollision();
                }
            }
            return isInside;
        } else {
            return mRenderingMode == ISERenderMode.MODE_2D && m2DIconRect.contains(touch.getPosition(0).x, touch.getPosition(0).y);
        }
    }

    /**
     * Checks whether the given touch is inside the annotation marker.
     *
     * @param touch to process.
     * @return true if it is inside and false otherwise.
     */
    protected boolean isTouchInsideAnnotation(ISTouch touch) {
        if (mRenderingMode == ISERenderMode.MODE_3D) {
            boolean isAnnotationTouched = false;
            boolean isIndicatorTouched = false;

            if (mIndicatorDisplayRect != null && mIsIndicatorDisplayed && mIndicatorOverlay != null) {
                isIndicatorTouched = mIndicatorDisplayRect.contains(touch.getPositionOrig(0).x, touch.getPositionOrig(0).y) && mIndicatorOverlay.getObject3D().wasTargetOfLastCollision();
            }

            if (mAnnotationDisplayRect != null && mIsWindowDisplayed && mAnnotationOverlay != null) {
                isAnnotationTouched = mAnnotationDisplayRect.contains(touch.getPositionOrig(0).x, touch.getPositionOrig(0).y) && mAnnotationOverlay.getObject3D().wasTargetOfLastCollision();
            }
            return isAnnotationTouched || isIndicatorTouched;
        } else {
            return mRenderingMode == ISERenderMode.MODE_2D && mIsWindowDisplayed && mWindowRect.contains(touch.getPosition(0).x, touch.getPosition(0).y);
        }
    }

    /**
     * Checks whether the given touch is inside the action marker.
     *
     * @param touch to process.
     * @return true if it is inside and false otherwise.
     */
    protected boolean isTouchInsideAction(ISTouch touch) {
        if (mRenderingMode == ISERenderMode.MODE_3D) {
            boolean isInside = false;
            if (mActionDisplayRect != null && mIsActionDisplayed && mActionOverlay != null) {
                isInside = mActionDisplayRect.contains(touch.getPositionOrig(0).x, touch.getPositionOrig(0).y) && mActionOverlay.getObject3D().wasTargetOfLastCollision();
            }
            return isInside;
        } else {
            return mRenderingMode == ISERenderMode.MODE_2D && mIsActionDisplayed && (mActionBorderRect.contains(touch.getPosition(0).x, touch.getPosition(0).y) || mActionMainRect.contains(touch.getPosition(0).x, touch.getPosition(0).y));
        }
    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public String toString() {
        return mLabel + "";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ISGenericRTO && this.getRtoID() == ((ISGenericRTO)o).getRtoID();
    }
}
