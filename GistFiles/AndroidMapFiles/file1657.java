import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pro.alex_zaitsev.currency.R;

/**
 * @author A.Zaitsev
 */
public class MaterialShapeGenerator {

    private static final int[] MATERIAL_COLOR_IDS = new int[]{
            R.color.material_red,
            R.color.material_pink,
            R.color.material_purple,
            R.color.material_deep_purple,
            R.color.material_indigo,
            R.color.material_blue,
            R.color.material_light_blue,
            R.color.material_cyan,
            R.color.material_teal,
            R.color.material_green,
            R.color.material_light_green,
            R.color.material_lime,
            R.color.material_yellow,
            R.color.material_amber,
            R.color.material_orange,
            R.color.material_deep_orange,
            R.color.material_brown,
            R.color.material_grey,
            R.color.material_blue_grey
    };

    private int[] materialColors;
    private Random random;
    private Map<Integer, Drawable> drawableCache = new HashMap<>();

    public MaterialShapeGenerator(Context context) {
        materialColors = new int[MATERIAL_COLOR_IDS.length];
        for (int i = 0; i < MATERIAL_COLOR_IDS.length; i++) {
            materialColors[i] = context.getResources().getColor(MATERIAL_COLOR_IDS[i]);
        }
        random = new Random(System.currentTimeMillis());
    }

    private int generateRandomMaterialColor() {
        int index = random.nextInt(materialColors.length);
        return materialColors[index];
    }

    public Drawable generateCircleDrawable(int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    public Drawable generateAndCacheCircleDrawable() {
        int materialColor = generateRandomMaterialColor();
        Drawable cachedDrawable = drawableCache.get(materialColor);
        if (cachedDrawable == null) {
            drawableCache.put(materialColor, cachedDrawable = generateCircleDrawable(materialColor));
        }
        return cachedDrawable;
    }
}
