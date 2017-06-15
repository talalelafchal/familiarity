package common;

import utility.Interaction;
import utility.Utility;
import utility.iOSInit;


import java.lang.InterruptedException;

/*
 * Area:  Signout
 * Purpose:  This class contains the ability to signout of the app
 */



public class SignoutClass extends iOSInit {

    // Click on the Signout button
    public static void signoutButton() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_SignOutButton"));

        Thread.sleep(5000);

        Interaction.byNameClick(driver, Utility.returnObjectXML("common_SignOutAlertYesButton"));
    }

}
