package utility;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.URL;


public class iOSInit {
    public static IOSDriver driver;
    public static AppiumDriver deviceDriver;
    public static String URL = "Appium URL";
    public static String udid = "1234567890";
    public static String bundleID = "app.BundleID";

/*
   **************************************
   Test Setup - Native App

		 Launch app on real device
		 1. source code and cert must available
		 2. if app has installed , just use bundleID can launch app
		 3. the mobile device have to enable UI Automation from developer
		 4. deviceName can not empty , the device name is hard code but can be wrong name
		 5. if hybrid page,input ios_webkit_debug_proxy -c e51f816ea8511c59bcc631fe89b7049599d2f21a:27753 -d under command

   **************************************
*/

    public static IOSDriver launchNativeApp() throws IOException, InterruptedException{

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName","iOS");
        capabilities.setCapability("deviceName","iPad (3)");
        capabilities.setCapability("platformVersion", "8.3");
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("bundleId", bundleID);
        // capabilities.setCapability("orientation", "LANDSCAPE");
        return driver = new IOSDriver(new URL("http://" + URL +"/wd/hub"), capabilities);
    }


    /*
   **************************************
   Test Setup - Hybrid App

	    Launch webview app for web testing
	    1. launch app a bit slow around 30s
	    2. MUST launch ios_webkit_debug_proxy -c e51f816ea8511c59bcc631fe89b7049599d2f21a:27753
	    3. switch to webview mode

   **************************************
*/

    public static IOSDriver launchRealDevWebview() throws IOException, InterruptedException{

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName","iOS");
        capabilities.setCapability("deviceName","iOS Device");
        capabilities.setCapability("platformVersion", "8.3");
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("bundleId", "com.WebViewApp");
        //  capabilities.setCapability("orientation", "LANDSCAPE");
        return driver = new IOSDriver(new URL("http://" + URL +"/wd/hub"), capabilities);
    }
}