package com.epam.gai.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by Nadiya_Sidlovska on 2/5/14.
 */
public class SimpleTest {
    @Test
    public void articlePageTest() {
        WebDriver driver = new FirefoxDriver();
        String baseURL = "http://adicom:adicom@hp.dev.brand.adidas.com/";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.get(baseURL + "uk/goallin-2/");
//        driver.findElement(By.linkText("Dream Jobs: Meet Mark & Taylor! New!!!")).click();
        WebElement firstArticleHeadline = driver.findElement(By.xpath("(//section//div[@class = 'headline'])[1]"));
        WebElement category = firstArticleHeadline.findElement(By.xpath(".//span[@class = 'category-label']"));
        WebElement title = firstArticleHeadline.findElement(By.xpath("./div[@class = 'title-wrapper']//a"));
        String categoryText = category.getText();
        String titleText = title.getText();
        title.click();

        WebElement categoryS = driver.findElement(By.xpath("//span[@class = 'category-label']"));
        WebElement titleS = driver.findElement(By.xpath("//div[@class = 'title-wrapper']/*"));
        Assert.assertEquals(categoryS.getText().toLowerCase(), categoryText.toLowerCase(), "Incorrect category-label");
        Assert.assertEquals(titleS.getText().toLowerCase(), titleText.toLowerCase(), "Incorrect title");

//        driver.findElement(By.xpath("//div[@id='content-container']/section/a)".click());

        driver.quit();
    }

}