package com.selenium.steps;

import com.selenium.com.mindtree.utility.Waits;
import com.selenium.page.HomePage;
import com.selenium.page.LoginPage;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class Steps{
    public static void fillUsernameAndPassword (String username , String Password){
        Waits.waitByElement(LoginPage.enterUsername()).sendKeys(username);
        Waits.waitByElement(LoginPage.enterPassword()).sendKeys(Password);
        Waits.waitByElement(LoginPage.clicksignIn()).click();
    }

    public static void clickOnCreate (){
        Waits.waitByElement(HomePage.createClassLink()).click();
    }

}
