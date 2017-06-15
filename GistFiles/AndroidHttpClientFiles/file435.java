import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

/**
 * Created by JasoosJagga on 8/6/2016.
 */

public class Steps {
    private int x;
    private int y;

    @Given("i have $x rupees in one pocket")
    public void givenIhaveXRupeesInOnePocket(int x) {
this.x=x;
    }

    @Given("i have $y rupees in another pocket")
    public void givenIHave20RupeesInAnotherPocket(int y) {
this.y=y;    }

    @When("i combine the money from both pockets")
    public void whenICombineTheMoneyFromBothPockets() {
        System.out.println("Combining money............wait....");
    }

    @Then("i found that i have total of $z rupees")
    public void thenIFoundThatIHaveTotalOf30Rupees(int z) {
        Assert.assertEquals(x+y,z);
    }

}
