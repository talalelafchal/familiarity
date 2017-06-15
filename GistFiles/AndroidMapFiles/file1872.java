package helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rkmarketingltd.tabletalkies.GCMRegistrationIntentService;
import com.rkmarketingltd.tabletalkies.MainActivity;
import com.rkmarketingltd.tabletalkies.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Verdant Clock Web on 27-Apr-16.
 */
public class Utilities  {

    Context context;
    FragmentManager fragmentManager;

/*------------------------------------------------------*
*             Constructor Functions                     *
*-------------------------------------------------------*/

    //--- for general utilities
    public Utilities(Context context) {
        this.context = context;
    }

    //--- For fragments
    public Utilities(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

/*------------------------------------------------------*
*                    General Functions                  *
*-------------------------------------------------------*/

    /*--- Change Fragments with Previous Fragment
         fragment = Fragment Object,
         Tag =  Fragment TAG
         addToBackStack = true/false
    */
    public void changeFragment(Fragment fragment, String TAG, Boolean addToBackStack, int anim) {
        //--- Fragment Manager initialization
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (anim == 1)
            fragmentTransaction.setCustomAnimations(R.anim.blink, R.anim.exit_to_right);
        else if (anim == 2)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        else if (anim == 3)
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        else { }
        fragmentTransaction.replace(R.id.fragment_content, fragment,TAG);
        if (addToBackStack) {
            Log.d("COFFEE", TAG + "Added to Stack");
            fragmentTransaction.addToBackStack(TAG);
        }
        fragmentTransaction.commit();
    }

    //--- get unique Device id --
    public String getDeviceId() {
         return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

      //---- Alert Dialog with intent 
    public void showAlertWithIntent(String title, String message, final Intent intent) {
        android.support.v7.app.AlertDialog.Builder alertDialog;
        alertDialog = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    //--- get IMEI number of Device
    public String getIMEINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId().toString();

    }
    //--- get ip address of mobile 
    public String getIpAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }
    //---- Px to Dp Converter
    public int pxToDp(int px) {
        float density = context.getResources().getDisplayMetrics().density;
        int dp = (int) (px * density + 0.5f);
        return dp;
    }


    //--- Clear full BackStack
    public void clearFullStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    //--- get Email ID from Device
    public String getDeviceEmailId() {
        String email_id = null;
        AccountManager accManager = AccountManager.get(context);
        Account acc[] = accManager.getAccountsByType("com.google");
        int accCount = acc.length;

        for (int i = 0; i < accCount; i++) {
            email_id = acc[i].name;
        }
        return email_id;
    }


    /*------------------------------------------------------*
    *             Shared Preferences Functions              *
    *-------------------------------------------------------*/
    /*HashMap<String, String> map = new HashMap<String, String>();
       map.put("key", "value");
       putPref(map);*/
    //------ Write Shared Preferences
    public void putPref(HashMap<String, String> map) {
        SharedPreferences preferences = context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) iterator.next();
            //System.out.println(pair.getKey()+" : "+pair.getValue());
            editor.putString(pair.getKey().toString().trim(), pair.getValue().toString().trim());
            iterator.remove();
        }
        editor.commit();
    }

    //--- Read Preferences -----
    public String getPref(String prefKey) {
        SharedPreferences preferences = context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getString(prefKey.trim(), "");
    }

    //--- Clear All Preferences ---
    public void clearAllPref() {
        SharedPreferences preferences = context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    //--- Clear single preferences ---
    public void clearPref(String prefKey) {
        SharedPreferences preferences = context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        preferences.edit().remove(prefKey).commit();
    }

    //--- code to check Google play services availability.
    public boolean isPlayServicesInstalled() {
        GoogleApiAvailability getGoogleapiAvailability = GoogleApiAvailability.getInstance();
        int Code = getGoogleapiAvailability.isGooglePlayServicesAvailable(context);
        if (Code != ConnectionResult.SUCCESS) {
            Log.i("LOC_TAG", "ERROR");
            if (getGoogleapiAvailability.isUserResolvableError(Code)) {
                getGoogleapiAvailability.getErrorDialog(new MainActivity(), Code, 9000).show();
            } else {
                Log.i("LOC_TAG", "This device is not supported.");
                //finish();
            }
            return false;
        }
        Log.i("LOC_TAG", "SUCCESS");
        return true;
    }

    //--- Check device connected to or not internet
    public boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


}