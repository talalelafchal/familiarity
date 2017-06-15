package com.epam.gai.tests;

import com.epam.gai.core.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ihor_Yarmolovskyy
 * Date: 2/4/14
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GaiHomePage extends BaseTest {
    WebDriver driver;


    @Test

    public void filterByCategory() throws InterruptedException {
        driver = new FirefoxDriver();
        driver.get(System.getProperty("test.baseURL")+System.getProperty("test.marketURL")+"/goallin-2/");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        WebElement filterByText = driver.findElement(By.xpath("//span[@data-home-label='Filter By']"));
        System.out.println("Verify page title");
        Assert.assertEquals(driver.getTitle(), "adidas Go All In Blog UK", "Title is not as expected");
        System.out.println("Verify Filter By text");
        Assert.assertEquals(filterByText.getText(), "FILTER BY", "Filter text is not correct'");

        System.out.println(" Filter By Category");
        Reporter.log("Done",2,true);
      /*  filterByCategoryHomePage("//a[@data-categories='football']");
       Wait refresh filter.
       need to be clarified
       waitElement("//div[@class=\"filter-list-container\"]");
        Thread.sleep(2000);
        System.out.println("Verify football tag");
        Assert.assertEquals(driver.findElement(By.xpath("//a[@class='category-label']")).getText().toLowerCase(),"football","Tags are not correct'" );

        System.out.println("Verify Filter by changed to Football");
         Assert.assertEquals(driver.findElement(By.xpath("//span[@data-home-label='Filter By']")).getText().toLowerCase(),"football","Filter text is not correct'" );
        System.out.println("Open filter and verify that Close button is available");
        driver.findElement(By.xpath("//span[@data-home-label='Filter By']")).click();
         Assert.assertTrue(isElementPresent(By.xpath("//div[@class='label-close' and @style=\"display: block;\"]")),"Close button is not visible");
        System.out.println("Close filter and verify that Close button is displayed");
        driver.findElement(By.xpath("//span[@data-home-label='Filter By']")).click();
        Assert.assertFalse(isElementPresent(By.xpath("//div[@class='label-close' and @style=\"display: block;\"]")),"Close button is  visible"); */
    }


    @AfterClass
    public void cleanup() {
        driver.quit();
    }
}
