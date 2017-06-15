

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class DLDeviceUtil {

    /** Value - {@value}, Tag for Log output.*/
    public static final String TAG = "DLDeviceUtil";

    /** Value - {@value}, Constant for first available wireless lan hardware.*/
    public static final String WLAN0 = "wlan0";

    /** Value - {@value}, Constant for first available wireless lan hardware.*/
    public static final String ETH0 = "eth0";

    /**
     * Returns MAC Address of wireless lan of the device.
     *
     * @return  String format MAC Address or empty string.
     * @see     com.tigerspike.android.commons.DLDeviceUtil#WLAN0
     * @see     com.tigerspike.android.commons.DLDeviceUtil#getMACAddress(String)
     */
    public static String getMACAddressWLan() {
        return getMACAddress(WLAN0);
    }

    /**
     * Returns MAC Address of ethernet of the device.
     *
     * @return  String format MAC Address or empty string.
     * @see     com.tigerspike.android.commons.DLDeviceUtil#ETH0
     * @see     com.tigerspike.android.commons.DLDeviceUtil#getMACAddress(String)
     */
    public static String getMACAddressEthernet() {
        return getMACAddress(ETH0);
    }

    /**
     * Returns MAC Address device's hardware interface.
     *
     * The parameter takes the interface name and MAC Address of particular interface is obtained.
     * If null or empty value is provided, it will get the first interface that is connected to
     * the internet.
     *
     * @param interfaceName Provide {@link com.tigerspike.android.commons.DLDeviceUtil#ETH0},
     *                      {@link com.tigerspike.android.commons.DLDeviceUtil#WLAN0} or NULL
     *                      to use first available interface
     * @return              String format MAC Address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        String macAddress = "";
        try {
            List<NetworkInterface> interfaces = Collections.list(
                                                        NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (interfaceName != null) {
                    if (!networkInterface.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder buf = new StringBuilder();
                    for (byte aMac : mac) {
                        buf.append(String.format("%02X:", aMac));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    macAddress = buf.toString();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macAddress;
    }


    /**
     * Returns IP(v4) Address of the device.
     *
     * @return String format IP v4 Address.
     */
    public static String getIPAddress() {
        return getIPAddress(true);
    }

    /**
     * Returns IP Address of the device.
     *
     * Provides IP address from first non-localhost interface, IP v4 or v6 can be obtained by
     * passing the boolean parameter.
     *
     * See discussion on <a target="_blank"
     * href="http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device">
     * Stack Overflow</a> about how to get IP Address correctly across all Android devices.
     *
     *
     * @param useIPv4   Returns IP v4 if passed true else IP v6
     * @return          IP Address or empty string.
     */
    public static String getIPAddress(boolean useIPv4) {
        String stringAddress = "";
        try {
            List<NetworkInterface> interfaces = Collections.list(
                    NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> iNetAddresses = Collections.list(
                                                        networkInterface.getInetAddresses());
                for (InetAddress iNetAddress : iNetAddresses) {
                    if (!iNetAddress.isLoopbackAddress()) {
                        stringAddress = iNetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(stringAddress);
                        if (useIPv4) {
                            if (isIPv4) {
                                return stringAddress;
                            }
                        } else {
                            if (!isIPv4) {
                                int delimiter = stringAddress.indexOf('%'); // drop ip6 port suffix
                                return (delimiter < 0 ? stringAddress :
                                                        stringAddress.substring(0, delimiter));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stringAddress;
    }

    /**
     * Check network connectivity status of the device using {@link android.net.ConnectivityManager}
     * and {@link android.net.NetworkInfo}.
     *
     * @param context   Provide context of the application.
     * @return          true if connected to the internet.
     */
    public static boolean isNetworkConnected(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                                                                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    /**
     * Returns device's unique id using {@link android.telephony.TelephonyManager}.
     *
     * See discussion on
     * <a target="_blank" href="http://stackoverflow.com/a/2853253/2534207">Stack Overflow</a>
     * about how to get Device Id correctly across all Android devices.
     *
     *
     * @param context   Provide context of the application.
     * @return          Device's id in String format.
     */
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                                                                        Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(),
                                        ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        return deviceUuid.toString();
    }

    /**
     * Returns width of device in pixels.
     *
     * @param context   Provide context of the application.
     * @return          Width of the device.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int width;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            width = display.getWidth();  // deprecated

        } else {
            Point size = new Point();
            display.getSize(size);

            width = size.x;
        }

        return width;
    }

    /**
     * Returns height of device in pixels.
     *
     * @param context   Provide context of the application.
     * @return          Height of the device.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int height;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            height = display.getHeight();  // deprecated

        } else {
            Point size = new Point();
            display.getSize(size);

            height = size.y;
        }

        return height;
    }


    /**
     * Returns version code of the Application using {@link android.content.pm.PackageManager}.
     *
     * @param context   Context of the application.
     * @return          Version code of the Application.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                                                                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Returns last known location of device using {@link android.location.LocationManager}.
     *
     * @param context   Context of the application
     * @return          {@link android.location.Location} object.
     */
    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext()
                                                        .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    /**
     * Returns Manufacturer and Model name as String using {@link android.os.Build}.
     * Get device's Manufacturer and Model name as String.
     *
     * @return  {@link android.os.Build#MANUFACTURER} and {@link android.os.Build#MODEL}.
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return DLUtil.capitalize(model);
        } else {
            return DLUtil.capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * @return true if external storage state is readable.
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * @return true if external storage state is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
