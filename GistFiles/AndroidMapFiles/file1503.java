package com.siu.android.tennisparis.task;

import android.os.AsyncTask;
import android.util.Log;
import com.siu.android.tennisparis.app.activity.TennisMapActivity;
import com.siu.android.tennisparis.dao.model.Tennis;
import com.siu.android.tennisparis.dao.model.TennisDao;
import com.siu.android.tennisparis.database.DatabaseHelper;
import com.siu.android.tennisparis.service.TennisService;
import com.siu.android.tennisparis.util.NetworkUtils;

import java.util.List;

/**
 * @author Lukasz Piliszczuk <lukasz.pili AT gmail.com>
 */
public class TennisLoadTask extends AsyncTask<Double, Void, List<Tennis>> {

    private TennisMapActivity activity;

    public TennisLoadTask(TennisMapActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected List<Tennis> doInBackground(Double... coords) {
        Log.d(getClass().getName(), "TennisLoadTask");
        return DatabaseHelper.getInstance().getDaoSession().getTennisDao().loadAll();
    }

    @Override
    protected void onPostExecute(List<Tennis> tennises) {
        Log.d(getClass().getName(), "Tennises : " + tennises.size());
        activity.setSupportProgressBarIndeterminateVisibility(false);

        if (null != tennises && !tennises.isEmpty()) {
            activity.onTennisLoadSuccess(tennises);
        }

        activity.onTennisLoadFinish();
    }

    @Override
    protected void onCancelled(List<Tennis> centers) {
        activity.setSupportProgressBarIndeterminateVisibility(false);
    }

    public void setActivity(TennisMapActivity activity) {
        this.activity = activity;
    }
}
