import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;

public class Historian<T extends Activity> {

    public interface Host {
        void startActivity(Intent intent);
        Intent getIntent();
        String getPackageName();

        boolean matchIntent(Intent intent0, Intent intent1);
    }

    private static class IntentMatcher {
        final String mAlias;
        Intent mIntent;

        IntentMatcher(String alias) {
            mAlias = alias;
        }
    }

    private static final HashMap<Class<?>, ArrayList<IntentMatcher>> sClassMap =
            new HashMap<Class<?>, ArrayList<IntentMatcher>>();

    private final ArrayList<IntentMatcher> mLookup;

    public Historian(T host) {
        mLookup = getClassLookup(host.getClass());
        if (mLookup.size() == 0) {
            // store the alias pool for lookup
            final String[] aliasPool = getAliasPool(host);
            for (final String alias : aliasPool) {
                mLookup.add(new IntentMatcher(alias));
            }
        }
    }

    private static ArrayList<IntentMatcher> getClassLookup(Class<?> cls) {
        ArrayList<IntentMatcher> lookup = sClassMap.get(cls);
        if (lookup == null) {
            lookup = new ArrayList<IntentMatcher>();
            sClassMap.put(cls, lookup);
        }
        return lookup;
    }

    private String[] getAliasPool(Activity host) {
        final String[] ret = new String[0];
        final PackageManager pm = host.getPackageManager();
        final PackageInfo pi;
        try {
            pi = pm.getPackageInfo(host.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (final NameNotFoundException e) {
            // return empty pool
            return ret;
        }

        final String hostClass = host.getClass().getName();
        final ArrayList<String> pool = new ArrayList<String>();
        for (final ActivityInfo ai : pi.activities) {
            final String alias = ai.targetActivity;
            if (alias != null && alias.equals(hostClass)) {
                pool.add(ai.name);
            }
        }
        return pool.toArray(ret);
    }

    public void startActivity(Host host, Intent intent) {
        final IntentMatcher started = lookupMatch(host, intent);
        if (started != null) {
            started.mIntent = intent;
            final ComponentName cn = new ComponentName(host.getPackageName(), started.mAlias);
            intent.setComponent(cn);
        }
        host.startActivity(intent);
    }

    public void activityDestroyed(Host host) {
        final IntentMatcher im = lookupMatch(host, host.getIntent());
        if (im == null) return;
        im.mIntent = null;
    }

    private IntentMatcher lookupMatch(Host host, Intent intent) {
        IntentMatcher empty = null;
        for (final IntentMatcher im : mLookup) {
            if (host.matchIntent(im.mIntent, intent)) return im;
            if (empty == null && im.mIntent == null) empty = im;
        }
        return empty;
    }

}
