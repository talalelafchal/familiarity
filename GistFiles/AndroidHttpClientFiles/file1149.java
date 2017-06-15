package org.selenide.examples.googleTestWithPageObject;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SearchResults {
    public ElementsCollection getResults() {
        return $$("#ires .g");
    }
    public SelenideElement getResult(int index) {
        return $("#ires .g", index);
    }
}
