package common;

import utility.Interaction;
import utility.Utility;
import utility.iOSInit;
import org.testng.Reporter;

import java.lang.InterruptedException;

/*
 * Area:  Post-auth navigation
 * Purpose:  This class contains the ability to tap on main navigation buttons
 */



public class NavigationClass extends iOSInit {

    // Click on the Accounts button
    public static void accountNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_AccountsNavigation"));
        Reporter.log("Clicking on: Accounts");
        Thread.sleep(5000);
    }

    // Click on the Transfers button
    public static void transfersNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_TransfersNavigation"));
        Reporter.log("Clicking on: Transfers");
        Thread.sleep(5000);
    }

    // Click on the Bill Pay button
    public static void billPayNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_BillPayNavigation"));
        Reporter.log("Clicking on: Bill Pay");
        Thread.sleep(5000);
    }

    // Click on the Deposity Check button
    public static void depositCheckNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_MobileCheckDepositNavigation"));
        Reporter.log("Clicking on: Check Deposit");
        Thread.sleep(5000);
    }

    // Click on the Documents button
    public static void documentsNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_DocumentsNavigation"));
        Reporter.log("Clicking on: Documents");
        Thread.sleep(5000);
    }

    // Click on the Insights button
    public static void insightsNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_InsightsNavigation"));

        Thread.sleep(5000);
    }

    // Click on the Info button
    public static void infoNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_InfoNavigation"));
        Reporter.log("Clicking on: Info");
        Thread.sleep(5000);
    }

    // Click on the Markets button
    public static void marketsNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_MarketsNavigation"));
        Reporter.log("Clicking on: Markets");
        Thread.sleep(5000);
    }

    // Click on the Options button
    public static void optionsNavigation() throws InterruptedException {
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_OptionsNavigation"));
        Reporter.log("Clicking on: Options");
        Thread.sleep(5000);
    }
}
