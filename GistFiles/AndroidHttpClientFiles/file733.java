package Tests;

import io.appium.java_client.ios.IOSDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import utility.iOSInit;
import billPay.BillPayClass;
import common.SignoutClass;
import login.LoginClass;
import common.NavigationClass;
import utility.SendMail;

import java.lang.InterruptedException;


/*
 * Area:  Bill Pay - Add Company Payee
 * Purpose:  This script will add a user within Bill Pay
 */


public class BillPayAddUser {

    //Defines the driver type, which is IOS
    private IOSDriver driver;

    //Declares which platform to execute tests on based selected options within iOSInit class
    @BeforeTest
    protected void testSetup() throws IOException, InterruptedException {
        driver = iOSInit.launchNativeApp();
    }

    //Performs a teardown of the test
    @AfterSuite
    public void cleanEnv() throws InterruptedException, UnsupportedEncodingException, Exception{

        driver.quit();
        SendMail.execute("emailable-report.html");
    }

    //Executes Login, Bill Pay navigation, Add a Company Payee, and then signs out
    @Test(groups = "AddCompanyPayee", priority = 0)
    static void login() throws InterruptedException{
        LoginClass.loginNativeApp();
    }
    @Test (groups = "AddCompanyPayee", priority = 1)
    static void billPayNavigate() throws InterruptedException{
        NavigationClass.billPayNavigation();
    }

    @Test (groups = "AddCompanyPayee", priority = 2)
    static void addPayeeCompany() throws InterruptedException, ParserConfigurationException, IOException, SAXException, TransformerException {
        BillPayClass.addPayeeCompany();
    }

    @Test (groups = "AddCompanyPayee", priority = 3)
    static void signout() throws InterruptedException{
        SignoutClass.signoutButton();
    }

}