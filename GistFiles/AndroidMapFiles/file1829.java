import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


public class CountMethods {

	/*Setup Instructions
	1. Compile the project you wish to evaluate
	2. Find the classes.dex file in the bin folder of the project folder. 
	3. Place the path of the classes.dex folder into line 2 of the code
	4. Ensure the project is linked to dx.jar which you can find online
	5. Compile and run the program and view the results in the console
	*/
	
	public static void main(String[] args) throws IOException 
	{
		int totalMethods = 0 ;
		
		//Place file path to classes.dex
	    com.android.dex.Dex dex = new com.android.dex.Dex(new File("/home/Count Methods/classes.dex"));
	    Map<String, AtomicInteger> packageToCount = new TreeMap<String, AtomicInteger>();
	    for (com.android.dex.MethodId methodId : dex.methodIds()) {
	      String typeName = dex.typeNames().get(methodId.getDeclaringClassIndex());
	      String packageName = typeNameToPackageName(typeName);
	      //System.out.println(packageName);
	      AtomicInteger count = packageToCount.get(packageName);
	      if (count == null) {
	        count = new AtomicInteger();
	        packageToCount.put(packageName, count);
	      }
	      count.incrementAndGet();
	    }
	    for (Map.Entry<String, AtomicInteger> entry : packageToCount.entrySet()) {
	      System.out.printf("% 8d %s%n", entry.getValue().get(), entry.getKey());
	      totalMethods = totalMethods + entry.getValue().get() ;
	    }
	    
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    
	    PackageCount();
	    
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    
	    System.out.printf("Total Method Count: " + String.valueOf(totalMethods)) ;
	  }

	  private static String typeNameToPackageName(String typeName) {
	    if (typeName.startsWith("[")) typeName = typeName.substring(1); // arrays
	    if (!typeName.contains("/")) return "<default>"; // default package
	    return typeName.substring(1, typeName.lastIndexOf('/')).replaceAll("/", ".");
	  }
	  
	  public static void PackageCount() throws IOException
	  {
			int totalMethods = 0 ;
			String package1 = "" ;
			String package2 = "" ;
			String currentPackage1 = "" ;
			String currentPackage2 = "" ;
			int index1, index2, counter;
			counter = 1;
			boolean firstTime = true ;
			
		    com.android.dex.Dex dex = new com.android.dex.Dex(new File("/home/workspace3/Count Methods/classes.dex"));
		    Map<String, AtomicInteger> packageToCount = new TreeMap<String, AtomicInteger>();
		    for (com.android.dex.MethodId methodId : dex.methodIds()) {
		      String typeName = dex.typeNames().get(methodId.getDeclaringClassIndex());
		      String packageName = typeNameToPackageName(typeName);
		      //System.out.println(packageName);
		      AtomicInteger count = packageToCount.get(packageName);
		      if (count == null) {
		        count = new AtomicInteger();
		        packageToCount.put(packageName, count);
		      }
		      count.incrementAndGet();
		    }
		    for (Map.Entry<String, AtomicInteger> entry : packageToCount.entrySet()) 
		    {
		    	
		    	{
		    	//Check first package name
		    	 currentPackage1 = entry.getKey() ;
		    	 index1 = 0 ;
		    	 index2 = currentPackage1.indexOf(".");
		    	 if(index2 == -1)
		    		 index2 = currentPackage1.length() ;
		    	 //System.out.println(currentPackage1.substring(index1, index2))   ;
		    	 currentPackage1 = currentPackage1.substring(index1, index2);
		    	 
		    	//Check second package name
		    	 currentPackage2 = entry.getKey() ;
		    	 index1 = currentPackage2.indexOf(".") ;
		    	 if(index1 == -1)
		    	 {
		    		 index1 = currentPackage2.length() ;
		    		 currentPackage2 = currentPackage2.substring(index1,currentPackage2.length());
		    	 }
		    	 else
		    		 currentPackage2 = currentPackage2.substring(index1+1,currentPackage2.length());
		    	 //System.out.println("2: " + currentPackage2)   ;
		    	 
		    	 index2 = currentPackage2.indexOf(".");
		    	 if(index2 == -1)
		    		 index2 = currentPackage2.length() ;
		    	 currentPackage2 = currentPackage2.substring(0, index2) ;
		    	 //System.out.println(currentPackage1 + "." + currentPackage2)   ;
		    	 
		    	 if(package1.equals(currentPackage1) && package2.equals(currentPackage2))
		    	 {
		    		 counter = counter + entry.getValue().get();
		    	 }
		    	 else
		    	 {
		    		 if(firstTime == true)
		    			 firstTime =false ;
		    		 else
		    			 System.out.println(package1 + "." + package2 + " : " + counter);
		    		 counter = entry.getValue().get() ;
		    		 package1 = currentPackage1 ;
		    		 package2 = currentPackage2 ;
		    	 }
		    	 
		    	}
		    }
		  }
	  
	
	  }
