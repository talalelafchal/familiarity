/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 09/01/13
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class WebDriverDLTest3 {
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
    public void testWebDriverDLTest() throws Throwable {
        driver.get(baseUrl + "static/#");
        new Select(driver.findElement(By.name("salesforce_instance"))).selectByVisibleText("Sandbox");
        driver.findElement(By.cssSelector("input.btn")).click();
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("sfdl-all@mulesource.com.test");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("datal0ader");
        driver.findElement(By.id("Login")).click();
        driver.findElement(By.id("cmd-new-task")).click();

        driver.get(baseUrl + "static/#import");



        //Select Account Feed Object to Export

        driver.findElement(By.cssSelector("li[title=\"Account Contact Role (AccountContactRole)\"] > a > span.object-name > span")).click();

        // Click Next Button
        driver.findElement(By.xpath("//div[@id='wizard-view']/div/div[3]/ul/li[4]/button")).click();

        //Thread.sleep(10000);

       // System.out.println( "Text \n" + driver.findElement(By.xpath("//input[@type='file']")).getText() );


        driver.findElement(By.xpath("//input[@type='file']")).sendKeys("/Users/lunanueva/Documents/mulesoft/sample.csv");
       // driver.findElement(By.xpath("//input[@type='file']")).sendKeys(Keys.ENTER);
       // /Users/lunanueva/Documents/mulesoft/


       // driver.findElement(By.xpath("//form[@id]")).sendKeys(Keys.ENTER);

       // driver.findElement(By.xpath("//form[@id]")).submit();

        /*
        WebElement select = driver.findElement(By.tagName("select"));
        List<WebElement> allOptions = select.findElements(By.tagName("option"));
        for (WebElement option : allOptions) {
            System.out.println(String.format("Value is: %s", option.getAttribute("value")));
            option.click();
          */

       // Thread.sleep(10000);

        ////////////
        // Logout
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
            //driver.switchTo().activeElement().sendKeys(  );
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


    private String closeWindowAndGetItsText() throws Throwable {

        String strHref= "";
        String strFrom = "";

        try {

            strHref = driver.switchTo().activeElement().getAttribute("href");
            strFrom = driver.switchTo().activeElement().getAttribute("from");
            return "rhref:\n"+strHref + "from \n" + strFrom + "\n envio enter";

        } catch(Throwable exc)
        {
            exc.printStackTrace();
            throw exc ;

        }finally {
            acceptNextAlert = true;
        }
    }

    public static void main(String[] args) {
        WebDriverDLTest3 clwd= new WebDriverDLTest3();


        try {
            clwd.setUp();
            clwd.testWebDriverDLTest();
            clwd.tearDown();

        }catch (Throwable ex){
            ex.printStackTrace();
            System.out.println("Error\n" + ex.toString());
            try {
                clwd.tearDown();
            }catch(Exception ex1){
                System.out.println("Error en tearDown\n" + ex1.toString());
            }
        }

    }
}
