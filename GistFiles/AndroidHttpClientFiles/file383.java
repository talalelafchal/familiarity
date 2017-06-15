package edu.ualr.cpsc7398.updatechecker.controller.service.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.InformationAdapter;
import edu.ualr.cpsc7398.updatechecker.view.fragment.InformationFragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by uddhav on 2/18/17.
 */

public class HttpRequestRunnable implements Runnable { //also acting as a Bean
    public static int updateSuccessCount1;
    public static int totalUpdates1; //get from GUI
    public static long timeInterval1; //thread sleeps this much time
    public static boolean ISINITIALIZE = false;
    public static int threadCount1; //requestNumber
    private static String url1;
    private static SimpleDateFormat dateFormat1;
    private static ArrayList<String> listLastModified1;
    private static ArrayList<String> uniqueListLastModified1;
    private static ArrayList<Long> listInterval1;
    private static long largestInterval1;
    private static long leastInterval1;


    private static boolean status2 = false;

    private static Handler h1;

    public HttpRequestRunnable(Handler h1) { //resets the information
        HttpRequestRunnable.h1 = h1;
    }

    public HttpRequestRunnable() {
        HttpRequestRunnable.ISINITIALIZE = true;

        InformationAdapter informationAdapter1 = InformationFragment.mAdapter;

        HttpRequestRunnable.dateFormat1 = new SimpleDateFormat("E',' dd MMM yyyy kk:mm:ss 'GMT'"); //instead of hh, use kk for 24 hours format

        HttpRequestRunnable.threadCount1 = 0;
        HttpRequestRunnable.updateSuccessCount1 = 0;

        HttpRequestRunnable.largestInterval1 = 0;
        HttpRequestRunnable.leastInterval1 = 0;

        HttpRequestRunnable.listLastModified1 = new ArrayList<>();
        HttpRequestRunnable.listLastModified1.clear();

        HttpRequestRunnable.uniqueListLastModified1 = new ArrayList<>();
        HttpRequestRunnable.uniqueListLastModified1.clear();

        HttpRequestRunnable.listInterval1 = new ArrayList<>();
        HttpRequestRunnable.listInterval1.clear();

        HttpRequestRunnable.url1 = DataBean.getUrl();
        HttpRequestRunnable.totalUpdates1 = DataBean.getTotalUpdates();
        HttpRequestRunnable.timeInterval1 = DataBean.getSeed();
    }

