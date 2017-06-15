package org.selenide.examples;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

import org.junit.Test;
import org.openqa.selenium.By;


public class googleTest {

    @Test
    public void userSearch(){
        String browserType = "FF"; // выбираем FF - Firefox (по умолчанию), CH - Chrome
        if (browserType == "CH")
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
            System.setProperty("selenide.browser", "Chrome");
        }


        open("http://google.com");
        $(By.name("q")).setValue("Selenide").pressEnter();

        $$("#ires div.g").shouldHave(size(10));
        $("#ires div.g").shouldHave(text("Selenide: лаконичные и стабильные UI тесты на Java"));
    }
}
