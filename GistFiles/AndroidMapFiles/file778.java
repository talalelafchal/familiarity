package com.siu.android.tennisparis.app.activity;

import android.content.*;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.apps.analytics.easytracking.EasyTracker;
import com.google.android.maps.GeoPoint;
import com.siu.android.tennisparis.Application;
import com.siu.android.tennisparis.R;
import com.siu.android.tennisparis.app.service.TennisUpdaterService;
import com.siu.android.tennisparis.dao.model.Tennis;
import com.siu.android.tennisparis.list.TennisListAdapter;
import com.siu.android.tennisparis.map.EnhancedMapView;
import com.siu.android.tennisparis.map.TennisOverlay;
import com.siu.android.tennisparis.task.CurrentLocationTask;
import com.siu.android.tennisparis.task.TennisLoadTask;
import com.siu.android.tennisparis.toast.AppToast;
import com.siu.android.tennisparis.util.LocationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Piliszczuk <lukasz.pili AT gmail.com>
 */
@SuppressWarnings("deprecation")
public class TennisMapActivity extends SherlockMapActivity {

    private EnhancedMapView mapView;
    private ListView listView;
    private TennisListAdapter listAdapter;

    private TennisLoadTask tennisLoadTask;

    private TennisOverlay tennisOverlay;

    private List<Tennis> tennises = new ArrayList<Tennis>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.tennis_map_activity);

        setSupportProgressBarIndeterminateVisibility(false);

        mapView = (EnhancedMapView) findViewById(R.id.map);
        listView = (ListView) findViewById(android.R.id.list);

        startTennisLoading();
    }


    /* Centers Location */

    private void startTennisLoading() {
        stopTennisLoadingIfRunning();

        tennisLoadTask = new TennisLoadTask(this);
        tennisLoadTask.execute();
    }

    private void stopTennisLoadingIfRunning() {
        if (null == tennisLoadTask) {
            return;
        }

        tennisLoadTask.cancel(true);
        tennisLoadTask = null;
    }

    public void onTennisLoadSuccess(List<Tennis> receivedTennis) {
        tennises.clear();
        tennises.addAll(receivedTennis);

        tennisOverlay.getOverlayItems().clear();
        tennisOverlay.addTennises(tennises);

        listAdapter.notifyDataSetChanged();
        mapView.invalidate();
    }

    public void onTennisLoadFinish() {
        tennisLoadTask = null;
    }
}