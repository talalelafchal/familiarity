package com.test.rootchecker;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class RootChecker {
	
	Context ctx;
	RootChecker(Context context)
	{
		ctx = context;
	}
	
	Boolean checkForSuperUserAPK()
	{		
		//package:com.noshufou.android.su.elite 
		//package:com.noshufou.android.su
		List<ApplicationInfo> localList = ctx.getPackageManager().getInstalledApplications(0x00);
		for (ApplicationInfo info : localList)
		{
			if(info.packageName.contains("com.noshufou"))
				return true;
		}
		return false;
	}
	
	Boolean checkSuBinary()
	{
	    //Iterate through all folders in PATH looking for su
	    Map<String,String> env = System.getenv();
	    String path = env.get("PATH");
	    String [] dirs = path.split(":");
 
        for (String dir : dirs){
            String suPath = dir + "/" + "su";
            File suFile = new File(suPath);
            if (suFile != null && suFile.exists()) {
            	return true;
            }
        }
	
	    return false;
	}
	
	Boolean check_ro_secure()
	{
		String roSecureProp = System.getProperty("ro.secure");		
		if (roSecureProp != null && roSecureProp.contains("0"))
			return true;
		
		return false;
	}
}
