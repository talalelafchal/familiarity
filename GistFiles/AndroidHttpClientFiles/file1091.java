package com.selenium.browserFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class BrowserIntillzation {
    static WebDriver driver = null ;
    public static WebDriver driverIntillazation(browserType type){

        switch (type){
            case Chrome:
                String chromePath = new File ("src\\main\\resources\\chromedriver.exe").getAbsolutePath();
                 System.setProperty("webdriver.chrome.driver", chromePath);
                driver = new ChromeDriver();
                break;
            case firefox:
                String firefoxPath = new File ("src\\main\\resources\\geckodriver.exe").getAbsolutePath();
                System.setProperty("webdriver.gecko.driver", firefoxPath);
                driver = new FirefoxDriver();
                break;

            default:
                System.out.println("Please enter a correct Browser Name in the Property file");
        }
        driver.manage().window().maximize();
         return  driver;

    }
}
