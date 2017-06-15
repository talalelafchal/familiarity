package gturedi.gist;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import gturedi.gist.BuildConfig;

public class AndroidUtil {

    public static boolean isSdCardvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static Intent createShareIntent(String subject, String text) {
        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text);
    }

    public static Intent createMailIntent(String subject, String text, String... receivers) {
        return new Intent(Intent.ACTION_SEND)
                .setType("plain/text")
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text)
                .putExtra(Intent.EXTRA_EMAIL, receivers);
    }

    public static Intent createMarketIntent() {
        String url = "market://details?id=" + BuildConfig.APPLICATION_ID;
        return createBrowserIntent(url);
    }

    public static Intent createBrowserIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public static Intent createMusicPlayerIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("audio/*");
    }

    public static Intent createVideoPlayerIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("video/*");
    }

    public static void navigate(FragmentManager fm, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction trans = fm.beginTransaction()
                .replace(android.R.id.content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (addToBackStack) trans.addToBackStack(fragment.getClass().getSimpleName());
        trans.commit();
    }

    public static void navigate(FragmentManager fm, Fragment fragment) {
        fm.beginTransaction()
                .replace(android.R.id.content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public static void bindDataForSpinner(Spinner spinner, List items) {
        ArrayAdapter adapter = new ArrayAdapter<>(
                spinner.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items);
        //adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static void bindDataForListView(ListView listView, List items) {
        ArrayAdapter adapter = new ArrayAdapter<>(
                listView.getContext(),
                android.R.layout.simple_list_item_1,
                items);
        listView.setAdapter(adapter);
    }

    public boolean isApiLevelSupported(@IntRange(from = 2, to = 23) int target) {
        return Build.VERSION.SDK_INT >= target;
    }

    public static void setVisibilityForChildren(ViewGroup parent, int visibility) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setVisibility(visibility);
        }
    }

    public static void setEnabledForChildren(ViewGroup parent, boolean enabled) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setEnabled(enabled);
        }
    }
    
    public static String getTimeAgoString(long time) {
        return DateUtils.getRelativeTimeSpanString(
                time,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS).toString();
    }

}