package coreservlets;

import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.ddmlib.DdmPreferences;
import com.google.common.io.CharStreams;


public class JavaMonkey {
	private static JavaMonkey javamonkey;
	private Runtime runtime;
	private static final String ADB = "E:/android/android-sdk/platform-tools/adb";
	private static final String ADB_GET_DEVICES_PATH = "E:/android/android-sdk/platform-tools/adb devices";
	private static final long TIMEOUT = 5000;
	private static int currenDeviceCount;       
	private ChimpChat mChimpchat;        
	private static int maxDeviceCount = 10;
	private IChimpDevice[] mDevice = new IChimpDevice[maxDeviceCount];
	private static int counter = 0;

	//Singleton Object
	public static synchronized JavaMonkey getJavaMonkey() {
		if (javamonkey == null) {
			javamonkey = new JavaMonkey();
		}
		return javamonkey;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private JavaMonkey() {
		super();
		TreeMap<String, String> options = new TreeMap<String, String>();
		options.put("backend", "adb");
		options.put("adbLocation", ADB);
		com.android.ddmlib.DdmPreferences.setTimeOut(300000);
		mChimpchat = ChimpChat.getInstance(options);
	}


	public String[] getDevices() {
		try {
			runtime = Runtime.getRuntime();
			Process p = runtime.exec(ADB_GET_DEVICES_PATH);
			java.io.DataInputStream in = new java.io.DataInputStream(p.getInputStream());

			byte[] buf = new byte[1024];
			int len = in.read(buf);

			String[] ligne = new String(buf,0,len).split("\n");
			String[] retour = new String[ligne.length - 1]; // We don't take the first line "List of devices attached"
			int count = 0;
			for(int i = 1; i<ligne.length; i++) {
				retour[i-1] = ligne[i].split("\t")[0];
				count++;
			}
			setDeviceCurrentCount(count - 1);                   
			return retour;
		}
		catch (java.io.IOException e) {
			//log.error(e); 
			System.out.println("Error:" + e);
		}

		return null;
	}

	
	
	public String[] getAPKInfo(String filename) {
		try {
			runtime = Runtime.getRuntime();
			Process z = runtime.exec("E:/android/android-sdk/platform-tools/aapt dump badging " + filename + " AndroidManifest.xml | find \"application-label:\"");                 
			String res = CharStreams.toString(new InputStreamReader(z.getInputStream()));
			System.out.println(res);
			String[] result = new String[5];
			final Matcher matcher = Pattern.compile("application-label:'").matcher(res);
			if(matcher.find()){
				String[] resParse = res.substring(matcher.end()).trim().split("'",2);            				                				               				   
				result[0] = resParse[0];
				System.out.println("Label:" + result[0]);            				       
			}
			final Matcher matcher2 = Pattern.compile("package: name='").matcher(res);
			if(matcher2.find()){
				String[] resParse  = res.substring(matcher2.end()).trim().split("'",2);            				                				               				   
				result[1] = resParse[0];            				               				   
				System.out.println("Package:" + result[1]);            				       
			}
			final Matcher matcher3 = Pattern.compile("launchable-activity: name='").matcher(res);
			if(matcher3.find()){
				String[] resParse  = res.substring(matcher3.end()).trim().split("'",2);            				                				               				   
				result[2]= resParse[0];            				               				   
				System.out.println("Activity:" + result[2]);
			}
			return result;

		} catch (java.io.IOException e) {
			//log.error(e); 
			System.out.println("Error:" + e);
		}
		return null;
	}


	public void init(String deviceConnected) {
		//mDevice = mChimpchat.waitForConnection(TIMEOUT, ".*");
		mDevice[counter] = mChimpchat.waitForConnection(TIMEOUT, deviceConnected);
		if ( mDevice[counter] == null ) {
			throw new RuntimeException("Couldn't connect.");
		}                
		mDevice[counter].wake();
		counter++;
	}


	public String[] getProperties(int devNum) {
		String[] res = new String[5];
		System.out.println("Device count in JavaMonkey = " + devNum);
		if ( mDevice[devNum] == null ) {
			throw new IllegalStateException("init() must be called first.");
		}

		for (String prop: mDevice[devNum].getPropertyList()) {
			System.out.println(prop + ": " + mDevice[devNum].getProperty(prop));
		}

		res[0] =  mDevice[devNum].getProperty("build.manufacturer") + " "+
				mDevice[devNum].getProperty("build.model");
		res[1] = mDevice[devNum].getProperty("build.version.release");
		res[2] = mDevice[devNum].getProperty("display.height") + " X " + mDevice[devNum].getProperty("display.width");
		res[3] = "";
		res[4] = "";
		return res;
	}

	//TODO: To Remove
	public boolean installApp(int devNum){
		boolean res = mDevice[devNum].installPackage("D:/TestTool/opera.apk");
		if (res)
			System.out.println("Installation Successfully");
		else {
			System.out.println("Installation Failed");
			return false;
		}
		return true;
	}
	
	
    private void installApp(IChimpDevice mDevice, String name){
   	 boolean res = mDevice.installPackage(name);
   	  if (res)
         	System.out.println(name + " installed");
   	  else
   		 System.out.println(name + " installation failed");
   }
	

	public boolean installApp(int devNum, String fileName){
		long startTime = System.nanoTime(); 
		boolean res = mDevice[devNum].installPackage(fileName);
		if (res)
			System.out.println("Installation Successfully");
		else {
			System.out.println("Installation Failed");
			return false;
		}
		long estimatedTime = System.nanoTime() - startTime;
		return true;
	}
	
	
	public String[] installApp2(int devNum, String fileName){
		long startTime = System.nanoTime();
		System.out.println("Starting Installation...");
		String[] resArray = new String[2];
		boolean res = mDevice[devNum].installPackage(fileName);
		if (res){
			System.out.println("Installation Successfully");
			resArray[0] = "true";
		}else {
			System.out.println("Installation Failed");
			resArray[0] = "false";
		}
		
		long estimatedTime = System.nanoTime() - startTime;
		double seconds = (double)estimatedTime / 1000000000.0;
		DecimalFormat f = new DecimalFormat("##.00");
				
		resArray[1] = f.format(seconds) + "";
		return resArray;
	}
	
    public void uninstall(int devNum, String arr1){	      
  	  boolean res = mDevice[devNum].removePackage(arr1);
 
	  if (res){
      	System.out.println("uninstalled");
	  }else{
		 System.out.println("uninstallation failed");
	  }	  
    }
	
	
    public String[] uninstall2(int devNum, String arr1){
	  long startTime = System.nanoTime();
	  System.out.println("Starting Uninstallation...");
      String[] resArray = new String[2];	
    	
  	  boolean res = mDevice[devNum].removePackage(arr1);
 
	  if (res){
      	System.out.println("uninstalled");
		resArray[0] = "true";
	  }else{
		 System.out.println("uninstallation failed");
		 resArray[0] = "false";
	  }	  
	  long estimatedTime = System.nanoTime() - startTime;
	  double seconds = (double)estimatedTime / 1000000000.0;
	  DecimalFormat f = new DecimalFormat("##.00");
				
	  resArray[1] = f.format(seconds) + "";
	  return resArray;
    }

    
	public static int getDeviceCurrentCount() {
		return currenDeviceCount;
	}

    public void runAppChimp(int devNum, String arr1, String arr2){
    	//IChimpDevice device = javaMonkey.getDevice();
    	String uri = null;
    	String action = arr2; //"com.opera.mini.android.Browser";
    	String data = null;
    	String mimeType = null;
    	Collection categories = new ArrayList();
    	Map extras = new HashMap();
    	String pkg = arr1; //"com.opera.mini.android";
    	
    	String activity = arr2.substring(arr2.lastIndexOf('.')+1).trim();
    	//String activity = arr2;
    	String component = pkg + "/." + activity;
    	int flags = 0;
    	
    	System.out.println("Activity" + activity);
    	mDevice[devNum].startActivity(uri, action, data, mimeType, categories, extras, component, flags);
    	mDevice[devNum].takeSnapshot();
      	System.out.println("Running " + arr2);
    }

	public void runAppChimp(String actionName, String activityName){
		//IChimpDevice device = javaMonkey.getDevice();
		String uri = null;
		String action = actionName;
		String data = null;
		String mimeType = null;
		Collection categories = new ArrayList();
		Map extras = new HashMap();
		String pkg = "com.opera.mini.android";
		String activity = activityName;
		String component = pkg + "/." + activity;
		int flags = 0;

		mDevice[0].startActivity(uri, action, data, mimeType, categories, extras, component, flags);
		System.out.println("Running installed APK");
	}

	public void runAppChimp(int devNum){
		String uri = null;
		String action = "com.opera.mini.android.Browser";
		String data = null;
		String mimeType = null;
		Collection categories = new ArrayList();
		Map extras = new HashMap();
		String pkg = "com.opera.mini.android";
		String activity = "Browser";
		String component = pkg + "/." + activity;
		int flags = 0;
		/**
		 * Start an activity.
		 *
		 * @param uri the URI for the Intent
		 * @param action the action for the Intent
		 * @param data the data URI for the Intent
		 * @param mimeType the mime type for the Intent
		 * @param categories the category names for the Intent
		 * @param extras the extras to add to the Intent
		 * @param component the component of the Intent
		 * @param flags the flags for the Intent
		 *
		 * void startActivity(@Nullable String uri, @Nullable String action,
		 * @Nullable String data, @Nullable String mimeType,
		 * Collection categories, Map extras, @Nullable String component,
		 * int flags);
		 */
		mDevice[devNum].startActivity(uri, action, data, mimeType, categories, extras, component, flags);
		//mDevice[devNum].takeSnapshot();
		System.out.println("Running Opera");
	}

    public void snapshot(int devNum, String name, int count){
    	IChimpImage image =  mDevice[devNum].takeSnapshot();
    	image.writeToFile("C:\\Users\\public\\test-app\\" 
    	+ name + "-" + count + ".png", "png");		
    }
	
	//TODO: Remove
	public void snapshot(int devNum){
		IChimpImage image = mDevice[devNum].takeSnapshot();
		image.writeToFile("D:\\TestTool\\img\\Screen-" + devNum + ".png", "png");
	}



	public static void setDeviceCurrentCount(int deviceCurrentCount) {
		JavaMonkey.currenDeviceCount = deviceCurrentCount;
	}

    /**
     * Terminates this JavaMonkey.
     */
    private void shutdown(int devNum) {
            mChimpchat.shutdown();
            mDevice[devNum] = null;
    }

}
