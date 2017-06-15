import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

/**
 * Created by Zapalskyi Volodymyr on 30.11.2016.
 */
public class main {
    public static void main(String[] args) {

        //Include webdriver
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");

        //Open browser
        WebDriver brow1 = new ChromeDriver();

        //Go to site
        brow1.get("http://www.bing.com");

        //1-st page (searching)
        brow1.findElement(By.id("sb_form_q")).sendKeys("automation");
        brow1.findElement(By.id("sb_form_go")).click();

        //Print page title
        System.out.println("Title of page: " + brow1.getTitle());

        //Print result titles
        ArrayList<WebElement> resultList = (ArrayList<WebElement>) brow1.findElements(By.className("b_title"));
        System.out.println("Result list:");
        for(int i=0; i < resultList.size() ; i++){
            System.out.println(i+1 + ". " + resultList.get(i).findElement(By.tagName("a")).getText());
        }

        //Close browser
        brow1.quit();
    }
}
