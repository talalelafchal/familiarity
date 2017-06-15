import POM.Prestashop.Prestashop_Admin_Login_Page;
import POM.Prestashop.Prestashop_Admin_Main_Page;
import driver.Driver;

/**
 * Created by Дмитрий on 01.04.2017.
 */
public class Check_Working_Admin_Menu {

    public static String email = "webinar.test@gmail.com";
    public static String password = "Xcg7299bnSmMuRLp9ITw";


    public static void main (String args[]) throws InterruptedException {

        Prestashop_Admin_Login_Page prestashop_admin_login_page = new
                Prestashop_Admin_Login_Page("chrome");
        Driver.maximize();
        Prestashop_Admin_Main_Page prestashop_admin_main_page =
                prestashop_admin_login_page.LogIn(email, password);
        prestashop_admin_main_page.CheckMenu();
        prestashop_admin_main_page.Log_out();
        Driver.tearDown();

    }
}

