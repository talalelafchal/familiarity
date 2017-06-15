package gturedi.gist;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ContextUtil {

    private final Context ctx;

    public ContextUtil(Context ctx) {
        this.ctx = ctx;
    }

    public void showToast(@StringRes int resId) {
        showToast(ctx.getString(resId));
    }

    public void showToast(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    // requires permission: ACCESS_NETWORK_STATE
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    // requires permission: ACCESS_FINE_LOCATION (for pre-lollipop)
    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public Point getScreenDimensions() {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return  (int) (dp * (metrics.densityDpi / 160f));
    }

    public int convertPixelsToDp(int px) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) (px / (metrics.densityDpi / 160f));
    }

    public void hideKeyboard(View target) {
        try {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(target.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDownloadManager(String url) {
        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        dm.enqueue(request);
    }

    public String str(@StringRes int id) {
        return ctx.getString(id);
    }

    public int color(@ColorRes int id) {
        return ctx.getResources().getColor(id);
    }

}