package POM;

import driver.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Created by Дмитрий on 30.03.2017.
 */
public class BasePage {

    private WebDriver driver;
    public  static String pageUrl;

    public BasePage(String browser)
    {
        driver = Driver.getInstance(browser);
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public BasePage(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    public void goToUrl(String url)
    {
        driver.navigate().to(url);
    }

    public WebDriver getDriver()
    {
        return this.driver;
    }

}
