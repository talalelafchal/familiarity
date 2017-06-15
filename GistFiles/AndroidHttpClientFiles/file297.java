import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.selenium.BaseClass.BaseClass;
import com.selenium.com.mindtree.utility.PropertyReader;
import com.selenium.steps.Steps;
import org.testng.annotations.Test;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class LoginTest extends BaseClass {


    @Test
    public static void LoginTest(){
        ExtentTest test = extentReporter.createTest("Login Test");
        driver.get(PropertyReader.propertyReader().getProperty("url"));
        test.log(Status.INFO, "User is able to Login into the URL");
        logger.info("User is able to Login into the URL");
        Steps.fillUsernameAndPassword("","");
        logger.info("User is able to enter the Username  and Password ");
        test.log(Status.PASS, "Able to Login into the Web Applications");
        logger.info("Able to Login into the Web Applications ");
        Steps.clickOnCreate();
        test.log(Status.PASS, "Able to Navigate to the Home Page ");
        logger.info("Able to Navigate to the Home Page");



    }
}
