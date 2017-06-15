
package eAppRedmi4A;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.sikuli.script.*;

public class eAppRedmi4A
{
	/***********************************/
	static String MOBILE_NO = "9900525388";
	static int WAIT_ITERATION = 10;
	static int LAUNCH_ITERATION = 20;
	static boolean ett_selected = false;
	static boolean doallapps = true;
	static boolean amazon2 = false;
	static boolean semiautomode = false;//make false for fully auto
	static boolean balance_only = false;
	/***********************************/
	static int fromIndex = 1;
	static int endIndex = 100;
	static Set<Integer> ett_custom = new HashSet<Integer>();
	static boolean useTab = true;
	static Region balRegion = null; 
    static int ErrorCode = 0;
    static Runtime runtime = null;
    static Process process = null;
    static BufferedWriter bs = null;
    
    static String device = null;
    static int currId = 1;
  	static class Record
  	{
  	    int id;
  	    String mobileno;
  	    String otp;
  	    Set<String> set = new HashSet<String>();
  	    void SetRecord(int id, String mobileno, String otp)
  	    {
  	    	this.id = id;
  	    	this.mobileno = mobileno;
  	    	this.otp = otp;
  	    }
  	};
  	
  	 static Record [] rd = new Record[3500];
     
	 static String GetAutomationFolderPath()
	{
		return "C:\\Users\\mechu\\Desktop\\Automation\\eAppRedmi4A\\";
	}
	 static String GetImagePath(String name)
	{
		String path = GetAutomationFolderPath() + "Images\\";
		return path+name;
	}
	 public static void playSound()
	 {

	 }
  	static void UpdateAppInAccount(String app,String userid, String passwd) throws IOException
  	{
  		BufferedWriter bsw = null;
  		String dat = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
  		bsw = new BufferedWriter(new FileWriter(GetAutomationFolderPath() + "Output\\Apps\\" + app + ".txt",true));
  		bsw.write(dat + "      " + currId + "	" + userid + "    " + passwd);
  		bsw.newLine();
  		bsw.close();
  	}
	 static String GetAppImagePath(String appName, String imageName)
	{
		String path = GetImagePath("") + appName + "\\";
		return path+imageName;
	}
	 static String GenerateRandomNumber()
	{
        
       String strLong = Long.toString(System.currentTimeMillis());
       return strLong;
	}
	 static String GenerateMobileNo()
	{
       String strLong = Long.toString(System.currentTimeMillis());  
       return "97"+strLong.substring(5);
	}
	 static String GenerateRandomName()
	{
        String fisrtName[] = {"Ramesh","Suresh","Naveen","Nidhi","Rajesh","Pradeep","Sunil","Suman","Ravinder","Salman"};
        String middleName[] = {"Kumar","Singh","Pal","kumari","Devi","Das","Kr","Sharan","Kumar"," "};
        String lastName[] = {"Sharma","Gupta","Dabi","Agarwal","Choudhary","Sengar","Meel","Lotsara","Yadav","Shiyag"};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(9);
        
        int num = Integer.parseInt(strLong);
        String name = "";
        name = fisrtName[num%10];
        num = num/10;
        name += " " + middleName[num%10];
        num = num/10;
        name += " " + lastName[num%10];
       return name;
	}
	 static String GenerateRandomEmail()
	{
        String fisrtName[] = {"Ramesh","Suresh","Naveen","Nidhi","Rajesh","Pradeep","Sunil","Suman","Ravinder","Salman"};
        String middleName[] = {"Kumar","Singh","Pal","kumari","Devi","Das","Kr","Sharan","Kumar"," "};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(6);
        
        int num = Integer.parseInt(strLong);
        String name = "";
        name = fisrtName[num%10];
        num = num/10;
        name += middleName[num%10];
        num = num/10;
        
        name += strLong + "@gmail.com";
        name = name.replaceAll("\\s+","");
       return name;
	}
	 static String GenerateHandle()
	{
        String fisrtName[] = {"Ramesh","Suresh","Naveen","Nidhi","Rajesh","Pradeep","Sunil","Suman","Ravind","Salma"};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(5);
        
        int num = Integer.parseInt(strLong);
        String name = "";
        name = fisrtName[num%10];
        num = num/10;
        name += strLong;
        name = name.replaceAll("\\s+","");
       return name;
	}
	 static String GetFirstName()
	{
        String fisrtName[] = {"Ramesh","Suresh","Naveen","Nidhi","Rajesh","Pradeep","Sunil","Suman","Ravinder","Salman"};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(9);
        
        int num = Integer.parseInt(strLong);
        return fisrtName[num%10];	
	}
	 static String GetMiddleName()
	{
		 String middleName[] = {"Kumar","Singh","Pal","kumari","Devi","Das","Kr","Sharan","Kumar","Kripal"};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(9);
        
        int num = Integer.parseInt(strLong);
        return middleName[num%10];	
	}
	 static String GetLastName()
	{
		String lastName[] = {"Sharma","Gupta","Dabi","Agarwal","Choudhary","Sengar","Meel","Lotsara","Yadav","Shiyag"};
        String strLong = Long.toString(System.currentTimeMillis());
        strLong = strLong.substring(9);
        int num = Integer.parseInt(strLong);
        
        return lastName[num%10];
        	
	}
	 static String GetTime()
	{
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return  sdf.format(cal.getTime());
	}
	 static void WriteLog(String msg) throws IOException
	{
		bs.write(GetTime() + "  " + msg);
		bs.newLine();
		bs.flush();
	}
	 
	 static int DownloadFromBrowser(Screen s) throws FindFailed, InterruptedException
	 {
		 
    	  if(s.exists(GetImagePath("redirectapp.png")) != null)
    	  {
    		  return 0;
    	  }
     
     	  for(int x = 0; x < 60; x++)
     	  {
        	  if(s.exists(GetAppImagePath("wyncgames","harm_ok.png")) != null)
        	  {
        		  s.click(GetAppImagePath("wyncgames","harm_ok.png"));
        		 Thread.sleep(3000);
        	  }
        	  
        	  if(s.exists(GetAppImagePath("wyncgames","download.png")) != null)
        	  {
        		  s.click(GetAppImagePath("wyncgames","download.png"));
        		 Thread.sleep(1000);
        	  }

     		  if(s.exists(GetAppImagePath("wyncgames","open.png")) != null)
     		  {
     			s.click(GetAppImagePath("wyncgames","open.png"));
     			break;
     		  }
     		  Thread.sleep(1000);
     	  }
     	  
     	  return 1;
	 }
	 static void ShortWaitAndClick(Screen s, String path)  throws InterruptedException, FindFailed, IOException
	{
		int count = 0;
		ErrorCode = 0;
		System.out.println("waitAndClick " + path);
		
		while((count < 3) && (s.exists(path) == null))
		{
			Thread.sleep(2000);
			count++;
		}
		if(count == 3)
		{
			ErrorCode = 1;
			WriteLog("Image not found after 10 retry: " + path + "Setting Error Flag = 1");
		}
		else
		{
		  s.click(path);
		}
	}
	 static void waitAndClick(Screen s, String path, int time) throws InterruptedException, FindFailed, IOException
	{
		int count = 0;
		ErrorCode = 0;
		System.out.println("waitAndClick " + path);
		
		while((count < WAIT_ITERATION) && (s.exists(path) == null))
		{
			Thread.sleep(2000);
			count++;
		}
		if(count == WAIT_ITERATION)
		{
			ErrorCode = 1;
			WriteLog("Image not found after 10 retry: " + path + "Setting Error Flag = 1");
		}
		else
		{
		  s.click(path);
		}
	}
	 static void waitForImage(Screen s, String path, int time) throws InterruptedException, FindFailed, IOException
	 {
		int count = 0;
		ErrorCode = 0;

		while((count < WAIT_ITERATION) && (s.exists(path) == null))
		{
			Thread.sleep(time);
			count++;
		}
		
		if(count == WAIT_ITERATION)
		{
			ErrorCode = 1;
			WriteLog("Image not found after 10 retry: " + path + "Setting Error Flag = 1");
		}
	 }
	 static void ExecuteCommand(String str)
	 {
		   try
		   {
			   if(!device.isEmpty())
			   {
				   str = str.substring(4);
				   str = "adb -s " + device + " " + str;
			   }


			   System.out.println(str);
		       process = runtime.exec(str);
		       String line = null;
		       BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		       while ((line = input.readLine()) != null)
		       {
		    	   System.out.println(line);
		       }
		       input.close();
		   }
		   catch(IOException e)
		   {
            e.printStackTrace();
		   }
	 }
	 static void LaunchApp(String pkg) throws IOException, InterruptedException
	 {
		 ExecuteCommand("adb shell monkey -p " + pkg + " -c android.intent.category.LAUNCHER 1");
		 Thread.sleep(3000);
	 }
	 static void UninstallApps() throws InterruptedException, IOException
	 {
         System.out.println("Going for UnInstall Apps");

         
         //ExecuteCommand("adb shell pm uninstall  com.playphone.gamestore.loot");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.mobikwik_new");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.games24x7.teenpatti.playphone");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.skype.m2");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.m2fpremium.lionkingdom");
         //Thread.sleep(2000);
         //ExecuteCommand("adb shell pm uninstall  com.mobi2fun.skyblockpusher");
         //Thread.sleep(2000);

         ExecuteCommand("adb shell pm uninstall  com.mventus.selfcare.activity");
         Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.baadl.dekkho");
         //Thread.sleep(2000);
         
         ExecuteCommand("adb shell pm uninstall  com.app.dream11Pro");
         Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.wealthdoctor");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.realarcade.CMO");
         //Thread.sleep(2000);
         
