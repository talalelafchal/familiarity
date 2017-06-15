package com.vesicant.ActivityTest;

import android.text.LoginFilter;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: vesicant
 * Date: 13/03/13
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class RMSettings {
    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        Username = username;
    }

    public static String Username = "userName";

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }

    public static String Password = "password";

    public static String getCompany() {
        return Company;
    }

    public static void setCompany(String company) {
        Company = company;
    }

    public static String Company = "companyName";

    public static String getGateway() {
        return Gateway;
    }

    public static void setGateway(String gateway) {
        Gateway = gateway;
    }

    public static String Gateway = "http://gateway.com";
}
