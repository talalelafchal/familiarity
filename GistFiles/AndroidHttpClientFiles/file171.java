import org.junit.*;
import static org.junit.Assert.*;


/**
 * Created by root on 17.06.14.
 */
public class LogPassTestCase {
    private LogPassTest login;

    @Before
    public void setUp(){
        login = new LogPassTest();
       // pass = new LogPassTest();
    }

    @Test
    public void testLogin(){
        LogPassTest login = new LogPassTest();
        //assertNull();
        assertEquals(null, login.Login(login.toString()));
    }


}