         ExecuteCommand("adb shell pm uninstall  com.supersports.sportsflashes");
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell pm uninstall  in.amazon.mShop.android.shopping");
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell pm uninstall   in.droom.online_obv_app");
         Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall  com.aranoah.healthkart.plus");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall air.com.ace2three.mobile.cash");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall com.tweensoft.circles");
         //Thread.sleep(2000);
   
         //ExecuteCommand("adb shell pm uninstall in.msewa.vpayqwik");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall com.balaji.alt");
         //Thread.sleep(2000);
         
         //ExecuteCommand("adb shell pm uninstall com.m2fpremium.lionkingdom");
         //Thread.sleep(2000);
         
         /*
         ExecuteCommand("adb shell pm uninstall  com.jabong.android");
         Thread.sleep(2000);

         ExecuteCommand("adb shell pm uninstall  in.droom");
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell pm uninstall   com.saavn.android");
         Thread.sleep(2000);

         ExecuteCommand("adb shell rm -rf /storage/Download/9apps.apk");
         Thread.sleep(2000);
         

         ExecuteCommand("adb shell pm uninstall  com.eterno");
         Thread.sleep(2000);
         */
	 }
	 public static boolean DownloadAndInstallApp(Screen s, String appName) throws InterruptedException, FindFailed, IOException
	 {
		 if(s.exists(GetImagePath(appName)) == null)
		 {
			 WriteLog("ERROR: DownloadAndInstallApp " + appName + " APP NOT EXIST ON ETT SCREEN RETURN FALSE");
			 return false;
		 }
       
     	  waitAndClick(s,GetImagePath(appName),1000);
     	  if(ErrorCode == 1) return false;
     	 Thread.sleep(5000);


     	  waitAndClick(s,GetImagePath("download_now.png"),1000);
     	  if(ErrorCode == 1)
     	  {
     		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
       		  Thread.sleep(5000);
       		  waitAndClick(s,GetImagePath("download_now.png"),1000);
       		  if(ErrorCode == 1)
        	  {
         		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
           		  Thread.sleep(5000);
           		  waitAndClick(s,GetImagePath("download_now.png"),1000);
           		  if(ErrorCode == 1)
            	  {
           			 WriteLog(appName + " ERROR: waitAndClick download_now failed after retry");
            	     return false;
            	  }  
        	  }      		
     	  }
     	  
     	  Thread.sleep(10000);
     	  
     	  if(appName == "buyhatke.png") return true;
     	  
     	  if(s.exists(GetImagePath("redirectapp.png")) != null)
     	  {
     		  return false;
     	  }
     	  
     	  if(s.exists(GetImagePath("playstore.png")) != null)
     	  {
     		  s.click(GetImagePath("playstore.png"));
     		 Thread.sleep(8000);
     	  }

      	
     	  waitAndClick(s,GetImagePath("install.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 Thread.sleep(8000);
     		 ExecuteCommand("adb  shell input tap 866 1134");
     		 Thread.sleep(8000);
          }

     	  Thread.sleep(8000);
     	  
          if(s.exists(GetImagePath("accept.png")) != null)
          {
        	  s.click(GetImagePath("accept.png"));
        	  Thread.sleep(8000);
          }
     	  
     	  
     	 return true;
	 }
	 public static int InstallAmazon(Screen s) throws InterruptedException, FindFailed, IOException
	 {

		 if(DownloadAndInstallApp(s,"amazon.png") == false)
		 {
			 WriteLog("DownloadAndInstallApp through Playstore failed flag = false");
			 return 0;
		 }
		 
		 WriteLog("DownloadAndInstallApp Amazon Done Waiting for launch now");
		 
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	  System.out.println("Waiting for Amazon Launch.....");
       	      if(s.exists(GetAppImagePath("Amazon","amazon_launch.png")) != null)
 	          {
	       		  System.out.println("Amazon Launched.....");
	       		  Thread.sleep(9000);
	       		  break;
 	          }
       	      System.out.println("Amazon not launched retrying again.....");
       	      Thread.sleep(3000);
         }
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Amazon launch not detected check logs..");
             return 0;
         }
         //create new account
       
         Thread.sleep((long) (1000*60));
         /*
         ExecuteCommand("adb  shell input tap 382 734");//existing user
         Thread.sleep(12000);
         
         ExecuteCommand("adb  shell input tap 215 486");//existing user
         Thread.sleep(3000);
         
         ExecuteCommand("adb  shell input text mechu@vmware.com");//existing user
         Thread.sleep(3000);
         
         ExecuteCommand("adb  shell input tap 207 575");//existing user
         Thread.sleep(3000);
         
         ExecuteCommand("adb  shell input text 41s65a42tv");//existing user
         Thread.sleep(3000);
       
         ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");//existing user
         Thread.sleep(3000);
         
         ExecuteCommand("adb  shell input tap 365 795");//existing user
         */
         //Thread.sleep((long) (300000));
		 return 1;
	 }
	 public static int InstallDekho(Screen s) throws InterruptedException, FindFailed, IOException
	 {

		 if(DownloadAndInstallApp(s,"dekho.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	  System.out.println("Waiting for dekho Launch.....");
       	      if(s.exists(GetAppImagePath("dekho","dekho_launched.png")) != null)
 	          {
	       		  System.out.println("dekho Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("dekho not launched retrying again.....");
       	      Thread.sleep(3000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Dailyhunt launch not detected check logs..");
             return 0;
         }
         
         ExecuteCommand("adb  shell input tap 365 197");
         Thread.sleep(7000);
         
         ExecuteCommand("adb  shell input tap 365 197");
         Thread.sleep(2000);
     
         Thread.sleep(10000);
         
		 return 1;
	 }
	 static void turnoffwifi() throws InterruptedException
	 {
    	 ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         Thread.sleep(3000);
         
    	 ExecuteCommand("adb shell am start -a android.intent.action.MAIN -n com.android.settings/.wifi.WifiSettings");
         Thread.sleep(3000);
         ExecuteCommand("adb shell input tap 693 130");
         Thread.sleep(5000);
     
    	 ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         Thread.sleep(3000);
	 }
	 static void turnonwifi() throws InterruptedException
	 {
    	 ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         Thread.sleep(3000);
    	
		 ExecuteCommand("adb shell am start -a android.intent.action.MAIN -n com.android.settings/.wifi.WifiSettings");
         Thread.sleep(3000);
         ExecuteCommand("adb shell input tap 693 130");
         Thread.sleep(5000);
		 
    	 ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         Thread.sleep(3000);
	 }
	 public static int InstallMyVodafone(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 if(DownloadAndInstallApp(s,"myvodafone.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
	     for(x = 0; x < LAUNCH_ITERATION; x++)
	     {
	        System.out.println("Waiting for MyVodafone Launch.....");
	       	if(s.exists(GetAppImagePath("myvodafone","myvodafone_launched.png")) != null)
	 	    {
			   System.out.println("myvodafone Launched now click it.....");
			   Thread.sleep(5000);
			   break;
	 	    }
	       	System.out.println("myvodafone not launched retrying again.....");
	       	Thread.sleep(3000);
	     }
	         
	     if(x == LAUNCH_ITERATION)
	     {
	          WriteLog("myvodafone launch not detected check logs..");
	          return 0;
	     }
	         
		 ExecuteCommand("adb shell pm grant com.mventus.selfcare.activity android.permission.RECEIVE_SMS");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant com.mventus.selfcare.activity android.permission.READ_CONTACTS");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant com.mventus.selfcare.activity android.permission.WRITE_EXTERNAL_STORAGE");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant com.mventus.selfcare.activity android.permission.ACCESS_FINE_LOCATION");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant com.mventus.selfcare.activity android.permission.CALL_PHONE");
		 Thread.sleep(1000);

		 ExecuteCommand("adb shell am force-stop com.mventus.selfcare.activity");
         Thread.sleep(6000);
         LaunchApp("com.mventus.selfcare.activity");
         
         waitAndClick(s,GetAppImagePath("myvodafone","open.png"),1000);
         Thread.sleep(10000);
         waitAndClick(s,GetAppImagePath("myvodafone","login.png"),1000);
         Thread.sleep(10000);
	   
	     ExecuteCommand("adb  shell input text 9587746024");
	     Thread.sleep(4000);
	     
	     //ExecuteCommand("adb  shell input tap 192 780");
	     //Thread.sleep(4000);
	     
	     waitAndClick(s,GetAppImagePath("myvodafone","checkbox.png"),1000);
	     Thread.sleep(3000);
	     
	     waitAndClick(s,GetAppImagePath("myvodafone","go.png"),1000);
	     Thread.sleep(8000);
	     
	     ExecuteCommand("adb  shell input tap 570 712");//sms
	     Thread.sleep(10000);
	     
	     waitAndClick(s,GetAppImagePath("myvodafone","ok.png"),1000);
	     
		 ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
         Thread.sleep(3000);
         ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
         Thread.sleep(3000);
	     ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");//sms
	     Thread.sleep(3000);
         
         ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         Thread.sleep(3000);
	     
		 ExecuteCommand("adb shell am force-stop com.mventus.selfcare.activity");
         Thread.sleep(6000);
         LaunchApp("com.mventus.selfcare.activity");
         Thread.sleep(4000);
         waitAndClick(s,GetAppImagePath("myvodafone","open.png"),1000);
         Thread.sleep(10000);
	     ExecuteCommand("adb  shell input tap 60 121");//sms
	     Thread.sleep(5000);
	     ExecuteCommand("adb  shell input tap 70 208");//sms
	     Thread.sleep(5000);
	     ExecuteCommand("adb  shell input text 9587746024");//sms
	     Thread.sleep(3000);
	     ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");//sms
	     Thread.sleep(3000);
	     ExecuteCommand("adb  shell input text 20");//sms
	     Thread.sleep(3000);
	     
	     ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");//sms
	     Thread.sleep(3000);
	     
	     waitAndClick(s,GetAppImagePath("myvodafone","paygo.png"),1000);
	     Thread.sleep(3000);
	     waitAndClick(s,GetAppImagePath("myvodafone","buy.png"),1000);
	     Thread.sleep(3000);
	     waitAndClick(s,GetAppImagePath("myvodafone","yes.png"),1000);
	     Thread.sleep(3000);
	     waitAndClick(s,GetAppImagePath("myvodafone","netbanking.png"),1000);
	     Thread.sleep(6000);
	     waitAndClick(s,GetAppImagePath("myvodafone","hdfc.png"),1000);
	     Thread.sleep(3000);
	     ExecuteCommand("adb  shell input swipe 300 900 300 100");//YES
	     Thread.sleep(6000);
	     waitAndClick(s,GetAppImagePath("myvodafone","paynow.png"),1000);
	 
         Thread.sleep(8000);
		 return 1;
	 }
	 public static int InstallDroom(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 if(DownloadAndInstallApp(s,"droom.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	  System.out.println("Waiting for Droom Launch.....");
       	      if(s.exists(GetAppImagePath("Droom","droom_launch.png")) != null)
 	          {
       	    	 if(s.exists(GetAppImagePath("Droom","droom_launch_1.png")) != null)
       	    	 {
		       		  System.out.println("Droom Launched.....");
		       		  Thread.sleep(5000);
		       		  break;
       	    	 }
 	          }
       	      System.out.println("Droom not launched retrying again.....");
       	      Thread.sleep(3000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Droom launch not detected check logs..");
             return 0;
         }
         
         Thread.sleep(7000);
         //click done
         ExecuteCommand("adb  shell input tap 915 1883");
         Thread.sleep(10000);
         //click next
         ExecuteCommand("adb  shell input tap 37 144");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input tap 500 371");
         Thread.sleep(3000);
         
         //first name
         ExecuteCommand("adb  shell input text " + GetFirstName());
         Thread.sleep(3000);
         
         ////Enter middle name
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text " + GetMiddleName());
         Thread.sleep(3000);
       
         //Enter Last name
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text " + GetMiddleName());
         Thread.sleep(3000);
       
         //Handle
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text " + GenerateHandle());
         Thread.sleep(3000);

         //email
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         String email = GenerateRandomEmail();
         ExecuteCommand("adb  shell input text " + email);
         Thread.sleep(3000);
         
         //phone
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text " + GenerateMobileNo());
         Thread.sleep(3000);
       
         //password
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text 41s65a42tv");
         Thread.sleep(3000);
     
         //retype password
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text 41s65a42tv");
         Thread.sleep(3000);
         
         
         //pincode two tab required here
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB");
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input text 560069");
         Thread.sleep(3000);
                
         ExecuteCommand("adb  shell input tap 778 1322");
         Thread.sleep(2000);
         ExecuteCommand("adb  shell input tap 775 1694");
         Thread.sleep(2000);
         //swipe to go on bottom
         ExecuteCommand("adb  shell input swipe 300 700 300 100");
         Thread.sleep(3000);

         ExecuteCommand("adb  shell input tap 574 1857");//REGISTER
         Thread.sleep(8000);
         ExecuteCommand("adb  shell input tap 965 717");//CLOSE OTP POPUP
         Thread.sleep(3000);
         
         //Click Items now
         ExecuteCommand("adb  shell input tap 292 1210");
         Thread.sleep(3000);
         
         for(x = 0; x < 5; x++)
         {
        	 ExecuteCommand("adb  shell input swipe 500 900 500 100");
             Thread.sleep(2000);
         }
         
         ExecuteCommand("adb  shell input tap 945 742");//click one vehicle
         Thread.sleep(3000);
         ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");//GO BACK
         Thread.sleep(1000);
         ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");//GO BACK
         Thread.sleep(1000);
         ExecuteCommand("adb  shell input tap 945 742");//CLOSE OTP POPUP again
         Thread.sleep(2000);
         
         //Click another items
         ExecuteCommand("adb  shell input tap 790 1210");
         Thread.sleep(3000);
         
         for(x = 0; x < 5; x++)
         {
        	 ExecuteCommand("adb  shell input swipe 500 900 500 100");
             Thread.sleep(2000);
         }
         
         ExecuteCommand("adb  shell input tap 945 742");//click one vehicle
         UpdateAppInAccount("Droom",email,"41s65a42tv");
         Thread.sleep(10000);
         
		 return 1;
	 }
	 public static int InstallOrangeBookValue(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 if(s.exists(GetImagePath("OBV.png")) == null)
		 {
			 WriteLog("ERROR: App OBV not found on screen");
			 return 0;
		 }
       
     	 waitAndClick(s,GetImagePath("OBV.png"),1000);
     	 if(ErrorCode == 1) return 0;
     	 Thread.sleep(7000);

     	  waitAndClick(s,GetImagePath("download_now.png"),1000);
     	  if(ErrorCode == 1) return 0;
     	  
 		 int y = 0;
         for(y = 0; y < LAUNCH_ITERATION; y++)
         {
        	 System.out.println("Waiting for Install icon Launch.....");
        	  if(s.exists(GetImagePath("install.png")) != null)
 	          {
       	    	  System.out.println("Click Install.....");
       	    	  waitAndClick(s,GetImagePath("install.png"),1000);
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("Waiting for install button on playstore for obv.....");
       	      Thread.sleep(2000);
       	      
       	      if(y == 10 || y == 25)
       	      {
       	         ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
       	         Thread.sleep(5000);
            	 waitAndClick(s,GetImagePath("download_now.png"),1000);
             	 if(ErrorCode == 1) return 0;
       	      }
         }
         
         if(y == LAUNCH_ITERATION)
         {
             WriteLog("OBV Install button not found going back..");
             return 0;
         }
		 
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for OBV Launch.....");
        	  if(s.exists(GetAppImagePath("obv","obv_launch.png")) != null)
 	          {
       	    	  System.out.println("OBV Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("OBV not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("OBV launch not detected check logs..");
             return 0;
         }
         
         ExecuteCommand("adb shell input tap 603 1265");
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell input tap 603 1265");
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell input tap 603 1265");
         Thread.sleep(2000);
         
         waitAndClick(s,GetAppImagePath("obv","next.png"),1000);
         Thread.sleep(4000);
         waitAndClick(s,GetAppImagePath("obv","ok.png"),1000);
         Thread.sleep(4000);
         waitAndClick(s,GetAppImagePath("obv","ok.png"),1000);
         Thread.sleep(4000);
         ExecuteCommand("adb shell input tap 127 536");//individual
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell input tap 115 723");//select vehicle type
         Thread.sleep(7000);
         ExecuteCommand("adb shell input tap 137 580");//select vehicle
         Thread.sleep(7000);
         
         ExecuteCommand("adb shell input tap 180 921");//select make
         Thread.sleep(7000);
         ExecuteCommand("adb shell input tap 380 410");//select audi
         Thread.sleep(7000);
         
         ExecuteCommand("adb shell input tap 550 928");//select model
         Thread.sleep(7000);
         ExecuteCommand("adb shell input tap 365 600");//select model
         Thread.sleep(7000);
         
         for(int p = 0; p < 3; p++)
         {
        	 ExecuteCommand("adb shell input swipe 300 800 300 300");
             Thread.sleep(2000); 
         }
        
         ExecuteCommand("adb shell input tap 188 642");//selectyear
         Thread.sleep(7000);
         ExecuteCommand("adb shell input tap 366 903");//2012
         Thread.sleep(7000);
     
         ExecuteCommand("adb shell input tap 525 650");//select trim
         Thread.sleep(7000);
         ExecuteCommand("adb shell input tap 366 667");//2.0 tdi
         Thread.sleep(7000);

         ExecuteCommand("adb shell input tap 175 837");//select km
         Thread.sleep(2000);
        
         ExecuteCommand("adb shell input text 7467");//enter km
         Thread.sleep(2000);
         
         ExecuteCommand("adb shell input keyevent 111");//enter km
         Thread.sleep(2000);
         
         for(int p = 0; p < 1; p++)
         {
        	 ExecuteCommand("adb shell input swipe 300 800 300 300");
             Thread.sleep(2000); 
         }
         
      
         ExecuteCommand("adb shell input tap 424 915");
         Thread.sleep(7000);
         
         ExecuteCommand("adb shell input tap 191 504");
         Thread.sleep(2000);
         ExecuteCommand("adb shell input text " + GetFirstName());
         Thread.sleep(2000);
         ExecuteCommand("adb shell input keyevent KEYCODE_TAB ");
         Thread.sleep(2000);
         ExecuteCommand("adb shell input text " + GenerateRandomEmail());
         Thread.sleep(2000);
         ExecuteCommand("adb shell input keyevent KEYCODE_TAB ");
         Thread.sleep(2000);
         ExecuteCommand("adb shell input text " + GenerateMobileNo());
         Thread.sleep(2000);
         ExecuteCommand("adb shell input keyevent 111");
         Thread.sleep(2000);
         ExecuteCommand("adb shell input tap 520 1003");//click submit
         Thread.sleep(2000);
       
         Thread.sleep(10000);
         
		 return 1;
	 }
	 public static int Installbuyhatke(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 DownloadAndInstallApp(s,"buyhatke.png");
		
         Thread.sleep(10000);
         
         ExecuteCommand("adb  shell input swipe 610 600 600 1200");
 	     Thread.sleep(10000);
 	     
         ExecuteCommand("adb  shell input swipe 610 600 600 1200");
 	     Thread.sleep(10000);
 	     
         ExecuteCommand("adb  shell input swipe 610 600 600 1200");
 	     Thread.sleep(10000);
 	     
         ExecuteCommand("adb  shell input swipe 610 600 600 1200");
 	     Thread.sleep(20000);
         
		 return 1;
	 }
	 
	 public static int InstallSkype(Screen s) throws InterruptedException, FindFailed, IOException
	 {
         
		 if(DownloadAndInstallApp(s,"skype.png") == false)
		 {
			 return 0;
		 }
		 	 
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for Skype Launch.....");
        	  if(s.exists(GetAppImagePath("skype","skype_launch.png")) != null)
 	          {
       	    	  System.out.println("Skype Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("Skype not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Skype launch not detected check logs..");
             return 1;
         }
         
         GivePermissions("com.skype.m2");
         
         Thread.sleep(5000);

         waitAndClick(s,GetAppImagePath("skype","skype_launched.png"),1000);
         Thread.sleep(3000);
         waitForImage(s,GetAppImagePath("skype","next.png"),1000);
         Thread.sleep(3000);
		 ExecuteCommand("adb shell input tap 257 395");
		 Thread.sleep(3000);
		 
		 ExecuteCommand("adb shell input tap 257 395");
		 Thread.sleep(3000);
		 
		 ExecuteCommand("adb shell input text 9164842851");
		 Thread.sleep(3000);
         waitAndClick(s,GetAppImagePath("skype","next.png"),1000);
         Thread.sleep(3000);
         waitForImage(s,GetAppImagePath("skype","signin.png"),1000);
         Thread.sleep(3000);
         
		 ExecuteCommand("adb shell input tap 245 485");
		 Thread.sleep(3000);
		 
		 ExecuteCommand("adb shell input text 41s65a42tv");
		 Thread.sleep(3000);
		 
         waitAndClick(s,GetAppImagePath("skype","signin.png"),1000);
         Thread.sleep(3000);
         
         waitAndClick(s,GetAppImagePath("skype","ignore.png"),1000);
         Thread.sleep(3000);
		 
		 return 1;
	 }
	 public static int Installqpay(Screen s) throws InterruptedException, FindFailed, IOException
	 {
         
		 if(DownloadAndInstallApp(s,"qpay.png") == false)
		 {
			 return 0;
		 }
		 	 
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for qpay Launch.....");
        	  if(s.exists(GetAppImagePath("qpay","qpay_launch.png")) != null)
 	          {
       	    	  System.out.println("qpay Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("qpay not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("qpay launch not detected check logs..");
             return 1;
         }
         
         Thread.sleep(15000);

         
		 return 1;
	 }
	 public static int Install1mg(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(DownloadAndInstallApp(s,"1mg.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for 1mg Launch.....");
        	  if(s.exists(GetAppImagePath("1mg","1mg_launched.png")) != null)
 	          {
       	    	  System.out.println("1mg Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("1mg not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("1mg launch not detected check logs..");
             return 0;
         }
         
		 ExecuteCommand("adb shell pm grant com.aranoah.healthkart.plus android.permission.ACCESS_FINE_LOCATION");
		 Thread.sleep(3000);
         
         waitAndClick(s,GetAppImagePath("1mg","1mg_launched.png"),1000);
         Thread.sleep(7000);
         waitAndClick(s,GetAppImagePath("1mg","1mg_later.png"),1000);
         Thread.sleep(15000);
         
         if(ErrorCode == 1)
         {
             ExecuteCommand("adb shell input tap 650 110");
             Thread.sleep(15000);
         }
         
         ExecuteCommand("adb shell input tap 465 431");
         Thread.sleep(8000);
         
         System.out.println("Click one article.....");
         ExecuteCommand("adb shell input tap 167 1077");//click one article
         Thread.sleep(4000);
         
         System.out.println("Click one article page.....");
         ExecuteCommand("adb shell input tap 560 1076");//click one article
         Thread.sleep(4000);
         
         System.out.println("Click on share it.....");
         
         for(int i = 0; i < 2; i++)
         {
             ExecuteCommand("adb shell input tap 655 107");
             Thread.sleep(4000);
             for(int y=0; y < 30; y++)
             {
            	 if(s.exists(GetAppImagePath("1mg","whatsapp.png")) != null)
            	 {
            		 s.click(GetAppImagePath("1mg","whatsapp.png")); 
            		 Thread.sleep(3000);
                     ExecuteCommand("adb shell input tap 310 303");
                     Thread.sleep(3000);
                     ExecuteCommand("adb shell input tap 620 1193");
                     Thread.sleep(3000);
                     ExecuteCommand("adb shell input tap 665 1200");
                     Thread.sleep(3000);
                     ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
                     Thread.sleep(3000);
                     ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
                     Thread.sleep(3000);
                     break;
            	 }
            	 Thread.sleep(1000);
            	 
            	 if(y == 10 || y == 20)
            	 {
                     ExecuteCommand("adb shell input tap 655 107");
                     Thread.sleep(4000);
            	 }
            		
             }
         }
        
         System.out.println("1 mg done.....");
         Thread.sleep(10000);
         
		 return 1;
	 }
	 
	 
	 public static int InstallSportsFlashes(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(DownloadAndInstallApp(s,"sports.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for Sports Flashes Launch.....");
        	  if(s.exists(GetAppImagePath("sports","sports_launched.png")) != null)
 	          {
       	    	  System.out.println("Sports Flashes Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("Sports Flashes not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Sports Flashes launch not detected check logs..");
             return 0;
         }
         waitAndClick(s,GetAppImagePath("sports","sports_launched.png"),1000);
         Thread.sleep(10000);
         ExecuteCommand("adb shell input tap 365 650");
         Thread.sleep(5000);
         System.out.println("Sports Flashes done.....");
         Thread.sleep(10000);
         
		 return 1;
	 }
	 public static int InstallWealthDoctor(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(DownloadAndInstallApp(s,"wealthdoctor.png") == false)
		 {
			 return 0;
		 }
		 int x = 0;
         for(x = 0; x < LAUNCH_ITERATION; x++)
         {
        	 System.out.println("Waiting for Wealth Doctor Launch.....");
        	  if(s.exists(GetAppImagePath("wealthdoctor","wealthdoctor_launched.png")) != null)
 	          {
       	    	  System.out.println("Wealth Doctor Launched.....");
	       		  Thread.sleep(5000);
	       		  break;
 	          }
       	      System.out.println("WealthDcotr not launched retrying again.....");
       	      Thread.sleep(2000);
         }
         
         if(x == LAUNCH_ITERATION)
         {
             WriteLog("Wealth Doctor launch not detected check logs..");
             return 0;
         }
      
         
         ExecuteCommand("adb shell input swipe 450 900 450 300");
         Thread.sleep(5000);
         
         ExecuteCommand("adb shell input swipe 450 900 450 300");
         Thread.sleep(5000);

         ExecuteCommand("adb shell input tap 366 750");
         Thread.sleep(4000);
         
         ExecuteCommand("adb shell input text 7259235334");
         Thread.sleep(4000);
    	 waitAndClick(s,GetAppImagePath("wealthdoctor","next.png"),1000);
    	 Thread.sleep(4000);
    	 waitAndClick(s,GetAppImagePath("wealthdoctor","allow.png"),1000);
    	 Thread.sleep(7000);
         System.out.println("Wealth Doctor done.....");
         Thread.sleep(10000);
         
		 return 1;
	 }
	 public static int Installwyncgames(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(s.exists(GetImagePath("wyncgames.png")) == null)
		 {
			 WriteLog("ERROR: DownloadAndInstallApp " + " WyncGames " + " APP NOT EXIST ON ETT SCREEN RETURN FALSE");
			 return 0;
		 }
       
     	  waitAndClick(s,GetImagePath("wyncgames.png"),1000);
     	  if(ErrorCode == 1) return 0;
     	 Thread.sleep(5000);
     	  waitAndClick(s,GetImagePath("download_now.png"),1000);
     	  if(ErrorCode == 1)
     	  {
     		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
       		  Thread.sleep(5000);
       		  waitAndClick(s,GetImagePath("download_now.png"),1000);
       		  if(ErrorCode == 1)
        	  {
         		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
           		  Thread.sleep(5000);
           		  waitAndClick(s,GetImagePath("download_now.png"),1000);
           		  if(ErrorCode == 1)
            	  {
           			 WriteLog("WyncGames" + " ERROR: waitAndClick download_now failed after retry");
           			 return 0;
            	  }  
        	  }      		
     	  }
     	  Thread.sleep(10000);
     	  System.out.println("download_now done....");
     	  
     	  //satveer
     	  if(DownloadFromBrowser(s) == 0) return 0;
      	  
      	  System.out.println("App open done going for install....");
      	  Thread.sleep(4000);
     	  waitAndClick(s,GetAppImagePath("wyncgames","install.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 return 0;
          }

         waitAndClick(s,GetAppImagePath("wyncgames","allow.png"),1000);
     	 Thread.sleep(30000);
     	 ExecuteCommand("adb shell input tap 649 107");
         Thread.sleep(6000);
         
         /*
         for(int i = 0; i < 40; i++)
         {
        	 if(s.exists(GetAppImagePath("wyncgames","ad.png")) != null)
        	 {
        		 Thread.sleep(3000);
        		 ExecuteCommand("adb  shell input tap 680 75");
         	     Thread.sleep(3000);
         	     break;
        	 }
        	 Thread.sleep(1000);
         }
         */
         ExecuteCommand("adb  shell input swipe 300 900 300 300");
 	     Thread.sleep(3000);
         ExecuteCommand("adb  shell input tap 575 950");
 	     Thread.sleep(3000);
		 
   	     Thread.sleep(3000);
	     waitAndClick(s,GetAppImagePath("wyncgames","clickfree.png"),1000);
   	     if(ErrorCode == 1) 
   	     {
   		     return 0;
         }
   	     Thread.sleep(3000);
         
   	     if(MOBILE_NO == "9900525388" || MOBILE_NO == "9164842851")
   	     {
   	        ExecuteCommand("adb  shell input tap 327 786");
   	        Thread.sleep(3000);
   	        if(MOBILE_NO == "9900525388")
   	        {
   	           ExecuteCommand("adb  shell input text 9900525388");
   	        }
   	        else
   	        {
   	        	ExecuteCommand("adb  shell input text 919164842851");
   	        }
   	        Thread.sleep(3000);
   	     }

		 waitAndClick(s,GetAppImagePath("wyncgames","confirm.png"),1000);
		 Thread.sleep(2000);
		// waitAndClick(s,GetAppImagePath("wyncgames","allow.png"),1000);
		// Thread.sleep(6000);
		 waitAndClick(s,GetAppImagePath("wyncgames","install.png"),1000);
     	  Thread.sleep(16000);
     	  
      	  waitAndClick(s,GetAppImagePath("wyncgames","smallapp_open.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 return 0;
          }
         
         System.out.println("wync done.....");
         Thread.sleep(10000);
         
		 return 1;
		 
	 }
	 public static int InstallDream11(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(s.exists(GetImagePath("dream11.png")) == null)
		 {
			 WriteLog("ERROR: DownloadAndInstallApp " + " Dream11 " + " APP NOT EXIST ON ETT SCREEN RETURN FALSE");
			 return 0;
		 }
       
     	  waitAndClick(s,GetImagePath("dream11.png"),1000);
     	  if(ErrorCode == 1) return 0;
     	 Thread.sleep(5000);
     	  waitAndClick(s,GetImagePath("download_now.png"),1000);
     	  if(ErrorCode == 1)
     	  {
     		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
       		  Thread.sleep(5000);
       		  waitAndClick(s,GetImagePath("download_now.png"),1000);
       		  if(ErrorCode == 1)
        	  {
         		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
           		  Thread.sleep(5000);
           		  waitAndClick(s,GetImagePath("download_now.png"),1000);
           		  if(ErrorCode == 1)
            	  {
           			 WriteLog("WyncGames" + " ERROR: waitAndClick download_now failed after retry");
           			 return 0;
            	  }  
        	  }      		
     	  }
     	   Thread.sleep(10000);
     	  if(s.exists(GetImagePath("redirectapp.png")) != null)
     	  {
     		  return 0;
     	  }
     	  System.out.println("download_now done....");
      	
     	  //satveer
     	  if(DownloadFromBrowser(s) == 0) return 0;
     	  
      	  System.out.println("App open done going for install....");
      	  Thread.sleep(4000);
     	  waitAndClick(s,GetAppImagePath("dream11","install.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 return 0;
          }

     	 System.out.println("Long sleep now....");
     	 Thread.sleep(40000);
     	 
     	  waitAndClick(s,GetAppImagePath("dream11","register.png"),1000);
     	 Thread.sleep(8000);
         
		  ExecuteCommand("adb shell input tap 168 537");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input text sudeep");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 228 630");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input text " + GenerateRandomEmail());
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
   		  Thread.sleep(3000);
   		
		  ExecuteCommand("adb shell input tap 224 755");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input text 41s65a42");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 365 890");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 386 898");
   		  Thread.sleep(3000);
   		  
   		  //CLICK TEAM
		  ExecuteCommand("adb shell input tap 366 572");
   		  Thread.sleep(3000);
   		  
   		  //CREATETEAM
		  ExecuteCommand("adb shell input tap 380 989");
   		  Thread.sleep(3000);
   		  
   		  //SELECT WK
		  ExecuteCommand("adb shell input tap 370 750");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 275 356");
   		  Thread.sleep(3000);
   		  

   		  
   		  //batsman 4
   		  
		  ExecuteCommand("adb shell input swipe 300 800 300 100");
   		  Thread.sleep(3000);
   		  
   		  
		  ExecuteCommand("adb shell input tap 363 1023");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 349 884");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 287 756");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 374 635");
   		  Thread.sleep(3000);
   		  
   		  //allrounder
   		  
		  ExecuteCommand("adb shell input tap 424 362");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 385 1009");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 380 885");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 355 760");
   		  Thread.sleep(3000);
   		  
   		  //Bowl
   		  
		  ExecuteCommand("adb shell input tap 608 364");
   		  Thread.sleep(3000);
   		  
		  ExecuteCommand("adb shell input tap 351 1020");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 365 880");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 375 750");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 345 620");
   		  Thread.sleep(3000);
   		  
   		  //next
		  ExecuteCommand("adb shell input tap 360 1210");
   		  Thread.sleep(3000);
   		  
   		  //cap wc
		  ExecuteCommand("adb shell input tap 530 672");
   		  Thread.sleep(3000);
		  ExecuteCommand("adb shell input tap 630 803");
   		  Thread.sleep(7000);
		  ExecuteCommand("adb shell input tap 380 1216");
   		  Thread.sleep(7000);
		  ExecuteCommand("adb shell input tap 365 1220");
   		  Thread.sleep(7000);
		  ExecuteCommand("adb shell input tap 340 680");
   		  Thread.sleep(7000);
		  ExecuteCommand("adb shell input tap 328 680");
   		  Thread.sleep(7000);
   		  
		  ExecuteCommand("adb shell input tap 353 695");
   		  Thread.sleep(7000);
   		  
		  ExecuteCommand("adb shell input swipe 300 800 300 100");
   		  Thread.sleep(7000);
   		  
		  ExecuteCommand("adb shell input tap 360 545");
   		  Thread.sleep(7000);
		
         System.out.println("dream11 done.....");
         Thread.sleep(10000);
         
		 return 1;
		 
	 }
	 public static int InstallAce2Three(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 
		 if(s.exists(GetImagePath("ace2three.png")) == null)
		 {
			 WriteLog("ERROR: DownloadAndInstallApp " + " Ace2Three " + " APP NOT EXIST ON ETT SCREEN RETURN FALSE");
			 return 0;
		 }
       
     	  waitAndClick(s,GetImagePath("ace2three.png"),1000);
     	  if(ErrorCode == 1) return 0;
     	 Thread.sleep(5000);
     	  waitAndClick(s,GetImagePath("download_now.png"),1000);
     	  if(ErrorCode == 1)
     	  {
     		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
       		  Thread.sleep(5000);
       		  waitAndClick(s,GetImagePath("download_now.png"),1000);
       		  if(ErrorCode == 1)
        	  {
         		  ExecuteCommand("adb shell input keyevent KEYCODE_BACK");
           		  Thread.sleep(5000);
           		  waitAndClick(s,GetImagePath("download_now.png"),1000);
           		  if(ErrorCode == 1)
            	  {
           			 WriteLog("Ace2Three" + " ERROR: waitAndClick download_now failed after retry");
            	     return 0;
            	  }  
        	  }      		
     	  }
     	  
     	Thread.sleep(15000);
     	
   	    if(s.exists(GetImagePath("redirectapp.png")) != null)
   	    {
   		  return 0;
   	    }
     	  
 	     waitAndClick(s,GetAppImagePath("ace2three","download_play.png"),1000);
   	     if(ErrorCode == 1) 
   	     {
   		     return 0;
         }

    	  //satveer
    	  if(DownloadFromBrowser(s) == 0) return 0;
      	  
     	  waitAndClick(s,GetAppImagePath("ace2three","install.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 return 0;
          }

     	  waitAndClick(s,GetAppImagePath("ace2three","ace2three_open.png"),1000);
     	  if(ErrorCode == 1) 
     	  {
     		 return 0;
          }
     	  
     	  
     	 System.out.println("Long now....");
     	 Thread.sleep(20000);
     	
         ExecuteCommand("adb  shell input text " + GenerateHandle());
 	     Thread.sleep(3000);
 	     
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB ");
 	     Thread.sleep(3000);
 	     
         ExecuteCommand("adb  shell input text " + GenerateMobileNo());
 	     Thread.sleep(3000);
 	     
         ExecuteCommand("adb  shell input keyevent KEYCODE_TAB ");
 	     Thread.sleep(3000);
 	     
         ExecuteCommand("adb  shell input text 41s65a42tv");
 	     Thread.sleep(3000);
 	     
         ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");
 	     Thread.sleep(3000);
 	     
    	  waitAndClick(s,GetAppImagePath("ace2three","signup.png"),1000);
    	  if(ErrorCode == 1) 
    	  {
    		 return 0;
         }
    	 
    	  Thread.sleep(20000);
    	  
    	  waitAndClick(s,GetAppImagePath("ace2three","sendotp.png"),1000);
    	  if(ErrorCode == 1) 
    	  {
    		 return 0;
         }
    	  
         System.out.println("Ace2Three done.....");
         Thread.sleep(20000);
         
		 return 1;
	 }
	 public static int InstallApps(Screen s,String appName)  throws InterruptedException, FindFailed, IOException
	 {
		 if(appName == "amazon")
		 {
			 return InstallAmazon(s);
		 }
		 
		 if(appName == "dekho")
		 {
			 return InstallDekho(s);
		 }
		 
		 if(appName == "myvodafone")
		 {
			 return InstallMyVodafone(s);
		 }
		 
		 if(appName == "droom")
		 {
			 return InstallDroom(s);
		 }
		 
		 
		 if(appName == "obv")
		 {
			 return InstallOrangeBookValue(s);
		 }		
		 
		 if(appName == "wyncgames")
		 {
			 return Installwyncgames(s);
		 }
		 
		 if(appName == "dream11")
		 {
			 return InstallDream11(s);
		 }
		 
		 if(appName == "1mg")
		 {
			 return Install1mg(s);
		 }
		 
		 if(appName == "ace2three")
		 {
			 return InstallAce2Three(s);
		 }
		 
		 if(appName == "qpay")
		 {
			 return Installqpay(s);
		 }
		 
		 if(appName == "buyhatke")
		 {
			 return Installbuyhatke(s);
		 }
		 
		 if(appName == "wealthdoctor")
		 {
			 return InstallWealthDoctor(s);
		 }
		 
		 if(appName == "sports")
		 {
			 return InstallSportsFlashes(s);
		 }
		 return 0;
	 }
	 static boolean Vysor_Restart(Screen s) throws InterruptedException
	 {
		 System.out.println("Vysor restart called ....");
		 
		 if(s.exists(GetImagePath("SCREEN_TOP.png")) != null)
		 {
			 System.out.println("Vysor restart called screen top found....");
			 ExecuteCommand("adb kill-server");
	 		 Thread.sleep(1000*30);
			 ExecuteCommand("adb devices");
	 		 Thread.sleep(1000*30);
        	  ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
	              Thread.sleep(3000);
    		 if(s.exists(GetImagePath("homefound.png")) != null)
    		 {
    			 return true;
    		 }
			 
		 }
		 return false;
	 }
     public static int CheckAndInstallApp(Screen s,String appName)  throws InterruptedException, FindFailed, IOException
	 {
		 int flag = 0;
	     System.out.println("Searching App " + appName);
	     int swapCount = 9;
	   
	     if(amazon2)
		 {
		    	 ExecuteCommand("adb shell input swipe 300 800 300 400");
		    	 Thread.sleep(3000);
		 }
		     
		 if(appName == "amazon")
		  swapCount = 3;
		    
		 if(appName == "cleartrip")
		   swapCount = 4;
		     
		 for(int x = 0; x < swapCount; x++)
		 {
		    	  if(s.exists(GetImagePath(appName + ".png")) != null)
		    	  {
		    		  WriteLog(appName + " Found on ett screen");
		    	      break;
		    	  }
		    	    	 
		    	   ExecuteCommand("adb  shell input swipe 300 800 300 250");
		   	       Thread.sleep(5000);
		 }
		     
		 if(s.exists(GetImagePath(appName + ".png")) != null)
		 {
			    System.out.println("Install App " + appName);
			    flag = InstallApps(s,appName);	
		 }
		 else
		 {
			    	WriteLog("ERROR::" + appName + " App not Found on Screen");
		 }
	  
	     ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
   	     Thread.sleep(3000);
		 ExecuteCommand("adb shell am force-stop info.earntalktime");
         Thread.sleep(6000);
         LaunchApp("info.earntalktime");
         Thread.sleep(6000); 
         if(WaitForEttPageLoad(s) == false)
         {
        	 if(Vysor_Restart(s) == true)
        	 {
        	     ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
           	     Thread.sleep(3000);
        		 ExecuteCommand("adb shell am force-stop info.earntalktime");
                 Thread.sleep(6000);
                 LaunchApp("info.earntalktime");
                 Thread.sleep(6000); 
                 WaitForEttPageLoad(s);
        	 }
         }
   	     ExecuteCommand("adb  shell input swipe 600 1300 600 1100");
   	     Thread.sleep(7000); 
	     return flag;
	 }
	 static int GetAppFlagFromFile(String str) throws IOException
	 {
	         FileInputStream fstream = new FileInputStream(GetAutomationFolderPath() + "Input\\app_flag.txt");
	         DataInputStream in = new DataInputStream(fstream);
	         BufferedReader br = new BufferedReader(new InputStreamReader(in));
		     String line = null;

		     while ((line = br.readLine()) != null)
   	         {

		    	 if(line.contains(str) == true) break;
   	         }
		     br.close();
		     if(line != null)
		     {
		    	 if(line.contains("0") == true) return 0;
		    	 if(line.contains("1") == true) return 1;
		     }
		    
		     return 0;
	 } 
	 public static void ProcessApps(Screen s) throws InterruptedException, FindFailed, IOException
	 {
		 int countApps = 0;
		 int result = 0;
		 
		 System.out.println("Going for Process Apps");
		 
	    if(GetAppFlagFromFile("fdream11") == 1)
	    {
	       	    WriteLog("Installing Dream11....");
	       	    result = CheckAndInstallApp(s,"dream11");
	       	    WriteLog("Dream11  = " + result);
	       	    countApps = countApps + result;
	       	    WriteLog("Installing Dream11 Done");
	    }
		 if(GetAppFlagFromFile("fsports") == 1 && (rd[fromIndex].set.contains("sports") == false))
		 {
    		 WriteLog("Installing SportsFlashes....");
        	 result =  CheckAndInstallApp(s,"sports");
        	 WriteLog("SportsFlashes = " + result);
        	 countApps = countApps + result;
        	 WriteLog("Installing SportsFlashes Done");
	    }
		 		 
	    if(GetAppFlagFromFile("famazon") == 1 && (rd[fromIndex].set.contains("amazon") == false))
	    {
	    		 WriteLog("Installing Amazon....");
	        	 result =  CheckAndInstallApp(s,"amazon");
	        	 WriteLog("Amazon = " + result);
	        	 countApps = countApps + result;
	        	 WriteLog("Installing Amazon Done");
	    }
	    
	    if(GetAppFlagFromFile("fskype") == 1 && (rd[fromIndex].set.contains("skype") == false))
	    {
	    		 WriteLog("Installing SkypeLite....");
	        	 result =  CheckAndInstallApp(s,"skype");
	        	 WriteLog("Skype = " + result);
	        	 countApps = countApps + result;
	        	 WriteLog("Installing Skype Done");
	    }
	    
        if(GetAppFlagFromFile("fdekho") == 1 && (rd[fromIndex].set.contains("dekho") == false))
        {
       	 WriteLog("Installing Dekho....");
       	 result = CheckAndInstallApp(s,"dekho");
       	 WriteLog(" Dekho  = " + result);
       	 countApps = countApps + result;
       	 WriteLog("Installing Dekho Done");
        }
        
        if(GetAppFlagFromFile("fdroom") == 1 && (rd[fromIndex].set.contains("droom") == false))
        {
       	 WriteLog("Installing Droom....");
       	 result = CheckAndInstallApp(s,"droom");
       	 WriteLog("Droom = " + result);
       	 countApps = countApps + result;
       	 WriteLog("Installing Droom Done");
        }
	 
        if(GetAppFlagFromFile("fobv") == 1 && (rd[fromIndex].set.contains("obv") == false))
        {
		     WriteLog("Installing OBV....");
		     result = CheckAndInstallApp(s,"obv");
		     WriteLog("OBV  = " + result);
		     countApps = countApps + result;
		     WriteLog("Installing OBV Done");
        }
	      
        if(GetAppFlagFromFile("f1mg") == 1 && (rd[fromIndex].set.contains("1mg") == false))
        {
       	    WriteLog("Installing 1mg....");
       	    result = CheckAndInstallApp(s,"1mg");
       	    WriteLog("1mg  = " + result);
       	    countApps = countApps + result;
       	    WriteLog("Installing 1mg Done");
        }
        
 
		        
        if(GetAppFlagFromFile("face2three") == 1 && (rd[fromIndex].set.contains("ace2three") == false))
        {
       	    WriteLog("Installing Ace2Three....");
       	    result = CheckAndInstallApp(s,"ace2three");
       	    WriteLog("Ace2Three  = " + result);
       	    countApps = countApps + result;
       	    WriteLog("Installing Ace2Three Done");
        }

        if(GetAppFlagFromFile("fwealthdoctor") == 1)
        {
       	    WriteLog("Installing WelathDoctor....");
       	    result = CheckAndInstallApp(s,"wealthdoctor");
       	    WriteLog("WealthDoctor  = " + result);
       	    countApps = countApps + result;
       	    WriteLog("Installing WealthDoctor Done");
        }
        
        if(GetAppFlagFromFile("fbuyhatke") == 1 && (rd[fromIndex].set.contains("buyhatke") == false))
        {
            LaunchApp("com.android.chrome");
            
            ExecuteCommand("adb  shell input tap 675 90");
    	     Thread.sleep(5000);
    	     
            ExecuteCommand("adb  shell input tap 285 575");
    	     Thread.sleep(5000);

     	    for(int x = 0; x < 7; x++)
     	    {
     	    	if(s.exists(GetImagePath("clear_browsing_data.png")) != null)
     	    	{
     	    		s.click(GetImagePath("clear_browsing_data.png"));
     	    		Thread.sleep(1000);
     	    		break;
     	    	}
     	    	Thread.sleep(1000);
     	    }
     	    Thread.sleep(3000);
     	    for(int x = 0; x < 7; x++)
     	    {
     	    	if(s.exists(GetImagePath("clear_data.png")) != null)
     	    	{
     	    		s.click(GetImagePath("clear_data.png"));
     	    		Thread.sleep(1000);
     	    		break;
     	    	}
     	    	Thread.sleep(1000);
     	    }
     	    Thread.sleep(3000);
     	    for(int x = 0; x < 7; x++)
     	    {
     	    	if(s.exists(GetImagePath("browser_clear.png")) != null)
     	    	{
     	    		s.click(GetImagePath("browser_clear.png"));
     	    		Thread.sleep(1000);
     	    		break;
     	    	}
     	    	Thread.sleep(1000);
     	    }
     	    
            ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
    	    Thread.sleep(5000);
    	    
    	    LaunchApp("info.earntalktime");
    	    Thread.sleep(5000);
    	    WaitForEttPageLoad(s);
    	    Thread.sleep(5000);
       	    WriteLog("Installing Buyhatke....");
       	    result = CheckAndInstallApp(s,"buyhatke");
       	    WriteLog("Buyhatke  = " + result);
       	    countApps = countApps + result;
       	    WriteLog("Installing Buyhatke Done");
        }
        
        if(GetAppFlagFromFile("fwyncgames") == 1 && (rd[fromIndex].set.contains("wyncgames1") == false || rd[fromIndex].set.contains("wyncgames2") == false))
        {
       	    WriteLog("Installing WyncGames....");
       	    result = CheckAndInstallApp(s,"wyncgames");
       	    WriteLog("wyncgames  = " + result);
       	    countApps = countApps + result;
       	    WriteLog("Installing WyncGames Done");
        }
        
        if(GetAppFlagFromFile("fmyvodafone") == 1 && (rd[fromIndex].set.contains("myvodafone") == false))
        { 
       	 	WriteLog("Installing myvodafone....");
       	 	result = CheckAndInstallApp(s,"myvodafone");
       	 	WriteLog("myvodafone = " + result);
       	 	countApps = countApps + result;
       	 	WriteLog("Installing myvodafone Done");
        }
        
		
		 if(GetAppFlagFromFile("fqpay") == 1 && (rd[fromIndex].set.contains("qpay") == false))
		 {
			 ExecuteCommand("adb shell am force-stop info.earntalktime");
	         Thread.sleep(2000);
	         LaunchApp("info.earntalktime");
	         
	         for(int x = 0; x < 20; x++)
	         {
	        	 if(s.exists(GetImagePath("qpay.png")) != null)
	        	 {
	        		 s.click(GetImagePath("qpay.png"));
	        		 break;
	        	 }
	        	 Thread.sleep(1000);
	         }
	         waitAndClick(s,GetImagePath("download_now.png"),1000);
    		 Thread.sleep(3000);
    		 waitAndClick(s,GetImagePath("install.png"),1000);
    		 Thread.sleep(3000);
	    }

        WriteLog("installed apps = " + countApps);
	 }
	 static boolean WaitForEttPage(Screen s, int count ) throws InterruptedException, IOException, FindFailed
	 {
		 ErrorCode = 0;
         for(int i = 0; i < count; i++)
         {
	       	  if(s.exists(GetImagePath("LOGIN_DONE.png")) != null)
	       	  {
	       		  Thread.sleep(3000);
	       		  System.out.println("ETT Page Load Done Checking Popup");
		          if(s.exists(GetImagePath("popup_close.png")) != null)
		       	  {
		       		waitAndClick(s,GetImagePath("popup_close.png"),1000);
		       		Thread.sleep(3000);
		       		continue;
	       	      }
		          else
		          {
		        	  return true;
		          }
	       	  }
	       	  System.out.println("Still Waiting for ETT Page Load...");
	       	 Thread.sleep(3000);
         }
		 ExecuteCommand("adb shell am force-stop info.earntalktime");
         Thread.sleep(2000);
         LaunchApp("info.earntalktime");
         Thread.sleep(5000);
         return false;
	 }
	 static boolean WaitForEttPageLoad(Screen s ) throws InterruptedException, IOException, FindFailed
	 {
		 ErrorCode = 0;
         if(WaitForEttPage(s,5))
         {
        	 return true;
         }
     
         if(WaitForEttPage(s,15))
         {
        	 return true;
         }
         if(semiautomode)
         {
	         System.out.println("Screen blank or vysor closed. Please open ett page and type n");
	         playSound();
	         WaitForUserInput();
	         return true;
         }
         return false;
	 }
	 
	 static void LogErrorCode() throws IOException
	 {
 		  WriteLog("ErrorCode = 1 Going for next ID");
 		  bs.flush();
 		  bs.close();
 		  bs = null;
	 }
	 static void GoToTranscationPage(Screen s) throws IOException, InterruptedException, FindFailed
	 {
 	      ExecuteCommand("adb  shell input tap 22 98");
          Thread.sleep(3000);
 	      ExecuteCommand("adb  shell input tap 250 450");
          Thread.sleep(5000);
	 }
	 static void ChangeDeviceID() throws IOException, InterruptedException
	 {
		  for(int c = 0; c < 2; c++)
		  {
			  ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
			  Thread.sleep(3000);
			  LaunchApp("com.device.emulator.pro");
			  Thread.sleep(5000);
	 	      ExecuteCommand("adb  shell input tap 393 100");
	          Thread.sleep(3000);
         } 
		  
		  
 	      ExecuteCommand("adb  shell input tap 580 102");
          Thread.sleep(3000);
		  
 	      ExecuteCommand("adb  shell input tap 567 751");
          Thread.sleep(3000);
          
         Thread.sleep(1000*70);
 		 ExecuteCommand("adb kill-server");
 		 Thread.sleep(1000*50);
          System.out.println("150 sec wait is over now");
        
          ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
          ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
		  
	 }
	 
	 static void RebootAndWakeup(Screen s) throws IOException, InterruptedException, FindFailed
	 {
         System.out.println("Going for adb reboot");
         ExecuteCommand("adb reboot");
         System.gc();
         Thread.sleep(1000*30);
		 ExecuteCommand("adb kill-server");
		 Thread.sleep(1000*10);
         System.out.println("150 sec wait is over now");
       
         ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
         ExecuteCommand("adb shell input keyevent KEYCODE_HOME");
 
	 }
	 
	 static void RotateScreenOff() throws IOException, InterruptedException
	 {
         ExecuteCommand("adb shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0"); 
         Thread.sleep(3000);
	 }
	 
	 static void GivePermissions(String str) throws InterruptedException
	 {
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.RECEIVE_SMS");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.READ_CONTACTS");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.WRITE_EXTERNAL_STORAGE");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.RECORD_AUDIO");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.CAMERA");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.ACCESS_FINE_LOCATION");
		 Thread.sleep(1000);
		 ExecuteCommand("adb shell pm grant "+str+" android.permission.CALL_PHONE");
		 Thread.sleep(1000);
		
	 }
	 
	 static void WaitForUserInput() throws InterruptedException
	 {
		 while(true)
		 {

		        Scanner userInput = new Scanner(System.in);
		        String input = "";
		        if (userInput.hasNext()) input = userInput.nextLine();
		        if (input.equals("n") || input.equals("N"))
		        {
		            break;
		        }
		        userInput.close();
		        Thread.sleep(3000);
		    }
	 }
	 
	 public static void CloseVysor() throws FindFailed, InterruptedException, IOException
	 {
		 Screen s = new Screen();
		 s.w = s.w/2;
		 waitAndClick(s,GetImagePath("screen_close.png"),1000);
		 Thread.sleep(3000);
		 System.out.println("Closing Vysor....");
	 } 
     public static void FillOTP(int index) throws IOException
     {
     	 int index1 = index;
	     FileInputStream tfstream = new FileInputStream("C:\\Users\\mechu\\Desktop\\Automation\\eAppRedmi4A\\Input\\temp.txt");
	     DataInputStream tin = new DataInputStream(tfstream);
	     BufferedReader tbr = new BufferedReader(new InputStreamReader(tin));

	     BufferedWriter bsx = new BufferedWriter(new FileWriter("C:\\Users\\mechu\\Desktop\\Automation\\eAppRedmi4A\\Input\\temp_out.txt"));
	     String xbrLine = null;
	     while ((xbrLine = tbr.readLine()) != null)
	     {
	    	 xbrLine.replaceAll("\\s+","");
	    	 bsx.write(index1+"-");
	    	 xbrLine = xbrLine.substring(0,10);
	    	 bsx.write(xbrLine+"-");
	    	 
	    	 xbrLine = tbr.readLine();
	    	 xbrLine.replaceAll("\\s+","");
	    	 xbrLine = xbrLine.substring(0,6);
	    	 bsx.write(xbrLine);
	    	 index1++;
	    	 bsx.newLine();
	     }
     	 
	     bsx.close();
	     bsx = null;
	     tbr.close();
	     tbr = null;
     } 
     public static void FilterDuplicates() throws NumberFormatException, IOException
     {
    	 Set<String> ett_HashSet = new HashSet<String>();
	 	 FileInputStream fstream0 = new FileInputStream(GetAutomationFolderPath() + "Input\\ett_List.txt");
	 	 DataInputStream in = new DataInputStream(fstream0);
	 	 BufferedReader br = new BufferedReader(new InputStreamReader(in));
	     String strLine = "";
	     
	     BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\mechu\\Desktop\\Automation\\eAppRedmi4A\\Input\\duplicates.txt"));
	   
	     while ((strLine = br.readLine()) != null)
	     {
	          if(strLine.isEmpty() == true) continue;
	        	 
		      String[] tokens = strLine.split("-");
		      if(ett_HashSet.contains(tokens[1]))
		      {
		    	  bw.write(tokens[0]);
		    	  bw.newLine();
		      }
		      else
		      {
		    	  ett_HashSet.add(tokens[1]);
		      }
	    }
	     
	    br.close();
	    bw.close();
     }
     public static void LoadInput()
     {
     	 try
     	 {   
		        if(ett_selected)
		        {
		 	   	     FileInputStream fstream = new FileInputStream(GetAutomationFolderPath() + "Input\\ett_selected.txt");
		 	   	     DataInputStream in = new DataInputStream(fstream);
		 	   	     BufferedReader br = new BufferedReader(new InputStreamReader(in));
		             String str = "";
			   	     while ((str = br.readLine()) != null)
			   	     {
			   	         if(str.isEmpty()) continue;
			   	         System.out.println(Integer.parseInt(str));
			   	         ett_custom.add(Integer.parseInt(str));	 
			   	     }
			   	     br.close();
		        }
     
	   	        FileInputStream fstream = new FileInputStream(GetAutomationFolderPath() + "Input\\ett_List.txt");
	   	        DataInputStream in = new DataInputStream(fstream);
	   	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
	   	        FileInputStream fstream1 = new FileInputStream(GetAutomationFolderPath() + "Input\\input.txt");
	   	        DataInputStream in1 = new DataInputStream(fstream1);
	   	        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
	   	        
	   	        FileInputStream fstream2 = new FileInputStream(GetAutomationFolderPath() + "Input\\app.txt");
	   	        DataInputStream in2 = new DataInputStream(fstream2);
	   	        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
	   			String strLine = null;	   			 
	   			strLine = br1.readLine();
	   			fromIndex = Integer.parseInt(strLine.substring(11));
	   			
	   			strLine = br1.readLine();
	   			endIndex = Integer.parseInt(strLine.substring(9));
	   			

	  	        in1.close();
	  	         
	   	         while ((strLine = br.readLine()) != null)
	   	         {
	   	        	 if(strLine.isEmpty() == true) continue;
	   	        	 
	   				  String[] tokens = strLine.split("-");
	   				  rd[Integer.parseInt(tokens[0])] = new Record();
	   				  rd[Integer.parseInt(tokens[0])].SetRecord(Integer.parseInt(tokens[0]),tokens[1],tokens[2]);
	   				  System.out.println(tokens[0] + " " + tokens[1] + " " + tokens[2]);
	   	         }
	   	         in.close();
	   	      
	   	         while ((strLine = br2.readLine()) != null)
	   	         {
	   	        	  if(strLine.isEmpty() == true) continue;
	   	        	  String[] tokens = strLine.split("-");
                      int index = Integer.parseInt(tokens[0]);
                      if(index < fromIndex) continue;
                      if(index > endIndex) break;
                      String[] tkn = tokens[1].split(",");
                      int p = 0;
                      
                      if(doallapps == false)
                      {
	                      System.out.println(index);
	                      while(p < tkn.length)
	                      {
		   				     rd[index].set.add(tkn[p]);
		   				     System.out.println(tkn[p]);
		   				     p++;
	                      }
                      }
                      
	   	         }
	   	         in2.close();
   	   	 }
   	     catch (Exception e)
   	     {
   	        System.err.println("Error: " + e.getMessage());
   	     }
     }
     public static void TakeScreenshotAndSave(Screen s) throws InterruptedException, FindFailed, IOException
     {
   	  		//ExecuteCommand("adb shell screencap -p /storage/emulated/0/Automation/" + (fromIndex) +"_1.png");
   	  		//Thread.sleep(3000);
   	  		GoToTranscationPage(s);
   	  		ExecuteCommand("adb shell rm -rf  /storage/emulated/0/Automation/" + (fromIndex) +".png");
   	  		Thread.sleep(3000);
   	  		ExecuteCommand("adb shell screencap -p /storage/emulated/0/Automation/" + (fromIndex) +"_2.png");
     }
     
     public static void main(String[] args) throws FindFailed, InterruptedException, IOException, ClassNotFoundException, SQLException
	 {
		 FillOTP(3222);
		 //FilterDuplicates();
		 LoadInput();
		 device = "";
		 Screen s = new Screen();
		 Region r = s.find(GetImagePath("SCREEN_TOP.png"));
		
         s.x = r.x;
         s.y = r.y;
         s.w = r.w;
         
         balRegion = r;
 
         System.out.println("SCREEN_TOP Found");
         runtime = Runtime.getRuntime();
         
         System.out.println(fromIndex);
         System.out.println(endIndex);
        

         try
         {

        	 while(fromIndex <= endIndex)
        	 {      
        		 
        		 if(balance_only == false && ett_selected == true && ett_custom.contains(fromIndex) == false)
        		 {
        			 fromIndex++;
        			 continue;
        		 }
        		 
	        	  ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
   	              Thread.sleep(3000);
        		 if(s.exists(GetImagePath("homefound.png")) == null)
        		 {
        			if(Vysor_Restart(s) == false)
        			{
        			  System.out.println("Looks vysor closed or mobile screen not showing in vysor Start vysor and press n and enter");
        			  playSound();
        			  WaitForUserInput();
        			}
        		 }
        		 RotateScreenOff();//screen rotate off
        		 bs = new BufferedWriter(new FileWriter(GetAutomationFolderPath() + "Output\\Installed_" + (fromIndex) + ".txt"));
        		
        		 ExecuteCommand("adb shell am force-stop info.earntalktime");
                 Thread.sleep(2000);
                 ExecuteCommand("adb shell pm clear info.earntalktime");
                 Thread.sleep(3000);
                 
                 GivePermissions("info.earntalktime");
                 
                 if(rd[fromIndex] == null)//id is not in list
                 {
         			 WriteLog("ERROR: ID = " + fromIndex + "Looks missing in list");
         			 bs.flush();
         			 bs.close();
         			 bs = null;
         			 fromIndex++;
         			 continue;
                 }
                 
        		 ErrorCode = 0;
        		 WriteLog("Processing ID = " + fromIndex);
        		 String line1 = rd[fromIndex].mobileno;
         		 String line2 = rd[fromIndex].otp;
         		 
         		 if(line1 == null || line2 == null)
         		 {
         			 WriteLog("ERROR: While Getting Mobile No/OTP from ett_list for ID =" + fromIndex);
         			 bs.flush();
         			 bs.close();
         			 bs = null;
         			 fromIndex++;
         			 continue;
         		 }
         		 
         		 WriteLog("SUCCESS: ID =" + fromIndex + "  Mobile No = " + line1 + "  OTP = " + line2);
         		 System.out.println(fromIndex + " " + line1 + "  " + line2);
         		 
        	     try
        	     {
      	        	      ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
	        	          Thread.sleep(12000);
	        	          LaunchApp("info.earntalktime");
	        	          //waitAndClick(s,GetImagePath("ETT_ICON.png"),1000);
	        	          Thread.sleep(10000);
	        	       
	        	      	  waitAndClick(s,GetImagePath("INPUT_MOBILENO.png"),1000);
	        	      	  if(ErrorCode == 1)
	        	      	  {
	        	      		  fromIndex++;
	        	      		  LogErrorCode();
	        	      		  continue;
	        	      	  }
	        	      	  Thread.sleep(3000);
	        	      	  
	        	          for(int x = 0; x < 10; x++)
	        	          {
	        	        	  s.type(Key.BACKSPACE);
	        	        	  Thread.sleep(100);
	        	          }
	        	          Thread.sleep(6000);
        	              s.type(line1);
        	              Thread.sleep(3000);

	        	          ExecuteCommand("adb  shell input keyevent KEYCODE_BACK");
	 	        	   	  Thread.sleep(3000);
	 	        	   	  waitAndClick(s,GetImagePath("VERIFY.png"),1000);
	        	      	  if(ErrorCode == 1)
	        	      	  {
	        	      		  LogErrorCode();
	        	      		  fromIndex++;
	        	      		  continue;
	        	      	  }
	 	        	   	  waitAndClick(s,GetImagePath("NO_OK.png"),1000);
	        	      	  if(ErrorCode == 1)
	        	      	  {
	        	      		  LogErrorCode();
	        	      		  fromIndex++;
	        	      		  continue;
	        	      	  }
	        	        
	        	      	  waitAndClick(s,GetImagePath("INPUT_OTP.png"),1000);
	        	      	  if(ErrorCode == 1)
	        	      	  {
	        	      		  LogErrorCode();
	        	      		  fromIndex++;
	        	      		  continue;
	        	      	  }
	        	      	  s.type(line2);
	        	      	  Thread.sleep(3000);
	        	      	  waitAndClick(s,GetImagePath("submit.png"),1000);
	 	        	   	  Thread.sleep(10000);
	 	        	   	  
	 	        	   	  if(s.exists(GetImagePath("INPUT_OTP.png")) != null)
	 	        	   	  {
	        	      		  LogErrorCode();
	        	      		  fromIndex++;
	        	      		  continue;
	 	        	   	  }
	 	        	   	  
	        	          if(WaitForEttPageLoad(s) == false)
	        	          {
	        	      		  LogErrorCode();
	        	      		  fromIndex++;
	        	      		  continue;
	        	      	  }

	        	          Thread.sleep(3000);
	        	          currId = fromIndex-1;
	        	          if(balance_only == false)
	        	          {
	        	        	  ProcessApps(s);
	        	        	  if(semiautomode)
	        	        	  {
	        	 	        	 playSound();
	        		        	 WaitForUserInput();
	        	        	  }
	        	        	  UninstallApps();
	        	        	  ChangeDeviceID();       
	        	        	  System.out.println("Reboot");
	        	          }
	        	          else
	        	          {
	        	        	  TakeScreenshotAndSave(s);
	        	          }
	        	          
	        	          fromIndex++;

        	        	  ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
	        	          Thread.sleep(3000);
        	        	  ExecuteCommand("adb  shell input keyevent KEYCODE_HOME");
	        	          Thread.sleep(3000);
     	                  bs.flush();
     	                  bs.close();
     	                  bs = null;
               			 
        	        }
        	        catch(FindFailed e)
        	        {
        	                 e.printStackTrace();
        	                 bs.flush();
        	                 bs.close();
        	                 bs = null;
        	        } 
        	 }//while close here
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
     
     }
}