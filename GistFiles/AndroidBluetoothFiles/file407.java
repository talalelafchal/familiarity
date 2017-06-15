package com.dnn.zapbuild.commons.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dnn.zapbuild.commons.BaseApp;

/**
 * Created by zapbuild on 17/11/14.
 */
public class CommonUtil {

    /**
     *
     * @param message message to be displayed in Toast.
     */

    public static void showToast(String message)
    {
        Toast.makeText(BaseApp.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showIME(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideIME(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
