package POM.Prestashop;

import POM.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Дмитрий on 01.04.2017.
 */
public class Prestashop_Admin_Login_Page extends BasePage {

    public static String pageUrl = "http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/";

    @FindBy(id = "email")
    private WebElement input_Email;

    @FindBy(id = "passwd")
    private WebElement input_Password;

    @FindBy(id = "login_form")
    private WebElement login_Form;


    //WebElement login_Form = getDriver().findElement(By.id("login_form"));
   // WebElement input_Email = getDriver().findElement(By.id("input_Email"));
    //WebElement input_Password = getDriver().findElement(By.id("input_Passwors"));

    public Prestashop_Admin_Login_Page(String browser)  {
        super(browser);
        goToUrl(pageUrl);
    }

    public Prestashop_Admin_Login_Page(WebDriver driver) throws InterruptedException {
        super(driver);
        Thread.sleep(3000);
    }

    public Prestashop_Admin_Main_Page LogIn(String email, String password) throws InterruptedException {
        input_Email.sendKeys(email);
        input_Password.sendKeys(password);
        login_Form.submit();
        return new Prestashop_Admin_Main_Page(getDriver());
    }
}
