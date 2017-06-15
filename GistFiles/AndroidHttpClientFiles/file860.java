package edu.ualr.cpsc7398.updatechecker.model;

/**
 * Created by uddhav on 2/13/17.
 */

public class UrlDataGeneralWhatsGoingOn {
    private String name;
    private String value;

    public UrlDataGeneralWhatsGoingOn(String name, String value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
