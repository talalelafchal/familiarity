package com.package.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.package.accessibility.merchants.Amazon;
import com.package.accessibility.merchants.Chrome;
import com.package.accessibility.merchants.Ebay;
import com.package.accessibility.merchants.Flipkart;
import com.package.accessibility.merchants.HomeShop18;
import com.package.accessibility.merchants.Jabong;
import com.package.accessibility.merchants.Koovs;
import com.package.accessibility.merchants.LimeRoad;
import com.package.accessibility.merchants.Myntra;
import com.package.accessibility.merchants.Paytm;
import com.package.accessibility.merchants.ShopClues;
import com.package.accessibility.merchants.Snapdeal;
import com.package.accessibility.merchants.Zoovi;

/**
 * Created by umesh0492 on 30/12/15.
 */
public class NewAccessibilityService extends android.accessibilityservice.AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    String[] packages = {"com.flipkart.android", "com.android.chrome", "com.ebay.mobile",
            "com.myntra.android", "net.one97.paytm", "com.shopping.limeroad", "com.snapdeal.main",
            "com.jabong.android", "com.shopclues", "com.robemall.zovi",
            "com.amazon.mShop.android.shopping", "in.amazon.mShop.android.shopping",
             "com.homeshop18.activity","com.koovs.fashion"};


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.d(TAG, "Event Type : " + event.getEventType());
        try {
            if (event.getPackageName() != null) {

                switch (event.getPackageName().toString()) {
                    case "com.flipkart.android":

                        Flipkart.sendEvent(event);

                        break;
                    case "com.android.chrome":

                        // if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED)
                        Chrome.sendEvent(event);

                        break;

                    case "com.snapdeal.main":

                        // if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED)
                        Snapdeal.sendEvent(event);

                        break;

                    case "com.ebay.mobile":

                        // if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED)
                        Ebay.sendEvent(event);

                        break;

                    case "in.amazon.mShop.android.shopping":
                    case "com.amazon.mShop.android.shopping":

                        if(event.getEventType() != AccessibilityEvent.TYPE_VIEW_SCROLLED)
                        Amazon.sendEvent(event);

                        break;

                    case "net.one97.paytm":

                        // if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED)
                        Paytm.sendEvent(event);

                        break;

                    case  "com.myntra.android":

                        Myntra.sendEvent(event);
                        break;

                    case  "com.shopping.limeroad":

                        LimeRoad.sendEvent(event);
                        break;

                    case  "com.robemall.zovi":

                        Zoovi.sendEvent(event);
                        break;

                    case  "com.homeshop18.activity":

                        HomeShop18.sendEvent(event);
                        break;

                    case  "com.jabong.android":

                        Jabong.sendEvent(event);
                        break;

                    case  "com.shopclues":

                        ShopClues.sendEvent(event);
                        break;

                    case  "com.koovs.fashion":

                        Koovs.sendEvent(event);
                        break;


                }
                // PackageMap.getHandlerForPackage(event.getPackageName().toString(), this).m11430a(event);
            }

        } //catch (ClassNotFoundException e) {}
        catch (Exception e2) {
        }

    }

    @Override
    public void onInterrupt() {

    }

    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service Connected..");

        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes |= AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        accessibilityServiceInfo.eventTypes |= AccessibilityEvent.TYPE_VIEW_SCROLLED;
        //accessibilityServiceInfo.eventTypes |= AccessibilityEvent.TYPES_ALL_MASK;

        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.DEFAULT;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        accessibilityServiceInfo.packageNames = packages;
        setServiceInfo(accessibilityServiceInfo);

    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        return 1;
    }


}
