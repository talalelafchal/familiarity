package runners.vk;

import POM.Prestashop.Prestashop_Admin_Login_Page;
import POM.Prestashop.Prestashop_Admin_Main_Page;
import driver.Driver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by Дмитрий on 11.03.2017.
 */

public class Log_In_And_Out_Test {


    public static String email = "webinar.test@gmail.com";
    public static String password = "Xcg7299bnSmMuRLp9ITw";


    public static void main(String args[]) throws InterruptedException {

        Prestashop_Admin_Login_Page prestashop_admin_login_page = new
                Prestashop_Admin_Login_Page("chrome");
        Driver.maximize();
        Prestashop_Admin_Main_Page prestashop_admin_main_page =
                prestashop_admin_login_page.LogIn(email, password);
        prestashop_admin_main_page.Log_out();
        Driver.tearDown();


    }
}