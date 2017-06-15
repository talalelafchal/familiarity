import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by chRyNaN on 2/26/2016.
 */
public class ViewUtils {

    public static float toPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int toPixelInt(float dp, Context context){
        return (int) toPixel(dp, context);
    }

    public static float toDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float toDpInt(float px, Context context){
        return (int) toDp(px, context);
    }

    public static Point getScreenSize(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static float getScreenWidth(Context context){
        Point p = getScreenSize(context);
        return p.x;
    }

    public static float getScreenHeight(Context context){
        Point p = getScreenSize(context);
        return p.y;
    }

    public int getScreenOrientation(Activity activity){
        return activity.getResources().getConfiguration().orientation;
    }

    public static Point getCenterScreenPosition(Context context){
        Point p = getScreenSize(context);
        p.x = p.x / 2;
        p.y = p.y / 2;
        return p;
    }

    public static float getCenterXScreenPosition(Context context){
        Point p = getCenterScreenPosition(context);
        return p.x;
    }

    public static float getCenterYScreenPosition(Context context){
        Point p = getCenterScreenPosition(context);
        return p.y;
    }

    public static float getCenterPosition(float start, float end){
        return (end - start) / 2;
    }

    //http://stackoverflow.com/a/10600736/1478764
    public static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getViewAsBitmap(View view){
        Bitmap bitmap = null;
        if(view.isDrawingCacheEnabled()){
            view.buildDrawingCache();
            bitmap = view.getDrawingCache();
        }else {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            bitmap = view.getDrawingCache();
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
        }
        return bitmap;
    }

    public static Rect getTextBounds(String text, Paint paint){
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    public static Rect getDefaultTextBounds(String text, Context context){
        Paint paint = new Paint();
        paint.setTextSize(ViewUtils.toPixel(16, context));
        return getTextBounds(text, paint);
    }

    public static int getTextHeight(String text, Paint paint){
        Rect rect = getTextBounds(text, paint);
        return rect.height();
    }

    public static int getDefaultTextHeight(String text, Context context){
        Rect rect = getDefaultTextBounds(text, context);
        return rect.height();
    }

    public static int getTextWidth(String text, Paint paint){
        Rect rect = getTextBounds(text, paint);
        return rect.width();
    }

    public static int getDefaultTextWidth(String text, Context context){
        Rect rect = getDefaultTextBounds(text, context);
        return (rect.left + rect.right) / 2;
    }

    //This method returns the best preview size that can fit in the specified space
    //Code taken from here:
    //https://github.com/commonsguy/cw-advandroid/blob/master/Camera/Preview/src/com/commonsware/android/camera/PreviewDemo.java
    @SuppressWarnings("deprecation")
    public static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result=null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }

    //TODO gives close results but not sure why it doesn't provide the largest width
    @SuppressWarnings("deprecation")
    public static Camera.Size getFullScreenPreviewSize(Context context, Camera.Parameters params){
        int width = (int) getScreenWidth(context);
        int height = (int) getScreenHeight(context);
        return getBestPreviewSize(((width > height) ? width : height), ((width > height) ? height : width), params);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void circularReveal(View view, float x, float y, long duration, TimeInterpolator interpolator){
        Animator anim = getCircularRevealAnimation(view, x, y, duration, interpolator);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getCircularRevealAnimation(View view, float x, float y, long duration, TimeInterpolator interpolator){
        if(view == null){
            throw new IllegalArgumentException("View parameter in getCircularReveal method of ViewUtils class must not be null.");
        }
        x = (x < 0) ? 0 : x;
        y = (y < 0) ? 0 : y;
        x = (x > view.getWidth()) ? view.getWidth() : x;
        y = (y > view.getHeight()) ? view.getHeight() : y;
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) x, (int) y, 0, finalRadius);
        if(interpolator == null){
            interpolator = new DecelerateInterpolator(2f);
        }
        duration = (duration < 0) ? 0 : duration;
        anim.setInterpolator(interpolator);
        anim.setDuration(duration);
        return anim;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void circularUnreveal(View view, float x, float y, long duration, TimeInterpolator interpolator){
        Animator anim = getCircularUnrevealAnimation(view, x, y, duration, interpolator);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getCircularUnrevealAnimation(View view, float x, float y, long duration, TimeInterpolator interpolator){
        if(view == null){
            throw new IllegalArgumentException("View parameter in getCircularUnreveal method of ViewUtils class must not be null.");
        }
        x = (x < 0) ? 0 : x;
        y = (y < 0) ? 0 : y;
        x = (x > view.getWidth()) ? view.getWidth() : x;
        y = (y > view.getHeight()) ? view.getHeight() : y;
        Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) x, (int) y,
                (float) Math.hypot(view.getWidth(), view.getHeight()), 0);
        if(interpolator == null){
            interpolator = new AccelerateInterpolator(0.5f);
        }
        duration = (duration < 0) ? 0 : duration;
        anim.setInterpolator(interpolator);
        anim.setDuration(duration);
        return anim;
    }

}
