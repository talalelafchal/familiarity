package com.selenium.BaseClass;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.selenium.browserFactory.BrowserIntillzation;
import com.selenium.browserFactory.browserType;
import com.selenium.com.mindtree.utility.PropertyReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class BaseClass {
   public  static WebDriver driver = null ;
   public static ExtentHtmlReporter extentHtmlReporter  = new ExtentHtmlReporter("Reports.html");
    public static  ExtentReports extentReporter =  new ExtentReports();
   public static Logger logger = LogManager.getLogger();



   @BeforeTest
    public static void  beforTest(){
       extentReporter.attachReporter(extentHtmlReporter);


       if (PropertyReader.propertyReader().getProperty("browser").equalsIgnoreCase("Chrome")){
           driver  = BrowserIntillzation.driverIntillazation(browserType.Chrome);
           logger.info("Able to Start the Chrome Browser ");
       }

       else if (PropertyReader.propertyReader().getProperty("browser").equalsIgnoreCase("Firefox"))
       {
           driver = BrowserIntillzation.driverIntillazation(browserType.firefox);
           logger.info("Able to Start the Firefox Browser ");
       }

       else
       {
           System.out.println("Please enter a valid browser name ");
           logger.error("Please give a Valid Browser , Browser Name is given wrong ");

       }



    }

    @AfterTest
    public static  void afterSuite(){
        extentReporter.flush();
        driver.close();
    }
}
