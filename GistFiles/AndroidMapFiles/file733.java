import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.util.Pair;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that allows creation of StateListDrawables containing Vector assets
 * Created by vinaysshenoy on 14/05/16.
 */
public final class StateListDrawableCompat {

    private static final String TAG = "StateListDrawableCompat";

    private static CompatDrawableCreator creator;

    static {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            creator = new CompatDrawableCreatorV21();
        } else {
            creator = new CompatDrawableCreatorV17();
        }
    }

    private StateListDrawableCompat() {

    }

    public static StateListDrawable create(@NonNull Context context, @DrawableRes int drawableResource, @Nullable Resources.Theme theme) {
        return creator.create(context, drawableResource, theme);
    }

    private interface CompatDrawableCreator {
        StateListDrawable create(@NonNull Context context, @DrawableRes int drawableResource, @Nullable Resources.Theme theme);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static final class CompatDrawableCreatorV21 implements CompatDrawableCreator {

        @Override
        public StateListDrawable create(@NonNull Context context, @DrawableRes int drawableResource, @Nullable Resources.Theme theme) {
            return (StateListDrawable) context.getResources().getDrawable(drawableResource, theme);
        }
    }

    private static final class CompatDrawableCreatorV17 implements CompatDrawableCreator {

        private final String NAMESPACE = "http://schemas.android.com/apk/res/android";

        private final SimpleArrayMap<String, Integer> attrNameValueMap;

        private final SimpleArrayMap<Integer, StateListDrawable> unthemedDrawableCache;

        private final SimpleArrayMap<Pair<Integer, Resources.Theme>, StateListDrawable> themedDrawableCache;

        public CompatDrawableCreatorV17() {
            attrNameValueMap = new SimpleArrayMap<>((int) (9 * 1.33F));
            attrNameValueMap.put("state_pressed", android.R.attr.state_pressed);
            attrNameValueMap.put("state_hovered", android.R.attr.state_hovered);
            attrNameValueMap.put("state_focused", android.R.attr.state_focused);
            attrNameValueMap.put("state_selected", android.R.attr.state_selected);
            attrNameValueMap.put("state_checkable", android.R.attr.state_checkable);
            attrNameValueMap.put("state_checked", android.R.attr.state_checked);
            attrNameValueMap.put("state_enabled", android.R.attr.state_enabled);
            attrNameValueMap.put("state_activated", android.R.attr.state_activated);
            attrNameValueMap.put("state_window_focused", android.R.attr.state_window_focused);

            unthemedDrawableCache = new SimpleArrayMap<>((int) (10 * 1.33F));
            themedDrawableCache = new SimpleArrayMap<>((int) (10 * 1.33F));
        }

        private void addStateDrawable(StateListDrawable stateListDrawable, XmlResourceParser xmlResourceParser, Resources resources, Resources.Theme theme) {

            final AttributeSet attributeSet = Xml.asAttributeSet(xmlResourceParser);

            final int attributeCount = attributeSet.getAttributeCount();

            if (attributeCount > 0) {

                final int drawableResourceValue = attributeSet.getAttributeResourceValue(NAMESPACE, "drawable", 0);
                if (drawableResourceValue != 0) {
                    final VectorDrawableCompat drawable = VectorDrawableCompat.create(resources, drawableResourceValue, theme);

                    if (drawable == null) {
                        throw new RuntimeException("Could not read vector drawable: " + drawableResourceValue);
                    }
                    if (attributeCount == 1) {
                        stateListDrawable.addState(StateSet.WILD_CARD, drawable);
                    } else {

                        final List<Integer> attrs = new ArrayList<>(9);
                        boolean value;
                        for (int i = 0; i < attrNameValueMap.size(); i++) {
                            value = attributeSet.getAttributeBooleanValue(NAMESPACE, attrNameValueMap.keyAt(i), false);
                            if (value) {
                                attrs.add(attrNameValueMap.valueAt(i));
                            }
                        }
                        if (!attrs.isEmpty()) {

                            final int[] attrsArray = new int[attrs.size()];
                            for (int i = 0; i < attrs.size(); i++) {
                                attrsArray[i] = attrs.get(i);
                            }
                            stateListDrawable.addState(attrsArray, drawable);
                        }
                    }
                }
            }
        }

        @MainThread
        @Override
        public StateListDrawable create(@NonNull Context context, @DrawableRes int drawableResource, @Nullable Resources.Theme theme) {

            if (Looper.getMainLooper() != Looper.myLooper()) {
                throw new RuntimeException("Must be called on Main Thread");
            }

            //Check if the drawable is already created and cached
            StateListDrawable stateListDrawable;

            stateListDrawable = resolveCachedDrawable(drawableResource, theme);
            if (stateListDrawable != null) {
                Log.d(TAG, "Found cached drawable");
                return stateListDrawable;
            }
            int nextToken;
            String tagName;

            final XmlResourceParser xmlResourceParser = context.getResources().getXml(drawableResource);

            try {
                while ((nextToken = xmlResourceParser.next()) != XmlPullParser.END_DOCUMENT) {

                    switch (nextToken) {

                        case XmlPullParser.START_DOCUMENT: {
                            stateListDrawable = new StateListDrawable();
                            break;
                        }

                        case XmlPullParser.START_TAG: {
                            tagName = xmlResourceParser.getName();
                            if ("selector".equals(tagName)) {
                                setDrawableAttributes(stateListDrawable, xmlResourceParser);
                            } else if ("item".equals(tagName)) {
                                addStateDrawable(stateListDrawable, xmlResourceParser, context.getResources(), theme);
                            }
                            break;
                        }

                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                xmlResourceParser.close();
            }

            if (stateListDrawable != null) {
                //Cache for later use
                Log.d(TAG, "Adding created drawable to cache");
                if (theme == null) {
                    unthemedDrawableCache.put(drawableResource, stateListDrawable);
                } else {
                    themedDrawableCache.put(Pair.create(drawableResource, theme), stateListDrawable);
                }
            }
            return stateListDrawable;
        }

        private void setDrawableAttributes(StateListDrawable stateListDrawable, XmlResourceParser xmlResourceParser) {

            final AttributeSet attributeSet = Xml.asAttributeSet(xmlResourceParser);

            final DrawableContainer.DrawableContainerState state = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();

            final boolean constantSize = attributeSet.getAttributeBooleanValue(NAMESPACE, "constantSize", true);
            final boolean variablePadding = attributeSet.getAttributeBooleanValue(NAMESPACE, "variablePadding", false);
            final boolean dither = attributeSet.getAttributeBooleanValue(NAMESPACE, "dither", true);
            final boolean visible = attributeSet.getAttributeBooleanValue(NAMESPACE, "visible", true);

            state.setConstantSize(constantSize);
            state.setVariablePadding(variablePadding);
            stateListDrawable.setDither(dither);
            stateListDrawable.setVisible(visible, false);

        }

        @Nullable
        private StateListDrawable resolveCachedDrawable(int drawableResource, @Nullable Resources.Theme theme) {

            if (theme == null) {
                return unthemedDrawableCache.get(drawableResource);
            } else {
                return themedDrawableCache.get(Pair.create(drawableResource, theme));
            }
        }

    }

}
