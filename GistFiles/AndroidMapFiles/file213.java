package treinamento.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
 
final class dimensionar_view 
{
    private static final float PHOTO_BORDER_WIDTH = 3.0f;
    private static final int PHOTO_BORDER_COLOR = Color.BLACK;//0xffffffff;
    private static final Paint sStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        sStrokePaint.setStrokeWidth(PHOTO_BORDER_WIDTH);
        sStrokePaint.setStyle(Paint.Style.STROKE);
        sStrokePaint.setColor(PHOTO_BORDER_COLOR);
    }

    public static View dimensionar(Activity atividade, View view, final int R_drawable_imagem, final int altura, final int largura)
    {
    	Bitmap bMap = BitmapFactory.decodeResource(atividade.getResources(), R_drawable_imagem);       
        Bitmap ok = dimensionar_view.scaleAndFrame(bMap, largura, altura);
        Drawable drawable = new BitmapDrawable(atividade.getResources(), ok);
        view.setBackgroundDrawable(drawable);
        return view;
    }
    
    private static Bitmap scaleAndFrame(Bitmap bitmap, int width, int height) 
    {
        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        final float scale = Math.min((float) width / (float) bitmapWidth, 
                (float) height / (float) bitmapHeight);

        final int scaledWidth = (int) (bitmapWidth * scale);
        final int scaledHeight = (int) (bitmapHeight * scale);

        final Bitmap decored = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        final Canvas canvas = new Canvas(decored);

        final int offset = (int) (PHOTO_BORDER_WIDTH / 2);
        sStrokePaint.setAntiAlias(false);
        canvas.drawRect(offset, offset, scaledWidth - offset - 1,
                scaledHeight - offset - 1, sStrokePaint);
        sStrokePaint.setAntiAlias(true);

        return decored;
    }
}