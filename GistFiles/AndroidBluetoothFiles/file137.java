public static java.lang.String getDeviceID() {
		//1 compute IMEI
        TelephonyManager TelephonyMgr = (TelephonyManager)instance.getSystemService(TELEPHONY_SERVICE);
    	String imei = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
    	
        //2 compute DEVICE ID
        String devIDShort = "35" + //we make this look like a valid IMEI
        	Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
        	Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
        	Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
        	Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
        	Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
        	Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
        	Build.USER.length()%10 ; //13 digits
        
        //3 android ID - unreliable
        String androidID = Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID); 
        
        //4 wifi manager, read MAC address - requires  android.permission.ACCESS_WIFI_STATE or comes as null
        WifiManager wm = (WifiManager)instance.getSystemService(Context.WIFI_SERVICE);
        String wLANMAC = wm.getConnectionInfo().getMacAddress();

        //5 Bluetooth MAC address  android.permission.BLUETOOTH required
        BluetoothAdapter m_BluetoothAdapter	= null; // Local Bluetooth adapter
    	m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	String bTMAC = m_BluetoothAdapter.getAddress();
    	
    	//6 SUM THE IDs
    	String deviceID = imei + devIDShort + androidID + wLANMAC + bTMAC;
    	return deviceID;
	}