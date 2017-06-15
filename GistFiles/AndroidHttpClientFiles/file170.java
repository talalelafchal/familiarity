
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import cucumber.annotation.en.*;
import junit.framework.Assert;

public class StepDefinitions {


    private int result = 0;
     private int int_amt = 0 ;


  @Given("^I have \\$(\\d+) in my account$")
    public void I_have_$_in_my_account(int dollars) {
                int_amt = dollars;
      System.out.println("@ given methid " + int_amt   );
         }

   @When("^I deposit \\$(\\d+) in my account$")
    public void I_wave_my_magic_ring_at_the_machine(int deposit) {
       result = deposit+int_amt+1 ;

       System.out.println("@ when method " + result );
        }


   @Then("^I should see \\$(\\d+) in my account$")
    public void I_should_see_$_in_my_account(int var_m) {


       Assert.assertEquals(var_m , result );

       System.out.println("@ then methid " + result   );
    }
        }


