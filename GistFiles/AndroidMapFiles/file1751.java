package test.fragment.list;

import java.lang.ref.SoftReference;
import java.util.EnumMap;

import test.fragment.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageFactory {

    public enum Type {
        LOADING,
        DROID;
    }

    private static EnumMap<Type, SoftReference<Bitmap>> map;
    static {
        map = new EnumMap<Type, SoftReference<Bitmap>>(Type.class);
    }

    public static Bitmap getBitmap(Context context, Type type) {
        switch (type) {
        case LOADING:
            return getLoadingBitmap(context);
        case DROID:
            return getDroidBitmap(context);
        }
        throw new RuntimeException("illegal argument type:" + type);
    }

    private static Bitmap getLoadingBitmap(Context context) {
        SoftReference<Bitmap> loading = map.get(Type.LOADING);
        if (loading == null || loading.get().isRecycled()) {
            loading = new SoftReference<Bitmap>(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
        }
        return loading.get();
    }

    private static Bitmap getDroidBitmap(Context context) {
        SoftReference<Bitmap> droid = map.get(Type.DROID);
        if (droid == null || droid.get().isRecycled()) {
            droid = new SoftReference<Bitmap>(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        }
        return droid.get();
    }
}
