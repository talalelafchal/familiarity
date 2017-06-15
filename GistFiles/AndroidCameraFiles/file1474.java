package com.team.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 *自定义view
 */
public class MyCustomView extends View{

    private static final String TAG = "MyCustomView";


    /**
     * 图片Bitmap
     */
    private Bitmap imageBitmap;


    /**
     * 图片的长宽比
     */
    private float imageAspectRatio;
    /**
     * 图片的透明度
     */
    private float imageAlpha;
    /**
     * 图片的左padding
     */
    private int imagePaddingLeft;
    /**
     * 图片的上padding
     */
    private int imagePaddingTop;
    /**
     * 图片的右padding
     */
    private int imagePaddingRight;
    /**
     * 图片的下padding
     */
    private int imagePaddingBottom;
    /**
     * 图片伸缩模式
     */
    private int imageScaleType;
    /**
     * 图片伸缩模式常量 fillXY
     */
    private static final int SCALE_TYPE_FILLXY = 0;
    /**
     * 图片伸缩模式常量 center
     */
    private static final int SCALE_TYPE_CENTER = 1;


    /**
     * 控件用的paint
     */
    private Paint paint;
    /**
     * 宽度和高度的最小值
     */
    private static final int MIN_SIZE = 12;
    /**
     * 控件的宽度
     */
    private int mViewWidth;
    /**
     * 控件的高度
     */
    private int mViewHeight;

    private String xMessage;




    public MyCustomView(Context context) {
        this(context,  null, 0);

    }

    public MyCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs, defStyle);
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.MyCustomView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyCustomView_imageSrc:
                    imageBitmap = BitmapFactory.decodeResource(
                            getResources(), a.getResourceId(attr, 0));
                    break;
                case R.styleable.MyCustomView_imageAspectRatio:
                    imageAspectRatio = a.getFloat(attr, 1.0f);//默认长宽相等
                    break;
                case R.styleable.MyCustomView_imageAlpha:
                    imageAlpha = a.getFloat(attr, 1.0f);//默认不透明
                    if (imageAlpha > 1.0f) imageAlpha = 1.0f;
                    if (imageAlpha < 0.0f) imageAlpha = 0.0f;
                    break;
                case R.styleable.MyCustomView_imagePaddingLeft:
                    imagePaddingLeft = a.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.MyCustomView_imagePaddingTop:
                    imagePaddingTop = a.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.MyCustomView_imagePaddingRight:
                    imagePaddingRight = a.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.MyCustomView_imagePaddingBottom:
                    imagePaddingBottom = a.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.MyCustomView_imageScaleType:
                    imageScaleType = a.getInt(attr, 0);
                    break;
                case R.styleable.MyCustomView_xMessage:
                    xMessage = a.getString(attr);//传入点击是toast显示的文字
                    break;

            }
        }
        a.recycle();


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);



    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int desired = getPaddingLeft() + getPaddingRight() +
                    imagePaddingLeft + imagePaddingRight;
            desired += (imageBitmap != null) ? imageBitmap.getWidth() : 0;
            width = Math.max(MIN_SIZE, desired);
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(desired, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int rawWidth = width - getPaddingLeft() - getPaddingRight();
            int desired = (int) (getPaddingTop() + getPaddingBottom() + imageAspectRatio * rawWidth);


            height = Math.max(MIN_SIZE, desired);
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desired, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }



    @Override
    protected void onDraw(Canvas canvas) {



        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = mViewWidth - getPaddingRight();
        int bottom = mViewHeight - getPaddingBottom();

        paint.setAlpha(255);//设置透明度



        if (imageBitmap != null) {
            paint.setAlpha((int) (255 * imageAlpha));
            left += imagePaddingLeft;
            top += imagePaddingTop;
            right -= imagePaddingRight;
            bottom -= imagePaddingBottom;
            if (imageScaleType == SCALE_TYPE_FILLXY) {
                canvas.drawBitmap(imageBitmap, left, top, paint);


            } else if (imageScaleType == SCALE_TYPE_CENTER) {
                int bw = imageBitmap.getWidth();
                int bh = imageBitmap.getHeight();
                if (bw < right - left) {
                    int delta = (right - left - bw) / 2;
                    left += delta;
                    right -= delta;
                }
                if (bh < bottom - top) {
                    int delta = (bottom - top - bh) / 2;
                    top += delta;
                    bottom -= delta;
                }

                canvas.drawBitmap(imageBitmap, left, top, paint);
            }
        }
    }


    /**
     * 重写了点击
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Toast.makeText(getContext(), xMessage, 1000).show();

                break;
            default:
                break;
        }
        return true;
    }

    public void setImageBitmap(Bitmap bitmap) {
        imageBitmap = bitmap;
        requestLayout();
        invalidate();
    }


    /**
     * *** set get****
     */


    public Bitmap getImageBitmap() {
        return imageBitmap;
    }




    public float getImageAspectRatio() {
        return imageAspectRatio;
    }

    public void setImageAspectRatio(float imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }



    public void setImageAlpha(float imageAlpha) {
        this.imageAlpha = imageAlpha;
    }

    public int getImagePaddingLeft() {
        return imagePaddingLeft;
    }

    public void setImagePaddingLeft(int imagePaddingLeft) {
        this.imagePaddingLeft = imagePaddingLeft;
    }

    public int getImagePaddingTop() {
        return imagePaddingTop;
    }

    public void setImagePaddingTop(int imagePaddingTop) {
        this.imagePaddingTop = imagePaddingTop;
    }

    public int getImagePaddingRight() {
        return imagePaddingRight;
    }

    public void setImagePaddingRight(int imagePaddingRight) {
        this.imagePaddingRight = imagePaddingRight;
    }

    public int getImagePaddingBottom() {
        return imagePaddingBottom;
    }

    public void setImagePaddingBottom(int imagePaddingBottom) {
        this.imagePaddingBottom = imagePaddingBottom;
    }

    public int getImageScaleType() {
        return imageScaleType;
    }

    public void setImageScaleType(int imageScaleType) {
        this.imageScaleType = imageScaleType;
    }

    public String getxMessage() {
        return xMessage;
    }

    public void setxMessage(String xMessage) {
        this.xMessage = xMessage;
    }

}
