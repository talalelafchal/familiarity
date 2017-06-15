import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RestClientTest {
    private String url = "http://server:port/";
    private RestClient client;
	
    @Before
    public void setUp() throws Exception {
        client = new RestClient(url);
    }

    @Test
    public void testGet() {
        Response response = null;
        try {
            response = client.get(new Request("interests/6"));
	} catch (Exception e) {
	    fail(e.toString);
	}
		
	assertEquals(response.getResponseCode(), 200);
	System.out.print(response.getResponse());
    }

    @Test
    public void testPost() {
	Response response = null;
	try {
	    Request r = new Request("resource/");
	    r.addParam("name", "TestName");
	    response = client.post(r);
	} catch (Exception e) {
	    fail(e.toString);
	}
		
	assertEquals(response.getResponseCode(), 201);
	System.out.print(response.getResponse());
    }
}