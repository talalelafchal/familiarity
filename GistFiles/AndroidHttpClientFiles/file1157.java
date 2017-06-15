/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 09/01/13
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */

import java.util.Set;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import com.sun.jna.platform.win32.Wdm;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;

public class ExportAccountFeedTest {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

     @Before
    public void setUp() throws Exception {


        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(true);
        //FirefoxDriver driver = new FirefoxDriver(profile);

        driver = new FirefoxDriver(profile);

        baseUrl = "https://sfdataloader-load.cloudhub.io/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
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

        driver.get(baseUrl + "static/#export");



        //Select Account Feed Object to Export

        driver.findElement(By.cssSelector("li[title=\"Account Feed (AccountFeed)\"] > a > span.object-name > span")).click();

        // Click Next Button
        driver.findElement(By.xpath("//div[@id='wizard-view']/div/div[3]/ul/li[4]/button")).click();


        // Select the checkbox of fields to export
        driver.findElement(By.cssSelector("div.action.span1 > input[type=\"checkbox\"]")).click();
        driver.findElement(By.xpath("(//input[@type='checkbox'])[3]")).click();
        driver.findElement(By.xpath("(//input[@type='checkbox'])[4]")).click();
        driver.findElement(By.xpath("(//input[@type='checkbox'])[5]")).click();

         // next
        driver.findElement(By.xpath("//div[@id='wizard-view']/div/div[3]/ul/li[4]/button")).click();

       // Select Advance Option

        driver.findElement(By.cssSelector("i.icon-chevron-right")).click();

        // input 10 row limit
        driver.findElement(By.cssSelector("input.export-limit")).clear();
        driver.findElement(By.cssSelector("input.export-limit")).sendKeys("10");

        // Click Save Button
        driver.findElement(By.xpath("//div[@id='wizard-view']/div/div[3]/ul/li[5]/button")).click();


        printCookieState(this.driver.manage().getCookies());
        System.out.println("Source\n " +
                driver.getPageSource());



        ////////
        // Run a Task Created
       // driver.get(baseUrl + "/static/#tasks/");

        // Click over Task to Run
        driver.findElement(By.cssSelector("div.name-wrapper")).click();
          // Click Run Link  of Task
        driver.findElement(By.cssSelector("li.task.active > div.status-no_run.type-export > div.content > div.controls > a.run")).click();

        //button[2]

        // Click Save Button

       // WebDriverWait wait = new WebDriverWait(driver, 10);
        //WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("//button[2]")));

        driver.findElement(By.cssSelector("button.btn.run")).click();


         // Wait to successful file

         WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.downloads > a")));

        Thread.sleep(30000);
        ////////


       System.out.println( "href 1\n" + driver.findElement(By.cssSelector("div.downloads > a")).getText());

        // click Download file
        driver.findElement(By.cssSelector("div.downloads > a")).click();

        Thread.sleep(10000);


       System.out.println("Retorno Windows: \n" + closeWindowAndGetItsText());

       // System.out.println("Check /api/runs...." + driver.getCurrentUrl());

       // driver.get("https://sfdataloader-load.cloudhub.io/api/runs?task_id=44");
      //  System.out.println("Soruce /api/runs" + driver.getPageSource());

        //https://sfdataloader-load.cloudhub.io/api/runs?task_id=44

        driver.get("https://sfdataloader-load.cloudhub.io/static/#task");

        Thread.sleep(10000);

        ////////////
        // Logout
        driver.findElement(By.cssSelector("i.icon-chevron-down")).click();
        driver.findElement(By.linkText("Logout")).click();
    }


    private void printCookieState(Set<Cookie> seleniumCookieSet) {

      //  BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
        for (org.openqa.selenium.Cookie seleniumCookie  :  seleniumCookieSet) {

           System.out.println( "cookiename" + seleniumCookie.getName() + "value:" + seleniumCookie.getValue());
           // duplicateCookie.setDomain(seleniumCookie.getDomain());
           // duplicateCookie.setSecure(seleniumCookie.isSecure());
           // duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
            //duplicateCookie.setPath(seleniumCookie.getPath());
           // mimicWebDriverCookieStore.addCookie(duplicateCookie);
        }


    }
     @After
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
            //driver.switchTo().activeElement().sendKeys(Keys.RETURN);
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
        Actions builder ;
        Action selectMultiple;
        ApiDataLoaderProxy  apidl;


        try {

            apidl = new ApiDataLoaderProxy( this.driver);
            apidl.setTaskId(getTaskId());
            driver.switchTo().activeElement().sendKeys(Keys.ENTER);
           System.out.println("initRunId : " +apidl.initRunId());

           System.out.println("initDownloadLink : " +apidl.initSuccessDownloadLink());
            apidl.downloadSuccessFile();





            //down.downloadFile(driver.getCurrentUrl());

           // down.downloadFile("https://sfdataloader-load.cloudhub.io/api/runs?task_id=" + getTaskId());

            /*
            for (String handle : driver.getWindowHandles()) {
                System.out.println("Ventana:\n " +driver.switchTo().window(handle).getCurrentUrl() );
               // driver.switchTo().window(handle).getCurrentUrl();

            }
            */

           // (new Actions(driver)).sendKeys( ( Keys.COMMAND ).perform();

            //(new Actions(driver)).click().perform();


            //builder = new Actions(driver);

           // builder.sendKeys(Keys.CANCEL);

                  //  .keyUp(Keys.COMMAND);
            //selectMultiple = builder.build();
            //selectMultiple.perform();


            //driver.switchTo().activeElement().sendKeys(Keys.RETURN);
          //  strHref = driver.switchTo().activeElement().getAttribute("href");
          //  strFrom = driver.switchTo().activeElement().getAttribute("from");
            return "rhref:\n"+strHref + "from \n" + strFrom + "\n bajar archivo";

        } catch(Throwable exc)
        {
            exc.printStackTrace();
            throw exc ;

        }finally {
            acceptNextAlert = true;
        }
    }



    private String getTaskId() {
        String taskId ="" ;
        String  tmpStr ="";
        int initTask= 0;

        tmpStr= driver.getCurrentUrl();
        System.out.println("url : " + driver.getCurrentUrl() );

        if (tmpStr.contains("#tasks/") ) {

            initTask = tmpStr.lastIndexOf("#tasks/") ;
            taskId = tmpStr.substring(initTask+7 ,tmpStr.length() );
            System.out.println("taskId Actual: " +  taskId);
        }

         return taskId;


    }

    public static void main(String[] args) {
        ExportAccountFeedTest clwd= new ExportAccountFeedTest();


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
