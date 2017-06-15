//完美的解决如何让AsyncTask终止操作
//http://www.oschina.net/question/54100_28516


package com.isummation.exampleapp;
  
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.UnknownHostException;
  
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
  
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
  
public class UserLogin extends Activity {
  
    private EditText etUsername;
    private EditText etPassword;
    private ProgressDialog progressDialog;
    private static final int PROGRESSDIALOG_ID = 0;
    private static final int SERVER_ERROR = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int CANCELLED = 3;
    private static final int SUCCESS = 4;
    private String ServerResponse;
    private LoginTask loginTask;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
  
        etUsername = (EditText) findViewById(R.id.txt_username);
        etPassword = (EditText) findViewById(R.id.txt_password);
  
        Button login_button = (Button) this.findViewById(R.id.login_button);
        login_button.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (etUsername.getText().toString().length() == 0
                        || etPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter username and password",
                            Toast.LENGTH_SHORT).show();
                } else {
  
                    //Show dialog by passing id
                    showDialog(PROGRESSDIALOG_ID);
                }
            }
        });
    }
  
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESSDIALOG_ID:
            removeDialog(PROGRESSDIALOG_ID);
  
            //Please note that forth parameter is true for cancelable Dialog
            //Also register cancel event listener
            //if the litener is registered then forth parameter has no effect
            progressDialog = ProgressDialog.show(UserLogin.this, "Authenticating",
                    "Please wait...", true, true, new OnCancelListener(){
  
                        public void onCancel(DialogInterface dialog) {
                            //Check the status, status can be RUNNING, FINISHED and PENDING
                            //It can be only cancelled if it is not in FINISHED state
                            if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
                                loginTask.cancel(true);
                        }
                    });
            break;
        default:
            progressDialog = null;
        }
        return progressDialog;
    }
  
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case PROGRESSDIALOG_ID:
            //check if any previous task is running, if so then cancel it
            //it can be cancelled if it is not in FINISHED state
            if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
                loginTask.cancel(true);
            loginTask = new LoginTask(); //every time create new object, as AsynTask will only be executed one time.
            loginTask.execute();
        }
    }
  
    class LoginTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                ServerResponse = null; //don't forget to make it null, as task can be called again
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(
                        getString(R.string.WebServiceURL)
                                + "/cfc/iphonewebservice.cfc?returnformat=json&method=validateUserLogin&username="
                                + URLEncoder.encode(etUsername.getText()
                                        .toString(), "UTF-8")
                                + "&password="
                                + URLEncoder.encode(etPassword.getText()
                                        .toString(), "UTF-8"));
                httpClient.getParams().setParameter(
                        CoreProtocolPNames.USER_AGENT,"Some user agent string");
  
                //call it just before you make server call
                //calling after this statement and canceling task will no meaning if you do some update database kind of operation
                //so be wise to choose correct place to put this condition
                //you can also put this condition in for loop, if you are doing iterative task
  
                //now this very important
                //if you do not put this condition and not maintaining execution, then there is no meaning of calling .cancel() method
                //you should only check this condition in doInBackground() method, otherwise there is no logical meaning
                if (isCancelled())
                {
                    publishProgress(CANCELLED); //Notify your activity that you had canceled the task
                    return (null); // don't forget to terminate this method
                }
                HttpResponse response = httpClient.execute(httpGet,
                        localContext);
  
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));
                ServerResponse = reader.readLine();
                publishProgress(SUCCESS); //if everything is Okay then publish this message, you may also use onPostExecute() method
            } catch (UnknownHostException e) {
                removeDialog(PROGRESSDIALOG_ID);
                e.printStackTrace();
                publishProgress(NETWORK_ERROR);
            } catch (Exception e) {
                removeDialog(PROGRESSDIALOG_ID);
                e.printStackTrace();
                publishProgress(SERVER_ERROR);
            }
            return (null);
        }
  
        @Override
        protected void onProgressUpdate(Integer... errorCode) {
            switch (errorCode[0]) {
            case CANCELLED:
                removeDialog(PROGRESSDIALOG_ID);
                Toast.makeText(getApplicationContext(), "Cancelled by user",
                        Toast.LENGTH_LONG).show();
                break;
            case NETWORK_ERROR:
                removeDialog(PROGRESSDIALOG_ID);
                Toast.makeText(getApplicationContext(), "Network connection error",
                        Toast.LENGTH_LONG).show();
                break;
            case SERVER_ERROR:
                removeDialog(PROGRESSDIALOG_ID);
                Toast.makeText(getApplicationContext(), "Server error",
                        Toast.LENGTH_LONG).show();
                break;
            case SUCCESS:
                removeDialog(PROGRESSDIALOG_ID);
                try {
                    if (ServerResponse != null) {
                        JSONObject JResponse = new JSONObject(ServerResponse);
                        String sMessage = JResponse.getString("MESSAGE");
                        int success = JResponse.getInt("SUCCESS");
                        if (success == 1) {
                            //proceed further
  
                            //you may start new activity from here
                            //after that you may want to finish this activity
                            UserLogin.this.finish();
  
                            //Remember when you finish an activity, it doesn't mean that you also finish thread or AsynTask started within that activity
                            //So you must implement onDestroy() method and terminate those threads.
                        } else {
                            //just showing invalid username password from server response
                            Toast.makeText(getApplicationContext(), sMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Server error",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            }
        }
  
        @Override
        protected void onPostExecute(Void unused) {
  
        }
    }
  
    @Override
    protected void onDestroy(){
        //you may call the cancel() method but if it is not handled in doInBackground() method
        if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
            loginTask.cancel(true);
        super.onDestroy();
    }
}