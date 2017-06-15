public class MyHTTPClientTest extends AndroidTestCase {
    private MockWebServer mServer;
    private MyHTTPClient mClient;
    
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.play();
        mClient = new MyHTTPClient(mServer.getUrl("/"));
    }
    
    public void tearDown() throws Exception {
        mServer.shutdown();
    }
    
    public void testGetString() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody("myString"));
        String str = mClient.getString();
        
        RecordedRequest req = mServer.takeRequest();
        assertEquals("/string", req.getPath());
        assertEquals("GET", req.getMethod());
        assertNotNull(str);
        assertEquals("myString", str);
    }
}