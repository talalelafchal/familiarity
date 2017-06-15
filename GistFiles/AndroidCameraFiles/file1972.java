package com.devlon.snazl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SingleFingerView extends LinearLayout {
    public AutoResizeTextView mView;
    public ImageView mPushView;
    public ImageView iv_fadein;
    private float _1dp;
    private boolean mCenterInParent = true;
    private String mTextDrawable;
    private Drawable mPushImageDrawable;
    private float mImageHeight, mImageWidth, mPushImageHeight, mPushImageWidth;
    private int mLeft = 0, mTop = 0;
    private ViewGroup _textViewcontainer;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleFingerView(Context context) {
        this(context, null, 0);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            mPushImageDrawable = context.getDrawable(R.drawable.push_btn);
        } else {
            mPushImageDrawable = context.getResources().getDrawable(R.drawable.push_btn);
        }
    }

    public SingleFingerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleFingerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._1dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        this.parseAttr(context, attrs);
        View mRoot = View.inflate(context, R.layout.test_image_view, null);
        addView(mRoot, -1, -1);

        mPushView = (ImageView) mRoot.findViewById(R.id.push_view);
        iv_fadein = (ImageView) mRoot.findViewById(R.id.iv_fadein);
        mView = (AutoResizeTextView) mRoot.findViewById(R.id.view);
        _textViewcontainer = (ViewGroup) mRoot.findViewById(R.id.container);
        mPushView.setOnTouchListener(new PushBtnTouchListener(context,mView,_textViewcontainer));
        mView.setOnTouchListener(new ViewOnTouchListener(context, mPushView, mView, iv_fadein));
        initForSingleFingerView();
    }

    private void parseAttr(Context context, AttributeSet attrs) {
        if (null == attrs) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleFingerView);
        if (a != null) {
            try {
                int n = a.getIndexCount();
                for (int i = 0; i < n; i++) {
                    int attr = a.getIndex(i);
                    if (attr == R.styleable.SingleFingerView_centerInParent) {
                        this.mCenterInParent = a.getBoolean(attr, true);
                    } else if (attr == R.styleable.SingleFingerView_text) {
                        this.mTextDrawable = (String) a.getText(attr);
                    } else if (attr == R.styleable.SingleFingerView_text_height) {
                        this.mImageHeight = a.getDimension(attr, 200 * _1dp);
                    } else if (attr == R.styleable.SingleFingerView_text_width) {
                        this.mImageWidth = a.getDimension(attr, 200 * _1dp);
                    } else if (attr == R.styleable.SingleFingerView_push_image) {
                        this.mPushImageDrawable = a.getDrawable(attr);
                    } else if (attr == R.styleable.SingleFingerView_push_image_width) {
                        this.mPushImageWidth = a.getDimension(attr, 50 * _1dp);
                    } else if (attr == R.styleable.SingleFingerView_push_image_height) {
                        this.mPushImageHeight = a.getDimension(attr, 50 * _1dp);
                    } else if (attr == R.styleable.SingleFingerView_left) {
                        this.mLeft = (int) a.getDimension(attr, 0 * _1dp);
                    } else if (attr == R.styleable.SingleFingerView_top) {
                        this.mTop = (int) a.getDimension(attr, 0 * _1dp);
                    }
                }
            }finally {
                a.recycle();
            }

        }
    }

    private void initForSingleFingerView() {
       /* ViewTreeObserver vto2 = mView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                FrameLayout.LayoutParams viewLP = (FrameLayout.LayoutParams) mView.getLayoutParams();
                FrameLayout.LayoutParams pushViewLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
                pushViewLP.width = (int) mPushImageWidth;
                pushViewLP.height = (int) mPushImageHeight;
                pushViewLP.leftMargin = (viewLP.leftMargin + mView.getWidth()) - mPushView.getWidth() / 2;
                pushViewLP.topMargin = (viewLP.topMargin + mView.getHeight()) - mPushView.getWidth() / 2;
                mPushView.setLayoutParams(pushViewLP);
            }
        });*/
    }

    private void setViewToAttr(int pWidth, int pHeight) {
        if (null != mTextDrawable) {
            //this.mView.setText("Text");
            this.mView.setHint("Text");
//            this.mView.setHintTextColor(Color.parseColor("#000000"));
        }
        if (null != mPushImageDrawable) {
            this.mPushView.setBackgroundDrawable(mPushImageDrawable);
            this.mPushImageWidth = 50f;
            this.mPushImageHeight = 50f;
        }else {
            this.mPushImageWidth = 50f;
            this.mPushImageHeight = 50f;
        }

        FrameLayout.LayoutParams viewLP = (FrameLayout.LayoutParams) this.mView.getLayoutParams();
        viewLP.width = (int) mImageWidth;
        viewLP.height = (int) mImageHeight;
        int left = 0, top = 0;
        if (mCenterInParent) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = Constant.ivBackground.getHeight();
            int width = Constant.ivBackground.getWidth();
            if (viewLP.width>0) {
                left = pWidth/2 - width/7 ;
                top =  pWidth/2  - height/2;
                Log.d("sligerviewheight1",String.valueOf(left)+" "+String.valueOf(top)+" pwidth:"+pWidth+" viewlpwidth"+viewLP.width+" viewheight:"+pHeight+" viewlpheght:"+viewLP.height);

            }
            else
            {
                left = pWidth/2 - width/7 ;
                top =  pWidth/2  - height/2;
                Log.d("sligerviewheight1",String.valueOf(left)+" "+String.valueOf(top)+" pwidth:"+pWidth+" viewlpwidth"+width+" viewheight:"+pHeight+" viewlpheght:"+height);

            }

        } else {

            left = pWidth /2 - viewLP.width /6;
            top = pHeight /2 - viewLP.height /6;
            Log.d("sligerviewheight2",String.valueOf(left)+" "+String.valueOf(top));

        }

        viewLP.leftMargin = left;
        viewLP.topMargin = top;
        viewLP.height = 100;
        viewLP.width = 150;
        this.mView.setLayoutParams(viewLP);
        this.iv_fadein.setLayoutParams(viewLP);

        FrameLayout.LayoutParams pushViewLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
        pushViewLP.width = (int) mPushImageWidth;
        pushViewLP.height = (int) mPushImageHeight;
        pushViewLP.leftMargin = (int) (viewLP.leftMargin + 150 - mPushImageWidth / 3);
        pushViewLP.topMargin = (int) (viewLP.topMargin + 100 - mPushImageHeight / 3);
        mPushView.setLayoutParams(pushViewLP);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("onmeasure",String.valueOf(widthMeasureSpec)+" "+String.valueOf(heightMeasureSpec));
        setParamsForView(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean hasSetParamsForView = false;

    private void setParamsForView(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (null != layoutParams && !hasSetParamsForView) {
            System.out.println("setParamsForView");
            hasSetParamsForView = true;
            int width;
            if ((getLayoutParams().width == LayoutParams.MATCH_PARENT)) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                width = getLayoutParams().width;
            }
            int height;
            if ((getLayoutParams().height == LayoutParams.MATCH_PARENT)) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                height = getLayoutParams().height;
            }
            setViewToAttr(width, height);
        }
    }
}
