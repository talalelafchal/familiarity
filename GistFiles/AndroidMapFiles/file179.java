package app.tidy.tidy.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by jpm on 13-02-2017.
 */

public final class EmojiUtil {
    
    private static List<String> emojiList;

     static{

         emojiList = Arrays.asList(
                 "kuala"
         );



    }
    public static final BitmapDescriptor loadBitmap (Context context, String name) {
        AssetManager am = context.getAssets();
        if (!emojiList.contains(name)) return null;
        try {
            return BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(am.open(name + ".png")));
        } catch (IOException ignored) { //Due to performance reasons
        }
        return null;
    }

    public static String getRandom(){
        Random r = new Random();
        return emojiList.get(r.nextInt(emojiList.size()));
    }


}
