package POM.Prestashop;

import POM.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by Дмитрий on 01.04.2017.
 */
public class Prestashop_Admin_Main_Page extends BasePage{

    public static String pageUrl = "http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/index.php?controller=AdminDashboard&token=f57b9e0913a4757c6ba02314cf473896";

    @FindBy(id = "header_employee_box")
    private WebElement icon;

    @FindBy(id = "employee_links")
    private WebElement employee_menu;

    @FindBy(id = "header_logout")
    private WebElement exit;

    @FindBy(className = "maintab")
    private List<WebElement> main_menu;


    public Prestashop_Admin_Main_Page(String browser) {
        super(browser);
    }

    public Prestashop_Admin_Main_Page(WebDriver driver) throws InterruptedException {
        super(driver);
        Thread.sleep(8000);
    }

    public  void CheckMenu() throws InterruptedException {
        for (int i = main_menu.size() - 1; i >= 0; i--) {
            (new WebDriverWait(getDriver(), 10))
                    .until(ExpectedConditions.elementToBeClickable(main_menu.get(i)));
            if (((main_menu.get(i).getText().contains("Modules")) || (main_menu.get(i).getText().contains("Каталог"))) == false) {
                Click_and_Check_link(main_menu.get(i));
            } else {
                Click_and_Check_link(main_menu.get(i));
                goToUrl(pageUrl);
            }


        }
    }



    public void Click_and_Check_link (WebElement element){
        element.click();
        System.out.println("________________________________________________________________________");
        System.out.println("h2 title : " + getDriver().findElement(By.tagName("h2")).getText());
        System.out.println ("url :" + getDriver().getCurrentUrl());
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println ("refreshing the page ...");
        getDriver().navigate().refresh();
        System.out.println(currentUrl);
        System.out.println ("Equal url? " + (getDriver().getCurrentUrl().equals(currentUrl)));
    }

    public Prestashop_Admin_Login_Page Log_out() throws InterruptedException {
        icon.click();
        exit.click();
        return new Prestashop_Admin_Login_Page(getDriver());
    }

}
