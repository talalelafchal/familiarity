/*
 * Copyright (c) 2015 Tom Wijgers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Except as contained in this notice, the name(s) of the above copyright holders shall not be used
 * in advertising or otherwise to promote the sale, use or other dealings in this Software without
 * prior written authorization.
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sss.utilities.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sss.utilities.AppCompatUtils;
import com.sss.utilities.R;
import com.sss.utilities.TypefaceManager;

/**
 * A circular progress bar, with rounded caps, and a gradient
 *
 * @author Tom Wijgers
 */

public class ProgressBarRoundedRing extends ProgressBar
{
    private static int sCount;
    private static final String TAG = ProgressBarRoundedRing.class.getName();
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String iTAG;
    private int iCount;

    private int iStartColor = Color.WHITE;
    private int iEndColor = Color.BLACK;
    private int iBackgroundColour = Color.TRANSPARENT;
    private int iTextColor = Color.WHITE;

    private boolean iShowProgress = false;
    private boolean iShowPercentage = false;
    private String iText = null;
    private String iAdditionalText = null;
    private String iTextTypeface = null;
    //private int iFontStyle = Typeface.NORMAL;

    private long iStart = -1;

    private Typeface mTypeface = null;

    private Paint mPaint;
    private SweepGradient mGradient;
    private TextPaint mTextPaint;
    private StaticLayout mTextLayout;
    private StaticLayout mAddTextLayout;

    private boolean resized = true;
    private int oldWidth;
    private int width;
    private float stroke;

    // Members for performance reasons.
    @SuppressWarnings("FieldCanBeLocal")
    private int period;
    @SuppressWarnings("FieldCanBeLocal")
    private float startAngle;
    @SuppressWarnings("FieldCanBeLocal")
    private float size;
    @SuppressWarnings("FieldCanBeLocal")
    private float sweepAngle;
    @SuppressWarnings("FieldCanBeLocal")
    private float angle;

    public ProgressBarRoundedRing(@NonNull Context context)
    {
        this(context, null);
    }

    public ProgressBarRoundedRing(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        iCount = sCount++;
        iTAG = TAG + "$" + iCount;
        TypefaceManager.init(context);
        loadAttrs(attrs, defStyleAttr, defStyleRes);
        initPaints();
    }

    private void loadAttrs(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        if (attrs == null)
            return;

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProgressBarRoundedRing,
                defStyleAttr,
                defStyleRes
        );

