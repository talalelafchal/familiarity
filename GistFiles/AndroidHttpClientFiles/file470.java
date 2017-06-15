
import static AJAJ.ajaj;

public class AJAJTest {
  
  /**
   * This method will post "Hello" and "Good Morning" to example.com and print the response
   */
  public void run() {
    ajaj("example.com", new Callback() {
        public void callback(JSONObject json) throws JSONException {
          System.out.println(json);
        }
      },new String[][]{{"message","Hello"},{"message2","Good Morning"}});
  }
  
}
