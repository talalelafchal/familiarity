package billPay;

import io.appium.java_client.AppiumDriver;
import utility.Interaction;
import utility.Utility;
import utility.iOSInit;

import org.openqa.selenium.WebElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.lang.InterruptedException;


public class BillPayClass extends iOSInit {

    /*
 * Area:  Bill Pay
 * Purpose:  This class contains methods to make add users into a bill payment system
 */



    /////////////////////////////////
    ///////// Pay a Bill ///////////
    ///////////////////////////////

    public static void paymentEarliestDate() throws InterruptedException {

        // Select payee from Payee List
        Interaction.byXpathClick(driver, Utility.returnObjectXML("billPay_PayeeList"));

        // Enter values from Scheduled Payment screen

        Interaction.byXpathClick(driver, Utility.returnObjectXML("billPay_SelectAccountField"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("fromAccount"));
        Interaction.byXpathSend(driver, Utility.returnObjectXML("billPay_AmountField"), Utility.returnXML("amount"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_EarliestAvailableDateRB"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_ContinueButton"));

// Verify details on Verification screen



    }
    /////////////////////////////////
    ///////// Manage Payee /////////
    ///////////////////////////////



    public static void addPayeeCompany() throws InterruptedException, ParserConfigurationException, IOException, SAXException, TransformerException {


        //  Click on the Manage tab
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_ManageTab"));


        // Click on the Company button
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_CompanyButton"));

        // Enter information into the Company fields
        Interaction.byXpathSend(driver, Utility.returnXML("companyName"), Utility.returnObjectXML("billPay_CompanyNameField"));
        Interaction.byXpathSend(driver, Utility.returnXML("nickname"), Utility.returnObjectXML("billPay_CompanyNicknameField"));
        Interaction.byXpathSendAccountNumber(driver, Utility.returnObjectXML("billPay_AccountNumberField"));

        Utility.updateXML(driver, "billPay_AccountNumberField", "billPay_AddCompany", "accountNumber");
        Interaction.byXpathSendDuplicateText(driver, Utility.returnObjectXML("billPay_AccountNumberField"), Utility.returnObjectXML("billPay_ConfirmAccountNumberField"));
        Interaction.byXpathSend(driver, Utility.returnXML("companyAddress"), Utility.returnObjectXML("billPay_CompanyAddressField"));
        Interaction.byXpathSend(driver, Utility.returnXML("optionalCompanyAddress"), Utility.returnObjectXML("billPay_OptionalCompanyAddressField"));
        Interaction.byXpathSend(driver, Utility.returnXML("city"), Utility.returnObjectXML("billPay_CityField"));
        Interaction.byXpathClick(driver, Utility.returnObjectXML("billPay_StateDropdown"));
        Interaction.byXpathSend(driver, Utility.returnXML("state"), Utility.returnObjectXML("billPay_StatePickerWheel"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_DoneButton"));
        Interaction.byXpathSend(driver, Utility.returnXML("zipCode"), Utility.returnObjectXML("billPay_ZipCodeField"));
        Interaction.byXpathSend(driver, Utility.returnXML("phoneNumber"), Utility.returnObjectXML("billPay_PhoneNumberField"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_ContinueButton"));


        // Verify verification screen data

        Interaction.byXpathValidate(driver, Utility.returnXML("companyName"), Utility.returnObjectXML("billPay_CompanyVerifyName"));
        Interaction.byXpathValidate(driver, Utility.returnXML("nickname"), Utility.returnObjectXML("billPay_CompanyVerifyNickname"));
        Interaction.byXpathValidate(driver, Utility.returnXML("accountNumber"), Utility.returnObjectXML("billPay_CompanyVerifyAccountNumber"));
        Interaction.byXpathValidate(driver, Utility.returnXML("companyAddress") + System.lineSeparator() + Utility.returnXML("optionalCompanyAddress") + System.lineSeparator() + Utility.returnXML("city") + "," + " " + Utility.returnXML("state") + " " + Utility.returnXML("zipCode"), Utility.returnObjectXML("billPay_CompanyVerifyAddress"));
        Interaction.byXpathValidate(driver, Utility.returnXML("phoneNumberVerify"), Utility.returnObjectXML("billPay_CompanyVerifyPhoneNumber"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("common_SubmitButton"));

        // Verify confirmation screen data
        Interaction.byXpathValidate(driver, Utility.returnXML("companyName"), Utility.returnObjectXML("billPay_CompanyVerifyName"));
        Interaction.byXpathValidate(driver, Utility.returnXML("nickname"), Utility.returnObjectXML("billPay_CompanyVerifyNickname"));
        Interaction.byXpathValidate(driver, Utility.returnXML("accountNumber"), Utility.returnObjectXML("billPay_CompanyVerifyAccountNumber"));
        Interaction.byXpathValidate(driver, Utility.returnXML("companyAddress") + System.lineSeparator() + Utility.returnXML("optionalCompanyAddress") + System.lineSeparator() + Utility.returnXML("city") + "," + " " + Utility.returnXML("state") + " " + Utility.returnXML("zipCode"), Utility.returnObjectXML("billPay_CompanyVerifyAddress"));
        Interaction.byXpathValidate(driver, Utility.returnXML("phoneNumberVerify"), Utility.returnObjectXML("billPay_CompanyVerifyPhoneNumber"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_SchedulePayment"));

    }
    // Verify Payee added to Payee list


    public static void addPayeePerson() throws InterruptedException {

        //  Click on the Manage tab
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_ManageTab"));

        // Click on the Company button
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_PersonButton"));

        // Enter information into the Person fields
        Interaction.byXpathSend(driver, Utility.returnXML("firstName"), Utility.returnObjectXML("billPay_PersonFirstNameField"));
        Interaction.byXpathSend(driver, Utility.returnXML("lastName"), Utility.returnObjectXML("billPay_PersonLastNameField"));
        Interaction.byXpathSend(driver, Utility.returnXML("nickname"), Utility.returnObjectXML("billPay_Person_OptionalNicknameField"));
        Interaction.byXpathSend(driver, Utility.returnXML("personAddress"), Utility.returnObjectXML("billPay_PersonAddressField"));
        Interaction.byXpathSend(driver, Utility.returnXML("optionalPersonAddress"), Utility.returnObjectXML("billPay_PersonOptionalAddressField"));
        Interaction.byXpathSend(driver, Utility.returnXML("city"), Utility.returnObjectXML("billPay_PersonCityField"));
        Interaction.byXpathClick(driver, Utility.returnObjectXML("billPay_PersonStateDropdown"));
        Interaction.byXpathSend(driver, Utility.returnXML("state"), Utility.returnObjectXML("billPay_StatePickerWheel"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_StateDropdownDoneButton"));
        Interaction.byXpathSend(driver, Utility.returnXML("zipCode"), Utility.returnObjectXML("billPay_PersonZipCodeField"));
        Interaction.byXpathSend(driver, Utility.returnXML("phoneNumber"), Utility.returnObjectXML("billPay_PersonPhoneNumberField"));
        Interaction.byNameClick(driver, Utility.returnObjectXML("billPay_ContinueButton"));

    }
}

