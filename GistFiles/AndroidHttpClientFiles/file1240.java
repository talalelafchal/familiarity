package utility;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Random;

import static org.testng.Assert.assertEquals;


/**
 * Purpose:  This is a utility class which contains actions that allow for interaction with the app
 */


public class Interaction {


        static int timeout = 30;


//////////////// Target Elements for Click events ///////////////

// These methods are used to execute click events

///////////////////////////////////////////////////////////////


        // Clicks an object using specified Name
        public static void byNameClick(IOSDriver driver, String target) {

            System.out.println("Going To Click: " + target);
            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.name(target)));
            WebElement element = driver.findElement(By.name(target));
            element.click();
            Reporter.log("Clicking on: " + target);
        }


        // Clicks an object using specified XPath
        public static void byXpathClick(IOSDriver driver, String target) {

            System.out.println("Going To Click:" + target);
            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));
            element.click();
            Reporter.log("Clicking on: " + target);
        }

        // Identifies alert, clicks, and sends alert text to report
        public static void byNameAlertClick(IOSDriver driver, String target, String target2) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.name(target)));
            WebElement element = driver.findElement(By.name(target));
            WebElement element2 = driver.findElement(By.xpath(target2));
            String alertText = element2.getText();
            Reporter.log(alertText);
            element.click();

        }

        // Clicks an object using specified XPath
        public static void byRegexClick(IOSDriver driver, String title, String target) {

            System.out.println("Going To Click:" + target);
            //new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(title)));
            WebElement element = driver.findElement(By.name(target));
            element.click();
            Reporter.log("Clicking on: " + target);
        }
//////////// Target Elements for Send events ///////////////////

// These methods are used to execute send events

///////////////////////////////////////////////////////////////


        // Sends specified content to an object using specified ID
        public static void byIDSend(IOSDriver driver, String content, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.id(target)));
            WebElement element = driver.findElement(By.id(target));
            //highLightElement(driver, element);
            element.clear();
            element.sendKeys(content);
            Reporter.log("Sending " + content + " to " + target);
        }

        // Sends specified content to an object using specified Name
        public static void byNameSend(IOSDriver driver, String content, String target) {

            System.out.println("Going To Send: " + target);
            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.name(target)));
            WebElement element = driver.findElement(By.name(target));
            element.clear();
            element.sendKeys(content);
            Reporter.log("Sending " + content + " to " + target);
        }

        // Sends specified content to an object using specified XPath
        public static void byXpathSend(IOSDriver driver, String content, String target) {

            System.out.println("Going To Send:" + content + " In To " + target);
            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));
            element.sendKeys(content);
            Reporter.log("Sending " + content + " to " + target);
        }

        // Copies previous text and sends it to another field (i.e. field and confirm field)
        public static void byXpathSendDuplicateText(IOSDriver driver, String target, String target2) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement firstText = driver.findElement(By.xpath(target));
            WebElement secondText = driver.findElement(By.xpath(target2));
            String content = firstText.getText();
            secondText.sendKeys(content);
            System.out.println("This is the duplicate data: " + content);
        }


        // Sends random number to Xpath target
        public static void byXpathSendAccountNumber(IOSDriver driver, String target) throws ParserConfigurationException, IOException, SAXException {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));

            Random acctNumber = new Random();
            int randomNumber = acctNumber.nextInt(9000000) + 1;
            String rand = Integer.toString(randomNumber);
            rand.valueOf(randomNumber);
            element.sendKeys(rand);
            System.out.println("This is the random value: " + rand);

        }

///////////// Identify attributes of an element /////////////////

// These methods are used to list attributes of an element

///////////////////////////////////////////////////////////////

        // Returns the text of a specified object
        public static String byNameReturn(IOSDriver driver, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.name(target)));
            WebElement element = driver.findElement(By.name(target));
            return element.getText();
        }



////////////////// Validation and Is Present ////////////////////////////

// These methods are used for data validation and presence of elements

///////////////////////////////////////////////////////////////////////

        // Compares the element value to the stored value located in the XML file
        public static void byXpathValidate(IOSDriver driver, String content, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));
            String appData = element.getText();
            System.out.println("This is the data the app displays: " + appData);
            System.out.println("This is the input data: " + content);
            assertEquals(appData, content);
        }

        // Identifies the presence of an element on the screen
        public static void byXpathFindAlert(IOSDriver driver, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            driver.findElementByXPath(target).isDisplayed();
        }

        // Returns the Name of a specified object
        public static void getValuebyXpath(IOSDriver driver, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));
            String content = element.getText();
            System.out.println(content);

        }



////////////////// Read or Write to XML ////////////////////////

// These methods are used to read or write to the XML docs

///////////////////////////////////////////////////////////////


        // Returns the Name of a specified object
        public static String updateXMLbyXpathTarget(IOSDriver driver, String target) {

            new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(target)));
            WebElement element = driver.findElement(By.xpath(target));
            return element.getText();
        }




    }
