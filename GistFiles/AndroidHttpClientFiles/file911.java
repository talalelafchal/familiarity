import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by root on 17.06.14.
 */
public class LogPassTest {
    public LogPassTest() {
    }



    public String Login (String login) {

        WebDriver ffdriver = new FirefoxDriver();
        /*ffdriver.get("https://drupal.org");
        ffdriver.findElement(By.className("login-register last")).click();
        login = ffdriver.findElement(By.);
        pass = ffdriver.findElement(By.);*/
        ffdriver.get("http://vk.com/id16175847");
        login = ffdriver.findElement(By.id("ts_input")).getText();

        return login;

    }
}
