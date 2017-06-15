package gturedi.gist;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressWarnings("ResourceType")
public class ActivityUtil {

    private static final int LOADING_PANEL_ID = 12;
    private static final int MESSAGE_PANEL_ID = 13;
    private static final int PROGRESS_SIZE = 50;

    private final Activity activity;
    private ViewGroup root;

    public ActivityUtil(Activity activity) {
        this.activity = activity;
        root = (ViewGroup) activity.findViewById(android.R.id.content);
    }

    public AlertDialog createAlert(@StringRes int title, @StringRes int msg) {
        return createAlert(activity.getString(title), activity.getString(msg));
    }

    public AlertDialog createAlert(String title, String msg) {
        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void showLoading() {
        AndroidUtil.setVisibilityForChildren(root, View.GONE);
        View loadingPanel = root.findViewById(LOADING_PANEL_ID);
        if (loadingPanel == null) root.addView(createLoadingPanel());
        else loadingPanel.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        View panel = root.findViewById(LOADING_PANEL_ID);
        if (panel != null) root.removeView(panel);
        AndroidUtil.setVisibilityForChildren(root, View.VISIBLE);
    }

    public void showMessage(@StringRes int stringRes) {
        showMessage(activity.getString(stringRes));
    }

    public void showMessage(String msg) {
        AndroidUtil.setVisibilityForChildren(root, View.GONE);
        View panel = root.findViewById(MESSAGE_PANEL_ID);
        if (panel == null) root.addView(createMessagePanel(msg));
        else panel.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {
        View panel = root.findViewById(MESSAGE_PANEL_ID);
        if (panel != null) root.removeView(panel);
        AndroidUtil.setVisibilityForChildren(root, View.VISIBLE);
    }

    // helpers //

    private View createLoadingPanel() {
        ProgressBar progressBar = new ProgressBar(activity);
        int size = new ContextUtil(activity.getApplication()).convertDpToPixel(PROGRESS_SIZE);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        return createPanel(progressBar, LOADING_PANEL_ID);
    }

    private View createMessagePanel(String msg) {
        TextView tv = new TextView(activity);
        tv.setText(msg);
        return createPanel(tv, MESSAGE_PANEL_ID);
    }

    private LinearLayout createPanel(View child, int id) {
        LinearLayout lnr = new LinearLayout(activity);
        lnr.setId(id);
        lnr.setGravity(Gravity.CENTER);
        lnr.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lnr.addView(child);
        return lnr;
    }

}