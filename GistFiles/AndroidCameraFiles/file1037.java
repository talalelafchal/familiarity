// Zdarzenie o przełączeniu aplikacji
//  Wymaga zaznaczenia opcji "Włączony" w
//  "Ustawienia > Dostępność > Usługi > AccEtykieta"
//
// Na podstawie
// http://developer.android.com/training/accessibility/service.html
// http://developer.android.com/guide/topics/ui/accessibility/services.html
//

package com.pieszynski.android.aloapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccService extends AccessibilityService {
    public final String TAGG = "T77p";

    public AccService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        CharSequence appPackage = event.getPackageName();
        Log.v(TAGG, "App: " + appPackage);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        //super.onServiceConnected();

        AccessibilityServiceInfo accs = new AccessibilityServiceInfo();
        accs.eventTypes =
                //AccessibilityEvent.TYPES_ALL_MASK |
                //AccessibilityEvent.TYPE_VIEW_CLICKED |
                //AccessibilityEvent.TYPE_VIEW_FOCUSED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        accs.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        accs.notificationTimeout = 100;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.
        accs.flags = AccessibilityServiceInfo.DEFAULT; // tylko jeśli nie określono .packageNames
        //accs.packageNames = new String[] {"com.sec.android.app.camera"};// .Camera

        this.setServiceInfo(accs);

        Log.v(TAGG, "Połączono!");
    }
}
