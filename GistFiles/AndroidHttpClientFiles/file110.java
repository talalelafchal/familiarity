package org.selenide.examples.googleTestWithPageObject;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
/**
 * Created by Mikhail on 15.05.2017.
 */
public class GooglePage {
    public SearchResults searchFor(String text) {
        $(By.name("q")).val(text).pressEnter();
        return page(SearchResults.class);
    }
}
