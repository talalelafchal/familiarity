package info.devexchanges.deviceinfor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


public class InforActivity extends Activity {

    @InjectView(R.id.wifi_status)
    TextView textWifiStatus;

    @InjectView(R.id.manufacture)
    TextView textManufacture;

    @InjectView(R.id.model)
    TextView textModel;

    @InjectView(R.id.device_name)
    TextView textDeviceName;

    @InjectView(R.id.mobile_network_status)
    TextView textMobileNetworkStatus;

    @InjectView(R.id.kernel)
    TextView textKernelVersion;

    @InjectView(R.id.serial)
    TextView textSerial;

    @InjectView(R.id.baseband)
    TextView textBasebandVersion;

    @InjectView(R.id.version_os)
    TextView textOSVersion;

    @InjectView(R.id.build_num)
    TextView textBuildNumber;

    @InjectView(R.id.screen_res)
    TextView textScreenResolution;

    @InjectView(R.id.bluetooth_status)
    TextView textBluetoothStatus;

    /**
     * Handling button event
     * get device information and show in TextViews
     */
    @OnClick(R.id.btn_get)
    void onClick() {
        getDeviceInformation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor);

        //define inject this layout
        ButterKnife.inject(this);
    }

    public String getDeviceScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x; //device width
        int height = size.y; //device height

        return "" + width + " x " + height; //example "480 * 800"
    }

    private void getDeviceInformation() {
        textDeviceName.setText(Build.PRODUCT);
        textModel.setText(Build.MODEL); // get model name
        textManufacture.setText(Build.MANUFACTURER); // get device manufacture
        textSerial.setText(Build.SERIAL); // get device Serial
        textBasebandVersion.setText(Build.getRadioVersion());
        textOSVersion.setText(Build.VERSION.RELEASE); // get OS version
        textKernelVersion.setText(System.getProperty("os.version")); // get kernel version
        textWifiStatus.setText(isWifiNetworkAvailable(this)); // check wifi connection
        textMobileNetworkStatus.setText(isMobileNetworkAvailable(this)); //check 3G connection
        textBluetoothStatus.setText(checkBluetoothConnection()); //check bluetooth status
        textScreenResolution.setText(getDeviceScreenResolution()); //get device resolution
        textBuildNumber.setText(Build.FINGERPRINT); //get Device's Build Number
    }

    /**
     * Checks if the device has Internet connection.
     */
    public String isWifiNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return "Connected";
        } else return "Disconnected";
    }

    public String isMobileNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return "Connected";
        } else {
            return "Disconnected";
        }

    }

    private String checkBluetoothConnection () {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return "Not supported";
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                return "Disabled";
            } else {
                return "Enabled";
            }
        }
    }
}
