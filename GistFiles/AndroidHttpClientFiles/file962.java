package com.selenium.page;

import com.selenium.com.mindtree.utility.PropertyReader;
import org.openqa.selenium.By;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class LoginPage {

    public static By enterUsername(){

        By by = (By.xpath(PropertyReader.propertyReader().getProperty("username")));
        return by ;
    }

    public static By enterPassword( ){
        By by = (By.xpath(PropertyReader.propertyReader().getProperty("password")));
        return by ;
    }

    public static By clicksignIn() {
        By by = By.xpath(PropertyReader.propertyReader().getProperty("signIn"));
        return by;
    }
}
