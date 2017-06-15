package com.selenium.com.mindtree.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by M1028219 on 3/15/2017.
 */
public class PropertyReader {
    static Properties properties = new Properties();

    public static Properties propertyReader() {


        try {
            properties.load(new FileInputStream(new File("src\\main\\resources\\Page.properties").getAbsolutePath()));
        } catch (Exception e) {
            System.out.println(e.getCause() + e.getMessage());
        }
        return properties;
    }

}
