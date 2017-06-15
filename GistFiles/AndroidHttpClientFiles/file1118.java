package com.epam.gai.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Created with IntelliJ IDEA.
 * User: Ihor_Yarmolovskyy
 * Date: 2/4/14
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseTest {
    @BeforeMethod
    public void init() {
        Driver.init();
    }

    @AfterMethod
    public void cleanup() {
                           Driver.tearDown ();
    }

}
