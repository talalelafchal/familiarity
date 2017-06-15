package org.selenide.examples.googleTestWithPageObject;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

import org.junit.Test;


public class googleTestPO {

    @Test
    public void userSearch(){
        String browserType = "FF"; // выбираем FF - Firefox (по умолчанию), CH - Chrome
        if (browserType == "CH")
        {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
            System.setProperty("selenide.browser", "Chrome");
        }
        GooglePage page = open("http://google.com/ncr", GooglePage.class);
        SearchResults results = page.searchFor("selenide");
        results.getResults().shouldHave(size(10));
        results.getResult(0).shouldHave(text("Selenide: concise UI tests in Java"));
    }
}
