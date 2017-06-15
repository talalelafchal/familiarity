/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 14/01/13
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */

import org.openqa.selenium.*;

public class ApiDataLoaderProxy {

    private WebDriver driver = null;
    private String taskId = "";
    private String runId = "";
    private String downloadLink = "" ;
    private FileDownloader downloader = null;




  public ApiDataLoaderProxy(WebDriver context )  {

      setDriver( context);
      setDownloader(new FileDownloader(getDriver()));


  }


   public String initRunId () {

       String strJsontmp = "";
       String returnRunId = "";
       int initid = 0 ;

       try {


       strJsontmp = getDownloader().downloadString("https://sfdataloader-load.cloudhub.io/api/runs?task_id=" + getTaskId());
           System.out.println("RunId json: " +  strJsontmp);
           if (strJsontmp.contains("\"id\":")) {
               initid = strJsontmp.lastIndexOf("\"id\":") ;
               returnRunId = strJsontmp.substring(initid+5 ,strJsontmp.length()-2 );


           }

       }catch(Throwable exc) {

           exc.printStackTrace();
           //throws exc;


       }
       System.out.println("RunId Actual: " +  returnRunId);
       setRunId(returnRunId);
        return returnRunId;


   }

    public String initSuccessDownloadLink () {

        String returnDownloadLink = "";
        String strJsontmp = "";
        int initid = 0 ;

       try {

           strJsontmp = getDownloader().downloadString("https://sfdataloader-load.cloudhub.io/api/runs/" + getRunId() + "/download?file=successes_file");

           System.out.println("DownloadLinkId json: " +  strJsontmp);
           if (strJsontmp.contains("{\"")) {
               initid = strJsontmp.lastIndexOf("{\"download_link\":\"") ;
               returnDownloadLink = strJsontmp.substring(initid+18 ,strJsontmp.length()-2 );


           }

       }catch (Throwable exc) {
           exc.printStackTrace();

       }
        System.out.println("downloadLink Actual: " +  returnDownloadLink);
        setDownloadLink(returnDownloadLink);
        return returnDownloadLink;


    }

    public void downloadSuccessFile() throws Exception {

        try {
          getDownloader().downloadFile(getDownloadLink());
        }catch(Exception exc) {
            exc.printStackTrace();
            throw exc;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public FileDownloader getDownloader() {
        return downloader;
    }

    public void setDownloader(FileDownloader downloader) {
        this.downloader = downloader;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
