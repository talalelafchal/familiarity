package me.dontenvy.videotest;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


public class MainMenu extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int myDraggingState = 0;
    private Button myButton = null;
    private Button takeImageNavButton = null;
    private ViewDragHelper myDragHelper;
    private int myDraggingBorder;
    private int myVerticalRange;
    private boolean myIsOpen; // true = slid up, false = at the bottom

    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == myDraggingState) { // no change
                return;
            }
            if ((myDraggingState == ViewDragHelper.STATE_DRAGGING || myDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (myDraggingBorder == 0) {
                } else if (myDraggingBorder == -myVerticalRange) {
                    myIsOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
            }
            myDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            myDraggingBorder = top;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return myVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.slide_menu);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = myVerticalRange;
            final int bottomBound = getPaddingBottom();
            return Math.max(Math.min(top, bottomBound), -topBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = -myVerticalRange;
            if (myDraggingBorder == 0) {
                return;
            }
            if (myDraggingBorder == rangeToCheck) {
                return;
            }
            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = false;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (myDraggingBorder > rangeToCheck / 2) {
                settleToOpen = false;
            } else if (myDraggingBorder < rangeToCheck / 2) {
                settleToOpen = true;
            }

            final int settleDestY = settleToOpen ? -myVerticalRange : 0;

            if(myDragHelper.settleCapturedViewAt(0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(MainMenu.this);
            }
        }
    }

    class ButtonArea {
        int x;
        int y;
        int diameter;

        public ButtonArea(Button b){
            int [] bLocation = new int [2];
            b.getLocationOnScreen(bLocation);
            x = bLocation[0];
            y = bLocation[1];
            diameter = b.getMeasuredHeight();
        }

        public boolean isTarget(MotionEvent event){
            boolean inY = (event.getRawY() > y) && (event.getRawY() < (y + diameter));
            boolean inX = (event.getRawX() > x) && (event.getRawX() < (x + diameter));
            return(inY && inX);
        }
    }

    public MainMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        myIsOpen = false;
    }

    @Override
    protected void onFinishInflate() {
        myButton = (Button) findViewById(R.id.slide_button);
        myDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        takeImageNavButton = (Button) findViewById(R.id.take_image_nav_button);
        takeImageNavButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                open();
            }
        });
        myIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        myVerticalRange = (h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private boolean isQueenTarget(MotionEvent event) {
        int[] queenLocation = new int[2];
        myButton.getLocationOnScreen(queenLocation);
        int upperLimit = queenLocation[1] + myButton.getMeasuredHeight();
        int lowerLimit = queenLocation[1];
        int y = (int) event.getRawY();
        return (y > lowerLimit && y < upperLimit);
    }

    private boolean isButtonTarget(MotionEvent event){
        ButtonArea takeImageNavButtonArea = new ButtonArea(takeImageNavButton);

        if (takeImageNavButtonArea.isTarget(event)){
            return true;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if ((!isButtonTarget(event)) && isQueenTarget(event) && myDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((!isButtonTarget(event)) && (isQueenTarget(event) || isMoving())) {
            myDragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (myDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean isMoving() {
        return (myDraggingState == ViewDragHelper.STATE_DRAGGING ||
                myDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return (myVerticalRange == -myDraggingBorder || myIsOpen);
    }

    public void close(){
        myDragHelper.smoothSlideViewTo(findViewById(R.id.slide_menu),0,0);
        bringChildToFront(findViewById(R.id.slide_menu));
        takeImageNavButton.setEnabled(true);
        myIsOpen = false;
    }

    public void open(){
        bringChildToFront(findViewById(R.id.camera_container));
        takeImageNavButton.setEnabled(false);
        myIsOpen = true;
    }
}