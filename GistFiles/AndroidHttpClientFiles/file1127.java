package login;

import utility.Interaction;
import utility.Utility;
import utility.iOSInit;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import java.lang.InterruptedException;

/*
 * Area:  Login
 * Purpose:  This class provides the ability to log into the application
 */

public class LoginClass extends iOSInit {

    public static void loginNativeApp() throws InterruptedException {
        // Enters the User ID (specified within the DataLibrary.xml) into the User ID field (specified from the ObjectLibrary.xml)
        Interaction.byNameSend(driver, Utility.returnXML("userID"), Utility.returnObjectXML("login_UserIDField"));
        // Enters the Password (specified within the DataLibrary.xml) into the Password field (specified from the ObjectLibrary.xml)
        Interaction.byNameSend(driver, Utility.returnXML("password"), Utility.returnObjectXML("login_PasswordField"));
        // Clicks on the Sign In button (specified from the ObjectLibrary.xml)
        Interaction.byNameClick(driver, Utility.returnObjectXML("login_SignInButton"));

        // Waits 10 seconds for login process to complete
        Thread.sleep(10000);

        // Verifies the Sign Out button displays to ensure login process was successful
        WebElement element = driver.findElement(By.name("signout"));

        if (element.isDisplayed()) {
            //     Interaction.byNameClick(driver, Utility.returnObjectXML("common_TransferNavigation"));
            System.out.println("Home Screen displays");
            Reporter.log("Home Screen displayed");
        }
        else {
            System.out.println("Home Screen does not display");
            Reporter.log("Home Screen not displayed");
        }

    }
}
