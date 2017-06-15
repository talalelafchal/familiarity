package edu.ualr.cpsc7398.updatechecker.controller.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import edu.ualr.cpsc7398.updatechecker.controller.service.utils.DataBean;
import edu.ualr.cpsc7398.updatechecker.controller.service.utils.HttpRequestRunnable;
import edu.ualr.cpsc7398.updatechecker.controller.service.utils.OnLineTracker;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataConclusion;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralStatistics;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralWhatsGoingOn;
import edu.ualr.cpsc7398.updatechecker.view.activity.MainActivity;
import edu.ualr.cpsc7398.updatechecker.view.fragment.ConclusionFragment;
import edu.ualr.cpsc7398.updatechecker.view.fragment.StatisticsFragment;
import edu.ualr.cpsc7398.updatechecker.view.fragment.WhatsGoingOnFragment;


public class IntentServiceForBackgroundWork extends IntentService {

    private Handler h1;
    private MainActivity mainActivity = new MainActivity();

    public IntentServiceForBackgroundWork() {
        super("IntentServiceForBackgroundWork");
    }

    @Override
    protected void onHandleIntent(Intent intent) { //once the service starts run, it continues to  run until we stop
//        Bundle mBundle = intent.getExtras();
//        String url = mBundle.getString("URL", "");
//        int totalUpdates = mBundle.getInt("TOTAL_UPDATES", 0);
//        int timePeriod = mBundle.getInt("TIME_PERIOD", 0);
//        Log.i("InsideServeice ", " Inside service!"); //write in Conclusion
//        Log.i("Url: ", url); //write in Conclusion
//        Log.i("Total ", "Updates to know: "+String.valueOf(totalUpdates)); //write in Conclusion
//        Log.i("Time-period (Seed): ", String.valueOf(timePeriod)); //write in Conclusion

        Thread thread;
        if (HttpRequestRunnable.isUrlResourceStatic(DataBean.getUrl())) {
            Log.i("Inside toast", "inside toast");
            DataBean.setUrl(null);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "This is the static resource, plz. use dynamic resource!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        } else {
            if (!(HttpRequestRunnable.timeInterval1 == 0 || HttpRequestRunnable.totalUpdates1 == 0)) { //url is already checked from EditTextLocker class
                if (OnLineTracker.isOnline(getApplicationContext())) { //check online

                    while (HttpRequestRunnable.updateSuccessCount1 < HttpRequestRunnable.totalUpdates1) { //if it finds different last-update then it increments updateSuccessCount
                        Log.i("totalUpdates: ", "" + HttpRequestRunnable.totalUpdates1);

                        h1 = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                Log.i("Inside handerl1", "Inside handler");

                                Bundle bundle = msg.getData();


                                int requestno = bundle.getInt("REQUESTNO");
                                StatisticsFragment.mAdapter.updateOnlyValue(0, new UrlDataGeneralStatistics("Requests No:  ", "" + requestno)); //first is null, second is not null, so it should do


                                String requestsentsenttime = bundle.getString("REQUESTSENTSENTTIME");
                                StatisticsFragment.mAdapter.updateOnlyValue(1, new UrlDataGeneralStatistics("Request Sent Time: ", requestsentsenttime));


                                String lastmodified = bundle.getString("LASTMODIFIED");
                                WhatsGoingOnFragment.mAdapter.updateOnlyValue(0, new UrlDataGeneralWhatsGoingOn("Last-Modified:  ", "" + lastmodified));


//                            String uniquelastmodified = bundle.getString("UNIQUELASTMODIFIED");
//                            WhatsGoingOnFragment.mAdapter.updateOnlyValue(1, new UrlDataGeneralWhatsGoingOn("Unique Last-Modified:  ", ""+uniquelastmodified));
                                WhatsGoingOnFragment.mAdapter.updateOnlyValue(1, new UrlDataGeneralWhatsGoingOn("Unique Last-Modified:  ", DataBean.getUniqueLastModified()));

//                            long modifiedinterval = bundle.getLong("MODIFIEDINTERVAL");
                                String valModified;
                                if (DataBean.getModifiedInterval() == 0) {
                                    valModified = "";
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(2, new UrlDataGeneralWhatsGoingOn("Modified Interval:  ", valModified));
                                } else {
                                    valModified = String.valueOf(DataBean.getModifiedInterval());
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(2, new UrlDataGeneralWhatsGoingOn("Modified Interval:  ", valModified));
                                }

//                            long largestmodifiedinterval = bundle.getLong("LARGESTMODIFIEDINTERVAL");
                                String valLargest;
                                if (DataBean.getLargestInterval() == 0) {
                                    valLargest = "";
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(3, new UrlDataGeneralWhatsGoingOn("Largest Modified Interval:  ", valLargest));
                                } else {
                                    valLargest = String.valueOf(DataBean.getLargestInterval());
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(3, new UrlDataGeneralWhatsGoingOn("Largest Modified Interval:  ", valLargest));
                                }

//                            long leastmodifiedinterval = bundle.getLong("LEASTMODIFIEDINTERVAL");
                                String valLeast;
                                if (DataBean.getLeastInterval() == 0) {
                                    valLeast = "";
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(4, new UrlDataGeneralWhatsGoingOn("Least Modified Interval:  ", valLeast));
                                } else {
                                    valLeast = String.valueOf(DataBean.getLeastInterval());
                                    WhatsGoingOnFragment.mAdapter.updateOnlyValue(4, new UrlDataGeneralWhatsGoingOn("Least Modified Interval:  ", valLeast));
                                }

                                ConclusionFragment.mAdapter.insert(requestno, new UrlDataConclusion("\nIteration: " + requestno + "\n\t" + "Requests No:  " + requestno + "\n\t" + "Request Sent Time: " + requestsentsenttime + "\n\t" + "Last-Modified:  " + lastmodified + "\n\t" + "Unique Last-Modified: " + DataBean.getUniqueLastModified() + "\n\t" + "Modified Interval: " + valModified + "\n\t" + "Largest Modified Interval:  " + valLargest + "\n\t" + "Least Modified Interval:  " + valLeast));
                            }
                        };

                        thread = new Thread(new HttpRequestRunnable(h1));
                        thread.start();
                        try {
                            thread.sleep(1000 * HttpRequestRunnable.timeInterval1); //this starts the run() of HttpRequestRunnable runnable in separate thread
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    stopSelf(); //stop service
                    new HttpRequestRunnable(); //reset values
                }
            } //end
        }


    }
}
