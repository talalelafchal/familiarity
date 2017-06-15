package org.healthonnet.phonegap.plugins;

import java.util.EnumMap;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.kaahe.kaaheApp.UtilLogger;

import android.app.Activity;
import android.util.SparseArray;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;


/**
 * Cordova Plugin for adjusting the font size, between 50% and 200%.
 * 
 * Shows up in the JavaScript as window.plugins.fontSizeAdjust
 * 
 * @author nolan
 *
 */
@SuppressWarnings("deprecation")
public class FontSizeAdjustPlugin extends CordovaPlugin {

    
    private static final String ACTION_INCREASE = "increase";
    private static final String ACTION_DECREASE = "decrease";
    private static final String ACTION_SET = "set";
    

    private static final EnumMap<TextSize, String> TEXT_SIZE_VALUES = new EnumMap<TextSize, String>(TextSize.class);
    static {
        TEXT_SIZE_VALUES.put(TextSize.SMALLEST, "50");
        TEXT_SIZE_VALUES.put(TextSize.SMALLER, "75");
        TEXT_SIZE_VALUES.put(TextSize.NORMAL, "100");
        TEXT_SIZE_VALUES.put(TextSize.LARGER, "150");
        TEXT_SIZE_VALUES.put(TextSize.LARGEST, "200");
    }
    private static final SparseArray<TextSize> TEXT_SIZE_REVERSE_LOOKUP = new SparseArray<TextSize>();
    static {
        TEXT_SIZE_REVERSE_LOOKUP.put(50, TextSize.SMALLEST);
        TEXT_SIZE_REVERSE_LOOKUP.put(75, TextSize.SMALLER);
        TEXT_SIZE_REVERSE_LOOKUP.put(100, TextSize.NORMAL);
        TEXT_SIZE_REVERSE_LOOKUP.put(150, TextSize.LARGER);
        TEXT_SIZE_REVERSE_LOOKUP.put(200, TextSize.LARGEST);
    }
    
    private static UtilLogger log = new UtilLogger(FontSizeAdjustPlugin.class);
    
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        
        log.d("action is %s and jsonarray is %s and callbackId is %s", action, data, callbackContext);
        
        DroidGap droidGap = (DroidGap)super.cordova.getActivity();
        
        // Why 100? See DroidGap.java:
        // https://github.com/apache/incubator-cordova-android/blob/f93c438067a03a181069baf7228b74659bfc1bf7
        // /framework/src/org/apache/cordova/DroidGap.java#L322
        WebView webView = (WebView) droidGap.findViewById(100);
        TextSize newTextSize = null;
        
        if (ACTION_SET.equals(action)) {
            // parse the input value now so that we can return false if necessary
            try {
                Object newValue = data.get(0);
                Integer newValueAsInt;
                if (newValue instanceof String) {
                    newValueAsInt = Integer.parseInt((String)newValue);
                } else {
                    newValueAsInt = (Integer) newValue;
                }
                newTextSize = TEXT_SIZE_REVERSE_LOOKUP.get(newValueAsInt);
            } catch (JSONException e) {
                log.e(e, "bad input to 'set'; should be one of: 50, 75, 100, 150, 200");
            } catch (NumberFormatException e) {
                log.e(e, "bad input to 'set'; should be one of: 50, 75, 100, 150, 200");
            } catch (NullPointerException e) {
                log.e(e, "bad input to 'set'; should be one of: 50, 75, 100, 150, 200");
            } catch (ClassCastException e) {
                log.e(e, "bad input to 'set'; should be one of: 50, 75, 100, 150, 200");
            }
            
            if (newTextSize == null) {
                log.e("error parsing the input integer to set()");
                return false;
            }
        } else if (!(ACTION_INCREASE.equals(action) || ACTION_DECREASE.equals(action))) {
            log.e("Unknown action %s", action);
            return false;
        }
        
        adjustOnUIThread(droidGap, webView, action, newTextSize, callbackContext);
        
        return true;
    }
    
    private void adjustOnUIThread(final Activity activity, final WebView webView, final String action, 
            final TextSize newTextSize, final CallbackContext callbackContext) {
        
        // run on UI thread to avoid Android logging a lot of warnings
        
        activity.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                adjust(webView, action, newTextSize, callbackContext);
            }
        });
    }

    private void adjust(WebView webView, String action, TextSize newTextSize, CallbackContext callbackContext) {
        
        WebSettings webSettings = webView.getSettings();
        TextSize oldTextSize = webSettings.getTextSize();
        
        if (ACTION_INCREASE.equals(action)) {
            newTextSize = TextSize.values()[Math.min(oldTextSize.ordinal() + 1, TextSize.values().length - 1)];
        } else if (ACTION_DECREASE.equals(action)) {
            newTextSize = TextSize.values()[Math.max(oldTextSize.ordinal() - 1, 0)];
        }
        
        webSettings.setTextSize(newTextSize);
        callbackContext.success(TEXT_SIZE_VALUES.get(newTextSize));
    }
}
