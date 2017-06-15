package ru.kurganec.vk.messenger.utils.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.flurry.android.FlurryAgent;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import ru.kurganec.vk.messenger.R;

import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 22.09.12
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class Emoji {
    private static final ImmutableMap<String, Integer> smiles;

    private static final String matchRegexp;
    private static String replacement = "<img src=\"$1\"/>";

    static {
        HashMap<String, Integer> tmp = new HashMap<String, Integer>();
        tmp.put("\uD83D\uDE0A", R.drawable.blush);
        tmp.put("\uD83D\uDE03", R.drawable.smile);//TODO this one does not look right // good luck
        tmp.put("\ud83d\ude09", R.drawable.wink);
        tmp.put("\ud83d\ude06", R.drawable.laughing);
        tmp.put("\ud83d\ude1c", R.drawable.wink2);
        tmp.put("\ud83d\ude0b", R.drawable.yum);
        tmp.put("\ud83d\ude0d", R.drawable.heart_eyes);
        tmp.put("\ud83d\ude0e", R.drawable.sunglasses);

        tmp.put("\ud83d\ude12", R.drawable.disappointed);
        tmp.put("\ud83d\ude0f", R.drawable.smirk);
        tmp.put("\ud83d\ude14", R.drawable.pensive);
        tmp.put("\ud83d\ude22", R.drawable.cry);
        tmp.put("\ud83d\ude2d", R.drawable.sob);
        tmp.put("\ud83d\ude29", R.drawable.weary);
        tmp.put("\ud83d\ude28", R.drawable.astonished);
        tmp.put("\ud83d\ude10", R.drawable.neutral_face);

        tmp.put("\ud83d\ude0c", R.drawable.satisfied);
        tmp.put("\ud83d\ude20", R.drawable.angry);
        tmp.put("\ud83d\ude21", R.drawable.rage);
        tmp.put("\ud83d\ude07", R.drawable.innocent);
        tmp.put("\ud83d\ude30", R.drawable.cold_sweat);
        tmp.put("\ud83d\ude32", R.drawable.dizzy_face);
        tmp.put("\ud83d\ude33", R.drawable.flushed);
        tmp.put("\ud83d\ude37", R.drawable.mask);


        tmp.put("\ud83d\ude1a", R.drawable.kissing_face);
        tmp.put("\ud83d\ude08", R.drawable.smiling_imp);
        tmp.put("\u2764", R.drawable.heart);
        tmp.put("\ud83d\udc4d", R.drawable.yes);
        tmp.put("\ud83d\udc4e", R.drawable.no);
        tmp.put("\u261d", R.drawable.point_up);
        tmp.put("\u270c", R.drawable.v);
        tmp.put("\uD83D\uDC4C", R.drawable.ok_hand);
        smiles = ImmutableMap.copyOf(tmp);

        String joined = Joiner.on('|').join(smiles.keySet());
        matchRegexp = "(" + joined + ")";
    }


    public static Spanned replace(final Context c, String str, final int size) {
        long time = System.currentTimeMillis();
        str = str.replaceAll(matchRegexp, replacement);
        Log.d("SMILES", (System.currentTimeMillis() - time) + " replaced");
        return Html.fromHtml(str, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                FlurryAgent.logEvent("emoji");
                Drawable d = c.getResources().getDrawable(smiles.get(source));
                d.setBounds(0, 0, size, size);
                return d;
            }
        },
                null);
    }

   
}
