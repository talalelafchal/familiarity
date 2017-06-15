/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 08/01/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */


import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;

public class WebDriverDLTest1 {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

  // @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "https://sfdataloader-load.cloudhub.io/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

   // @Test
    public void testWebDriverDLTest() throws Exception {
        driver.get(baseUrl + "static/#");
        new Select(driver.findElement(By.name("salesforce_instance"))).selectByVisibleText("Sandbox");
        driver.findElement(By.cssSelector("input.btn")).click();
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("sfdl-all@mulesource.com.test");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("datal0ader");
        driver.findElement(By.id("Login")).click();
        driver.findElement(By.id("cmd-new-task")).click();
        driver.findElement(By.cssSelector("ul.tasks-nav.dropdown-menu")).click();
        driver.findElement(By.id("cmd-new-task")).click();
        driver.get(baseUrl + "static/#task");
        /*
        System.out.println("Searching...");
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.   visibilityOfElementLocated(By.linkText("Export")));
        System.out.println("Found...");
          */
        // Esperar que aparezca el menu
        /*
        Wait<WebDriver> wait = new WebDriverWait(driver, 10);
        // Wait for search to complete
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                System.out.println("Searching...");
                return webDriver.findElement(By.c("Export")) !=
                        null;
            }
        });
          */
        driver.findElement(By.partialLinkText("Export\n")).click();
        driver.findElement(By.cssSelector("button.close")).click();
        driver.findElement(By.cssSelector("i.icon-chevron-down")).click();
        driver.findElement(By.linkText("Logout")).click();
    }

   // @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alert.getText();
        } finally {
            acceptNextAlert = true;
        }
    }


    public static void main(String[] args) {
        WebDriverDLTest1 clwd= new WebDriverDLTest1();


        try {
        clwd.setUp();
        clwd.testWebDriverDLTest();
        clwd.tearDown();

        }catch (Exception ex){
            System.out.println("Error\n" + ex.toString());
            try {
            clwd.tearDown();
            }catch(Exception ex1){
                System.out.println("Error en tearDown\n" + ex1.toString());
            }
        }

    }
}

