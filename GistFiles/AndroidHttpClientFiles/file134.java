/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 11/01/13
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

  public class FileDownloader {

        private static final Logger LOG = Logger.getLogger(FileDownloader.class);
        private WebDriver driver;
        private String localDownloadPath = "/Users/lunanueva/Documents/mulesoft/"; //System.getProperty("java.io.tmpdir");
        private boolean followRedirects = true;
        private boolean mimicWebDriverCookieState = true;
        private int httpStatusOfLastDownloadAttempt = 0;

        public FileDownloader(WebDriver driverObject) {
            this.driver = driverObject;
        }

        /**
         * Specify if the FileDownloader class should follow redirects when trying to download a file
         *
         * @param value
         */
        public void followRedirectsWhenDownloading(boolean value) {
            this.followRedirects = value;
        }

        /**
         * Get the current location that files will be downloaded to.
         *
         * @return The filepath that the file will be downloaded to.
         */
        public String localDownloadPath() {
            return this.localDownloadPath;
        }

        /**
         * Set the path that files will be downloaded to.
         *
         * @param filePath The filepath that the file will be downloaded to.
         */
        public void localDownloadPath(String filePath) {
            this.localDownloadPath = filePath;
        }

        /**
         * Download the file specified in the href attribute of a WebElement
         *
         * @param element
         * @return
         * @throws Exception
         */
        public String downloadFile(String element) throws Exception {
            return downloader(null, element);
        }

        /**
         * Download the image specified in the src attribute of a WebElement
         *
         * @param element
         * @return
         * @throws Exception
         */
        public String downloadImage(WebElement element) throws Exception {
            return downloader(element, "src");
        }

        /**
         * Gets the HTTP status code of the last download file attempt
         *
         * @return
         */
        public int getHTTPStatusOfLastDownloadAttempt() {
            return this.httpStatusOfLastDownloadAttempt;
        }

        /**
         * Mimic the cookie state of WebDriver (Defaults to true)
         * This will enable you to access files that are only available when logged in.
         * If set to false the connection will be made as an anonymouse user
         *
         * @param value
         */
        public void mimicWebDriverCookieState(boolean value) {
            this.mimicWebDriverCookieState = value;
        }

        /**
         * Load in all the cookies WebDriver currently knows about so that we can mimic the browser cookie state
         *
         * @param seleniumCookieSet
         * @return
         */
        private BasicCookieStore mimicCookieState(Set <org.openqa.selenium.Cookie> seleniumCookieSet) {
            BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
            for (org.openqa.selenium.Cookie seleniumCookie  :  seleniumCookieSet) {
                BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
                duplicateCookie.setDomain(seleniumCookie.getDomain());
                duplicateCookie.setSecure(seleniumCookie.isSecure());
                duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
                duplicateCookie.setPath(seleniumCookie.getPath());
                mimicWebDriverCookieStore.addCookie(duplicateCookie);
            }

            return mimicWebDriverCookieStore;
        }

      private String getCSRF(Set <org.openqa.selenium.Cookie> seleniumCookieSet ){
           String returnCSRF = "";
           String  nameCSRF = "";

          for (org.openqa.selenium.Cookie seleniumCookie  :  seleniumCookieSet) {
              nameCSRF= seleniumCookie.getName();

              System.out.println("name CSRF : " + nameCSRF);

              if ( seleniumCookie.getName().contains("CSRF_TOKEN")) {
                  System.out.println("value  CSRF : " + seleniumCookie.getValue());
                  returnCSRF=seleniumCookie.getValue();
              }



          }
          return returnCSRF;
      }

        /**
         * Perform the file/image download.
         *
         * @param element
         * @param attribute
         * @return
         * @throws IOException
         * @throws NullPointerException
         */
        private String downloader(WebElement element, String attribute) throws IOException, NullPointerException, URISyntaxException {


          //  String fileToDownloadLocation = element.getAttribute(attribute);
            String fileToDownloadLocation = attribute;

            if (fileToDownloadLocation.trim().equals("")) throw new NullPointerException("The element you have specified does not link to anything!");

            URL fileToDownload = new URL(fileToDownloadLocation);
            File downloadedFile = new File(this.localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
            if (downloadedFile.canWrite() == false) downloadedFile.setWritable(true);

            HttpClient client = new DefaultHttpClient();
            BasicHttpContext localContext = new BasicHttpContext();

            LOG.info("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
            if (this.mimicWebDriverCookieState) {
                localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
            }

            HttpGet httpget = new HttpGet(fileToDownload.toURI());
            httpget.setHeader("Host" , "sfdl-load.s3.amazonaws.com");
            httpget.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0");
            httpget.setHeader("Accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpget.setHeader("Accept-Language" , "es-MX,es;q=0.8,en-us;q=0.5,en;q=0.3");
            httpget.setHeader("Accept-Encoding" , "gzip, deflate");
            httpget.setHeader("Accept-Charset" , "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            httpget.setHeader("Connection" , "keep-alive");
            //httpget.setHeader("X-CSRF-Token" , getCSRF(this.driver.manage().getCookies()));
            //httpget.setHeader("X-Requested-With" , "XMLHttpRequest");
            httpget.setHeader("Referer" , "https://sfdataloader-load.cloudhub.io/static/");






            HttpParams httpRequestParameters = httpget.getParams();
            httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, this.followRedirects);

            httpget.setParams(httpRequestParameters);

            LOG.info("Sending GET request for: " + httpget.getURI());
            HttpResponse response = client.execute(httpget, localContext);
            this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
            LOG.info("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
            LOG.info("Downloading file: " + downloadedFile.getName());
            FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
            response.getEntity().getContent().close();

            String downloadedFileAbsolutePath = downloadedFile.getAbsolutePath();
            LOG.info("File downloaded to '" + downloadedFileAbsolutePath + "'");

            return downloadedFileAbsolutePath;
        }

      /**
       * Perform the file/image download.
       *
       *
       * @param attribute
       * @return
       * @throws IOException
       * @throws NullPointerException
       */
      public String downloadString( String attribute) throws IOException, NullPointerException, URISyntaxException {


          //  String fileToDownloadLocation = element.getAttribute(attribute);
          String fileToDownloadLocation = attribute;
          String strResponse =  "";
          StringWriter writer = new StringWriter();

          if (fileToDownloadLocation.trim().equals("")) throw new NullPointerException("The element you have specified does not link to anything!");

          URL fileToDownload = new URL(fileToDownloadLocation);
          File downloadedFile = new File(this.localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
          if (downloadedFile.canWrite() == false) downloadedFile.setWritable(true);

          HttpClient client = new DefaultHttpClient();
          BasicHttpContext localContext = new BasicHttpContext();

          LOG.info("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
          if (this.mimicWebDriverCookieState) {
              localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
          }

          HttpGet httpget = new HttpGet(fileToDownload.toURI());
          httpget.setHeader("Host" , "sfdataloader-load.cloudhub.io");
          httpget.setHeader("User-Agent" , "Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0");
          httpget.setHeader("Accept" , "application/json, text/javascript, */*; q=0.01");
          httpget.setHeader("Accept-Language" , "es-MX,es;q=0.8,en-us;q=0.5,en;q=0.3");
          httpget.setHeader("Accept-Encoding" , "gzip, deflate");
          httpget.setHeader("Accept-Charset" , "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
          httpget.setHeader("Connection" , "keep-alive");
          httpget.setHeader("X-CSRF-Token" , getCSRF(this.driver.manage().getCookies()));
          httpget.setHeader("X-Requested-With" , "XMLHttpRequest");
          httpget.setHeader("Referer" , "https://sfdataloader-load.cloudhub.io/static/");






          HttpParams httpRequestParameters = httpget.getParams();
          httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, this.followRedirects);

          httpget.setParams(httpRequestParameters);

          LOG.info("Sending GET request for: " + httpget.getURI());
          HttpResponse response = client.execute(httpget, localContext);
          this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
          LOG.info("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
          LOG.info("Downloading file: " + downloadedFile.getName());

          writer = new StringWriter();

          IOUtils.copy(response.getEntity().getContent(), writer);
          strResponse = writer.toString();


          //FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
          response.getEntity().getContent().close();

         // String downloadedFileAbsolutePath = downloadedFile.getAbsolutePath();
          //LOG.info("File downloaded to '" + downloadedFileAbsolutePath + "'");

          return strResponse;
      }

    }

