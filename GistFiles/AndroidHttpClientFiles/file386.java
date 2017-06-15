Working with AsyncTasks on Android 
from category Android
Introduction
Developing Android applications is a lot of fun because it offers a lot of possibilities to implement one's own ideas. Frequently i have to use AsyncTasks to do heavy work aside the ui thread. In the following i give a quick overview on AsyncTasks and provide some code snippets that might be useful for everyday development with Android regarding the use of AsyncTasks.
Lifecycle
An AsyncTask in Android is used to perform background operations and publish results on the UI thread without having to manipulate threads and/or handlers (more information). The structure follows call onPreExecute() for preparation, doInBackground() for doing the main task and return its result finally to the onPostExecute() method. Optional you can implement the onProgressUpdate() method e.g. to show a progressbar. 
 
 
The following example can be used as template for an AsyncTask. 
private class MyTask extends AsyncTask<Object, Void, Object> {
     @Override
     protected void onPreExecute() {
     }
 
     @Override
     protected String doInBackground(Object... objects) {
         // do something with objects 
         return objects[0];
     } 
 
    @Override
    protected void onPostExecute(Object result) {
        System.out.println(result.toString());
     }
 
    @Override
     protected void onProgressUpdate() {
     }
}
To call the AsyncTask use the following code.
// someObjects will be passed to doInBackground method
new MyTask().execute(someObjects);
The type parameters of an AsyncTask like AsyncTask<Param1, Param2, Param3> correspond to:
•  Param1: type for parameter of doInBackground method
•	Param2: type for parameter of onProgressUpdate method (optional, if not required just use Void)
•	Param3: type for parameter of onPostExecute method, the return of doInBackground method
 
AsyncTask with constructor
Instead of passing parameters for an AsyncTask via the doInBackground method parameter you can also use an constructor for your task. That practice might be useful, if you have many parameters.
private class MyTask extends AsyncTask<Void, Void, Object> {
 
     Object param1, param2;
 
     public MyTask(Object param1, Object param2) {
         this.param1 = param1;
         this.param2 = param2;
     }
 
     @Override
     protected void onPreExecute() {
     }
 
     @Override
     protected String doInBackground() {
         // do some work with objects param1 and param2
         return param1;
     } 
 
    @Override
    protected void onPostExecute(Object result) {
        System.out.println(result.toString());
     }
 
    @Override
     protected void onProgressUpdate() {
     }
}
Call the the task with the following code.
// paramters are passed via constructor to the task
MyTask theTask =  new MyTask(object1, object2);
theTask.execute();

 
AsyncTask with callback
The following example shows a task with a callback to return a value. The callback is used, because the task works asynchronous and the UI thread does not wait for a return value of the task. An activity using the AsyncTask has to implement a callback interface (e.g. like AsyncTaskCompleteListener).
interface AsyncTaskCompleteListener<T> {
   public void onTaskComplete(T result);
}
class MainActivity implements AsyncTaskCompleteListener<String> {
 
    // Called after task finished
    public void onTaskComplete(T result) {
        // do whatever you need with your result
        System.out.println(result);
    }
 
    public void launchTask(String url) {
        TaskWithCallback taskWithCallback = new TaskWithCallback(context, this);
        taskWithCallback.execute(url);
    }
}
class TaskWithCallback extends AsyncTask<String, Void, String> {
 
    private AsyncTaskCompleteListener<String> callback;
 
    @Override
    public A(Context context, AsyncTaskCompleteListener<String> callback) {
        this.context = context;
        this.callback = callback;
    }
 
    @Override
    protected String doInBackground(Object... objects) {
        // do work here
        // [...]
        return "Some String";
     }
 
    @Override
    protected void onPostExecute(String result) {
       callback.onTaskComplete(result);
   }  
}
AsyncTask with progress notification to download a file
The following example shows the complete usage of an AsyncTask which downloads a PDF file with showing the progress in a load dialog:
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
 
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
 
/***
 * Loads a pdf file in an ASyncTask and displays the progress
 * 
 * @author Alexander Cremer
 */
public class DownloadPDF extends AsyncTask<Void, Integer, String> {

 
    ProgressDialog  mProgressDialog;
    private Context appContext;

    private String  downloadURL;

 
    public DownloadPDF(Context appContext, String downloadURL) {

        this.appContext = appContext;
        this.downloadURL = downloadURL;
    }
 
    @Override
    protected void onPostExecute(String pdfFile) {

        mProgressDialog.dismiss();
        if (pdfFile != null) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(pdfFile);

            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            this.appContext.startActivity(Intent.createChooser(intent,
                    "Choose a pdf viewer"));
        }
 
    }
 
    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(this.appContext);
        mProgressDialog.setMessage("Initialize download...");
        mProgressDialog.setTitle("Download");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        cancel(true);
                    }
                });
        mProgressDialog.show();
    }
 
    @Override
    protected void onProgressUpdate(Integer... progress) {

        mProgressDialog
                .setMessage("Download is running...");
        mProgressDialog.setProgress(progress[0]);
    }
 
    @Override
    protected String doInBackground(Void... params) {

        try {
            HttpEntity entity;
            DefaultHttpClient httpclient = new DefaultHttpClient();
 
            String outputFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            File downloadingFile = new File(outputFolder, "downloadedFile.pdf");
	    /*
	     *  Possible to do a login here
	     */
            Credentials upc = new UsernamePasswordCredentials("loginUser","loginPassword");
            httpclient.getCredentialsProvider().setCredentials(
                            new AuthScope(Constants.hostName, 443,
                                    AuthScope.ANY_SCHEME), upc);
            HttpGet httpgetDownload = new HttpGet(downloadURL);
            BasicScheme basicAuth = new BasicScheme();
            HttpHost targetHost = new HttpHost(Constants.hostName, 443, "https");
            httpgetDownload.addHeader(basicAuth.authenticate(upc, httpgetDownload));
            HttpResponse response = httpclient.execute(targetHost, httpgetDownload);
            entity = response.getEntity();
 
            if (entity != null) {
                InputStream inputStream = entity.getContent();

                // Determine filesize
                URL url = new URL(downloadURL);

                URLConnection connection = url.openConnection();

                connection.connect();
                int fileLength = connection.getContentLength();
                // Stop if filesize is zero
                if (fileLength < 0)
                    return null;     
                FileOutputStream out = new FileOutputStream(downloadingFile);
                byte[] buffer = new byte[1024];
                int count = -1;
                long total = 0;
                while ((count = inputStream.read(buffer)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    out.write(buffer, 0, count);
                    if (isCancelled()) {
                        downloadingFile.delete();
                        break;
                    }
                }
                out.flush();
                out.close();
            }
            return downloadingFile.getPath();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }
}
Execute the task like this:
DownloadPDF downladPDFTask = new DownloadPDF(getApplicationContext(), "http://url.to.download.a.pdf")
downladPDFTask.execute()