        TypedArray ta = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Typeface,
                defStyleAttr,
                defStyleRes
        );

        try
        {
            iStartColor = a.getColor(R.styleable.ProgressBarRoundedRing_colorStart, iStartColor);
            iEndColor = a.getColor(R.styleable.ProgressBarRoundedRing_colorEnd, iEndColor);
            iBackgroundColour = a.getColor(R.styleable.ProgressBarRoundedRing_colorBackground, iBackgroundColour);
            iTextColor = a.getColor(R.styleable.ProgressBarRoundedRing_colorText, iTextColor);
            iShowProgress = a.getBoolean(R.styleable.ProgressBarRoundedRing_showProgress, iShowProgress);
            iShowPercentage = a.getBoolean(R.styleable.ProgressBarRoundedRing_showProgressAsPercentage, iShowPercentage);
            iAdditionalText = a.getString(R.styleable.ProgressBarRoundedRing_additionalText);
            iTextTypeface = ta.getString(R.styleable.Typeface_fontAssetName);
        }
        finally
        {
            a.recycle();
            ta.recycle();
        }
    }

    private void initPaints()
    {
        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(iTextColor);
        mTextPaint.setStyle(Style.FILL);
        mTextPaint.setAntiAlias(true);
        if(mTypeface == null && iTextTypeface != null)
            mTypeface = TypefaceManager.getTypeface(iTextTypeface);

        if(mTypeface != null)
            mTextPaint.setTypeface(mTypeface);
    }

    public ProgressBarRoundedRing(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressBarRoundedRing(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getCount()
    {
        return iCount;
    }

    /**
     * @param color the starting colour of the gradient
     */
    public void setStartColor(@ColorInt int color)
    {
        iStartColor = color;

        updateGradient();
    }

    private void updateGradient()
    {
        if (isIndeterminate())
        {
            int[] colours = {iStartColor, iEndColor, iStartColor, iEndColor, iStartColor};
            float[] position = {0f, 0.25f, 0.5f, 0.75f, 1f};

            mGradient = new SweepGradient(width / 2, width / 2, colours, position);
        }
        else
        {
            int[] colours = {iStartColor, iStartColor, iEndColor, iStartColor};
            float[] position = {0f, 0.01f, 0.01f, 1f};

            mGradient = new SweepGradient(width / 2, width / 2, colours, position);
        }

        postInvalidate();
    }

    /**
     * @param color the starting colour of the gradient
     */
    public void setStartColorResource(@ColorRes int color)
    {
        iStartColor = AppCompatUtils.getColor(getResources(), color);

        updateGradient();
    }

    /**
     * @param color the ending colour of the gradient
     */
    public void setEndColor(@ColorInt int color)
    {
        iEndColor = color;

        updateGradient();
    }

    /**
     * @param color the ending colour of the gradient
     */
    public void setEndColorResource(@ColorRes int color)
    {
        iEndColor = AppCompatUtils.getColor(getResources(), color);

        updateGradient();
    }

    /**
     * @param color the background colour of the progress ring
     */
    public void setBackgroundColor(@ColorInt int color)
    {
        iBackgroundColour = color;

        postInvalidate();
    }

    /**
     * @param color the background colour of the progress ring
     */
    public void setBackgroundColorResource(@ColorRes int color)
    {
        iBackgroundColour = AppCompatUtils.getColor(getResources(), color);

        postInvalidate();
    }

    /**
     * @param typeFace the typeface for the progress text
     */
    public void setTypeface(Typeface typeFace)
    {
        mTypeface = typeFace;

        mTextPaint.setTypeface(typeFace);

        postInvalidate();
    }

    /**
     * @param color the colour of the progress text
     */
    public void setTextColor(@ColorInt int color)
    {
        iTextColor = color;

        mTextPaint.setColor(iTextColor);

        postInvalidate();
    }

    /**
     * @param color the colour of the progress text
     */
    public void setTextColorResource(@ColorRes int color)
    {
        iTextColor = AppCompatUtils.getColor(getResources(), color);

        mTextPaint.setColor(iTextColor);

        postInvalidate();
    }

    /**
     * @param showProgress whether or not to show the progress as text in the middle of the progress ring
     */
    public void setShowProgress(boolean showProgress)
    {
        iShowProgress = showProgress;

        postInvalidate();
    }

    /**
     * @param showProgressPercentage show the progress as a percentage
     */
    public void setShowProgressPercentage(boolean showProgressPercentage)
    {
        iShowPercentage = showProgressPercentage;

        postInvalidate();
    }

    /**
     * The text to show in the middle of the progress ring. This is overridden if showProgress or
     * showProgressPercentage is set.
     *
     * @param text the text to show in the middle of the progress ring.
     */
    public void setText(String text)
    {
        iText = text;

        mTextLayout = null;

        postInvalidate();
    }

    /**
     * @param text additional text to show in a small font below the main text
     */
    public void setAdditionalText(String text)
    {
        iAdditionalText = text;

        mAddTextLayout = null;

        postInvalidate();
    }

    private void drawIndeterminate(@NonNull Canvas c)
    {
        if (iStart == -1)
            iStart = System.currentTimeMillis();

        period = (int) ((System.currentTimeMillis() - iStart) % 10800) / 30;

        startAngle = period * 3;
        size = (float) Math.abs(Math.sin(Math.toRadians(period))) * 45 + 30;
        sweepAngle = size * 2;
        startAngle -= size;

        oldWidth = width;
        width = c.getWidth() > c.getHeight() ? c.getHeight() : c.getWidth();
        if (width != oldWidth || width == 0)
            resized();

        if (iBackgroundColour == Color.TRANSPARENT)
            mPaint.setColor(Color.BLACK);
        else
            mPaint.setColor(iBackgroundColour);

        c.save();
        c.rotate(period * -1, width / 2, width / 2);

        if (iBackgroundColour != Color.TRANSPARENT)
            c.drawArc(new RectF(stroke, stroke, width - stroke, width - stroke), 0, 360, false, mPaint);
        mPaint.setShader(mGradient);
        c.drawArc(new RectF(stroke, stroke, width - stroke, width - stroke), startAngle, sweepAngle, false, mPaint);
        mPaint.setShader(null);
        c.restore();

        if (iAdditionalText != null)
        {
            int textSize = width / 12;

            mTextPaint.setTextSize(textSize);
            mTextPaint.setFakeBoldText(false);

            if (mAddTextLayout == null || resized)
                mAddTextLayout = new StaticLayout(iAdditionalText, mTextPaint, width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            c.save();
            c.translate(0, (width - mAddTextLayout.getHeight()) / 2);
            mAddTextLayout.draw(c);
            c.restore();
        }

        resized = false;

        invalidate();
    }

    private void drawDeterminate(@NonNull Canvas c)
    {
        oldWidth = width;
        width = c.getWidth() > c.getHeight() ? c.getHeight() : c.getWidth();
        if (width != oldWidth)
            resized();

        angle = 360 * (getProgress() / (float) getMax()) * .98f;

        /* Draw background in blue for testing
        paint.setColor(Color.BLUE);
        paint.setStyle(Style.FILL);
        c.drawRect(0,0,width,width,paint);
        //*/
        if (iBackgroundColour == Color.TRANSPARENT)
            mPaint.setColor(Color.BLACK);
        else
            mPaint.setColor(iBackgroundColour);

        c.save();
        c.rotate(-90, width / 2, width / 2);

        if (iBackgroundColour != Color.TRANSPARENT)
            c.drawArc(new RectF(stroke, stroke, width - stroke, width - stroke), 0, 360, false, mPaint);
        mPaint.setShader(mGradient);
        c.drawArc(new RectF(stroke, stroke, width - stroke, width - stroke), 360 - angle, angle, false, mPaint);
        mPaint.setShader(null);

        c.restore();

        if (iShowPercentage && getMax() > 0)
        {
            iText = String.valueOf((getProgress() * 100) / getMax()) + "%";
        }
        else if (iShowProgress)
            iText = String.valueOf(getProgress());

        if (iText != null)
        {
            int textSize = width / 3;

            mTextPaint.setTextSize(textSize);
            mTextPaint.setFakeBoldText(true);

            if (mTextLayout == null || resized)
                mTextLayout = new StaticLayout(iText, mTextPaint, width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            c.save();

            if (iAdditionalText == null)
                c.translate(0, (width - mTextLayout.getHeight()) / 2);
            else
            {
                int addSize = width / 12;
                c.translate(0, (width - (mTextLayout.getHeight() + addSize)) / 2);
            }
            mTextLayout.draw(c);


            if (iAdditionalText != null)
            {
                c.translate(0, textSize);

                textSize = width / 12;

                mTextPaint.setTextSize(textSize);
                mTextPaint.setFakeBoldText(false);

                if (mAddTextLayout == null || resized)
                    mAddTextLayout = new StaticLayout(iAdditionalText, mTextPaint, width, Alignment.ALIGN_CENTER, 1f, 0f, false);
                mAddTextLayout.draw(c);
            }

            c.restore();
        }
    }

    /**
     * Change the indeterminate mode for this progress bar. In indeterminate
     * mode, the progress is ignored and the progress bar shows an infinite
     * animation instead.
     */
    @Override
    public void setIndeterminate(boolean indeterminate)
    {
        super.setIndeterminate(indeterminate);

        updateGradient();
    }

    @Override
    public void onDraw(@NonNull Canvas c)
    {
        if (isIndeterminate())
        {
            drawIndeterminate(c);
        }
        else
        {
            drawDeterminate(c);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //Get the width measurement
        int widthSize = MeasureUtils.getMeasurement(widthMeasureSpec, 100000);

        //Get the height measurement
        int heightSize = MeasureUtils.getMeasurement(heightMeasureSpec, 100000);

        // Force square
        width = widthSize > heightSize ? heightSize : widthSize;

        //noinspection SuspiciousNameCombination
        setMeasuredDimension(width, width);

        resized();
    }

    private void resized()
    {
        resized = true;
        stroke = width / 18;
        mPaint.setStrokeWidth(stroke);
        updateGradient();
    }
}