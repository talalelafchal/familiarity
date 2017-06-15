import  java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import  java.net.URL;
import java.util.Map;
import java.util.List;

public class Experiment {

   public static final int HTTP_TEMPORARY_REDIRECT = 307; // Temporary Redirect (since HTTP/1.1)
   public static final int HTTP_PERMANENT_REDIRECT = 308; // Permanent Redirect (RFC 7538)

   public static void main(String[] arg) {
      try {

         String location = "http://realraum.at"; // does a 307 to https
         HttpURLConnection urlConnection = null;
         URL url = null;

         do{
            url = new URL(location);
            urlConnection = (HttpURLConnection) url.openConnection();
            //HttpURLConnection.setFollowRedirects(true);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
               || urlConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
               || urlConnection.getResponseCode() == HTTP_TEMPORARY_REDIRECT
               || urlConnection.getResponseCode() == HTTP_PERMANENT_REDIRECT) {
               // TODO: ^ think about more relevant status codes

               location = urlConnection.getHeaderField("Location");
               System.out.println(location == null ? "no location found" : location);

         } else {
            // TODO: introduce boolean flag for this:
            location = null;
         }

      } while(location != null);

         // should be a 200 ...
         System.out.println(urlConnection.getResponseCode());

         urlConnection.disconnect();
      } catch(Exception e) {
         System.out.println(e.toString());
      }
   }
}
