package com.example.denis.secondprogram;

import java.util.ArrayList;

/**
 * Created by denis on 9/11/14.
 */
public class Database {
    private static ArrayList<String>logins = new ArrayList<String>();
    private static ArrayList<String>passwords = new ArrayList<String>();
    private static ArrayList<String>emails = new ArrayList<String>();

    public static void setLogins(String login) {
        logins.add(login);
    }
    public static void setPasswords(String password) {
        passwords.add(password);
    }

    public static void setEmails(String email) {
        emails.add(email);
    }

    public static String getLogin(int index) {
        return logins.get(index);
    }

    public static String getPassword(int index) {
        return passwords.get(index);
    }

    public static String getEmail(int index) {
        return emails.get(index);
    }

    public static int getSize() {
        return logins.size();
    }

}