    public static boolean isUrlResourceStatic(String url2) { //is used in EditTextLocker class
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("E',' dd MMM yyyy kk:mm:ss 'GMT'"); //instead of hh, use kk for 24 hours format
        OkHttpClient httpClient2 = new OkHttpClient();

        TimeZone timeZone2 = TimeZone.getTimeZone("GMT");

        dateFormat2.setTimeZone(timeZone2);

        Calendar cal1 = Calendar.getInstance(timeZone2);
        cal1.add(Calendar.MINUTE, -10); //get time of 10 minutes before

        Date dateBeforeTenMinute2 = cal1.getTime();

        String dateStr2 = dateFormat2.format(dateBeforeTenMinute2); //current GMT time


        Log.i("Check ", "date of 10 min. before: " + dateStr2);

        try {
            Request request2 = new Request.Builder()  //note, Builder Design Pattern, it can make out of memory
                    .url(url2)
                    .addHeader("If-Modified-Since", dateStr2)
                    .build();
            Handler h2;

            h2 = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    HttpRequestRunnable.status2 = bundle.getBoolean("OURSTATUS");
                    Log.i("Ourstatus:", String.valueOf(status2));
                }
            };
            Thread t2 = new Thread(new CheckUrlResourceDynamicOrStatic(request2, httpClient2, h2)); //I could use two handler at each end
            t2.start();

//        status = CheckUrlResourceDynamicOrStatic.status; //do thread communication. Don't use static reference like this
        } catch (NullPointerException nul) {
            nul.printStackTrace();
        }

        return HttpRequestRunnable.status2;

    }

    @Override
    public void run() {
        HttpRequestRunnable.threadCount1++;

        Message m1 = Message.obtain(); //get null message
        Bundle bundle1 = new Bundle();

        bundle1.putInt("REQUESTNO", threadCount1);
//        m1.setData(bundle1);//
//        HttpRequestRunnable.h1.sendMessage(m1); //Pushes a message onto the end of the message queue after all pending messages before the current time.


        Log.i("Request No: ", String.valueOf(threadCount1)); //write in Statistics //write in Conclusion

        OkHttpClient httpClient1 = new OkHttpClient();
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date1 = new Date();
        String dateStr1 = dateFormat1.format(date1); //current GMT time

        Log.i("Request Sent Time: ", dateStr1); //write in Conclusion //write in Information
        bundle1.putString("REQUESTSENTSENTTIME", dateStr1);


        Request request1 = new Request.Builder()  //note, Builder Design Pattern, it can make out of memory
                .url(HttpRequestRunnable.url1)
                .addHeader("If-Modified-Since", dateStr1)
                .build();

        //IntentServiceForBackgroundWork.threadCount is to update in GUI
        //GUI update is done Via main thread

        try {
            Response response1 = httpClient1.newCall(request1).execute();
            String str1 = response1.header("Last-Modified");
//            Log.i("Lastmodified", str1);

            if (str1 != null) {
                listLastModified1.add(str1);

                bundle1.putString("LASTMODIFIED", str1);


                if (threadCount1 == 1) {
                    uniqueListLastModified1.add(str1);
                    updateSuccessCount1++;
                    DataBean.setUniqueLastModified(str1);

//                    bundle1.putString("UNIQUELASTMODIFIED", str1);
//                    Message m2 = Message.obtain();
//                    m2.setData(bundle1);
//                    HttpRequestRunnable.h1.sendMessage(m2); //same handler sending different message


                } else {
                    try {
                        if (((dateFormat1.parse(listLastModified1.get(threadCount1 - 1)).getTime()) - (dateFormat1.parse(listLastModified1.get(threadCount1 - 2)).getTime())) > 9000) {
                            uniqueListLastModified1.add(str1);
                            HttpRequestRunnable.updateSuccessCount1++;

                            //it is not possible every time this value goes in handler. So I needed to use DataBean
                            DataBean.setUniqueLastModified(str1);
//                            bundle1.putString("UNIQUELASTMODIFIED", str1);
//                            Message m2 = Message.obtain();
//                            m2.setData(bundle1);
//                            HttpRequestRunnable.h1.sendMessage(m2); //same handler sending different message

                            Log.i("New date: ", String.valueOf(listLastModified1.get(threadCount1 - 1))); //write in WhatsGoingOn
                            Log.i("Old date: ", String.valueOf(listLastModified1.get(threadCount1 - 2))); //write in WhatsGoingOn
//                            Log.i("UpdateCount: ", String.valueOf(HttpRequestRunnable.updateSuccessCount1));
                            long interval;
                            try {
                                interval = dateFormat1.parse(uniqueListLastModified1.get(HttpRequestRunnable.updateSuccessCount1 - 1)).getTime() - dateFormat1.parse(uniqueListLastModified1.get(HttpRequestRunnable.updateSuccessCount1 - 2)).getTime();
                                Log.i("Modified Interval: ", String.valueOf(interval / 1000) + " seconds!"); //write in WhatsGoingOn
                                long interval1 = interval / 1000;
                                DataBean.setModifiedInterval(interval1);
//                                bundle1.putLong("MODIFIEDINTERVAL", interval1);
//                                Message m4 = Message.obtain();
//                                m4.setData(bundle1);
//                                HttpRequestRunnable.h1.sendMessage(m4); //same handler sending different message

                                listInterval1.add(interval1);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("updateSuccess: ", "" + HttpRequestRunnable.updateSuccessCount1);


            if (HttpRequestRunnable.totalUpdates1 == HttpRequestRunnable.updateSuccessCount1) {
                //calculate least and largest modifiedInterval

                if (listInterval1.size() >= 1) {
                    largestInterval1 = Collections.max(listInterval1); //set largest Modified Interval
                    leastInterval1 = Collections.min(listInterval1); //set least Modified Interval
                }

//
//                bundle1.putLong("LARGESTMODIFIEDINTERVAL", largestInterval1);
//                bundle1.putLong("LEASTMODIFIEDINTERVAL", leastInterval1);
//
//                Message m5 = Message.obtain();
//                m5.setData(bundle1);
//                HttpRequestRunnable.h1.sendMessage(m5); //same handler sending different message

                DataBean.setLargestInterval(largestInterval1);
                DataBean.setLeastInterval(leastInterval1);

                Log.i("Largest Modified ", "Interval: " + String.valueOf(largestInterval1) + " seconds!"); //write in Conclusion //write in WhatsGoingOn
                Log.i("Least Modified ", "Interval: " + String.valueOf(leastInterval1) + " seconds!"); //write in Conclusion //write in WhatsGoingOn

            }

            m1.setData(bundle1);
            HttpRequestRunnable.h1.sendMessage(m1); //same handler sending different message

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
