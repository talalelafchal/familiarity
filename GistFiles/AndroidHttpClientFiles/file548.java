package com.stonete.qrtoken.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewUtil extends TextView {
    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 18;
// --Commented out by Inspection START (2015/3/4 11:58):
//    /**
//     * 距离右面的大小
//     */
//    private static final int TEXT_right = 15;
// --Commented out by Inspection STOP (2015/3/4 11:58)
// --Commented out by Inspection START (2015/3/4 11:58):
//    /**
//     * textview 的行间距
//     */
//    private static final int LINE_SPAC = 15;
// --Commented out by Inspection STOP (2015/3/4 11:58)
    /**
     * 手机的屏幕密度
     */
    private static float density;
    private final String namespace = "http://www.angellecho.com/";
    private String text;
    private float textSize;
    private float paddingLeft;
    private float paddingRight;
    private float marginLeft;
    private float marginRight;
    // --Commented out by Inspection (2015/3/4 11:58):private float linespac;
    private int textColor;
    private Paint paint1 = new Paint();
    private float textShowWidth;
// --Commented out by Inspection START (2015/3/4 11:58):
//    /**
//     *
//     */
//    private int ScreenRate;
// --Commented out by Inspection STOP (2015/3/4 11:58)

    public TextViewUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        // 将像素转换成dp

        text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
        textSize = attrs.getAttributeIntValue(namespace, "textSize", TEXT_SIZE);
        textColor = attrs.getAttributeIntValue(namespace, "textColor", Color.GRAY);
        paddingLeft = attrs.getAttributeIntValue(namespace, "paddingLeft", 0) * density;
        paddingRight = attrs.getAttributeIntValue(namespace, "paddingRight", 80) * density;
        marginLeft = attrs.getAttributeIntValue(namespace, "marginLeft", 0) * density;
        marginRight = attrs.getAttributeIntValue(namespace, "marginRight", 0) * density;
        paint1.setTextSize(textSize * density);
        paint1.setColor(textColor);
        paint1.setAntiAlias(true);
        textShowWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth() - paddingLeft - paddingRight - marginLeft - marginRight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineCount = 0;
        text = this.getText().toString();//.replaceAll("\n", "\r\n");
        if (text == null) return;
        char[] textCharArray = text.toCharArray();
        // 已绘的宽度
        float drawedWidth = 0;
        float charWidth;
        for (int i = 0; i < textCharArray.length; i++) {
            charWidth = paint1.measureText(textCharArray, i, 1);

            if (textCharArray[i] == '\n') {
                lineCount++;
                drawedWidth = 0;
                continue;
            }
            if (textShowWidth - drawedWidth < charWidth) {
                lineCount++;
                drawedWidth = 0;
            }
            canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth,
                    (lineCount + 1) * textSize * density, paint1);
            drawedWidth += charWidth;
        }
        setHeight((lineCount + 1) * (int) (textSize * density) + 5);

//      FontMetrics fm = paint1.getFontMetrics();
//      float baseline = fm.descent - fm.ascent;
//        float x = 0;
//        float y =  baseline;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
//            canvas.drawText(text, x, y, paint1);  //坐标以控件左上角为原点
//            y += baseline + fm.leading; //添加字体行间距
    }
}
