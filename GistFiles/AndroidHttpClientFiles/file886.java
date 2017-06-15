package com.epam.gai.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ihor_Yarmolovskyy
 * Date: 2/6/14
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class Driver {
    private static WebDriver driver;

    public static WebDriver get() {
        return driver;
    }

    public static void set(WebDriver driverInput) {
        driver = driverInput;
    }

    public boolean isElementPresent(By locator){
        get().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> elements=get().findElements(locator);
        get().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return elements.size()>0 && elements.get(0).isDisplayed();

    }

     public static void init(){
         Properties properties = new Properties();
         FileInputStream propFile;
         try {
             propFile = new FileInputStream("test.properties");
             properties.load(propFile);
         } catch (IOException e) {
             e.printStackTrace();
             Assert.fail(e.getMessage());
         }
         @SuppressWarnings("unchecked")
         Enumeration<String> e = (Enumeration<String>) properties.propertyNames();
         while (e.hasMoreElements()) {
             String key = e.nextElement();
             System.setProperty(key, properties.getProperty(key));
             Reporter.log(key + " - " + properties.getProperty(key), 2, true);
         }
         WebDriver driverInput=new FirefoxDriver();
         driverInput.manage().timeouts().implicitlyWait(Integer.parseInt(System.getProperty("test.timeout")),TimeUnit.SECONDS);
         Driver.set(new FirefoxDriver());
     }
    public static void tearDown(){
        Driver.get().quit();
                    }

    public void waitElement (String arg, String argue){
        WebDriverWait waiter= new WebDriverWait(driver,30,1000);
        By textLocator=By.xpath(arg);
        //needs condition about
        waiter.until(ExpectedConditions.visibilityOfElementLocated(textLocator));
    }

    public void filterByCategoryHomePage  (String arg){
        get().findElement(By.xpath("//span[@data-home-label='Filter By']")).click();
        get().findElement(By.xpath(arg)).click();
    }
}
