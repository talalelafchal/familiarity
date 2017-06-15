package com.jalatif.Chat;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/16/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocketService extends Service {
    private String host = "";
    private int port ;
    private String logTxt = "";
    private boolean connected = false;
    private Socket socket;
    private DataOutputStream dout;
    private DataInputStream din;
    private int result = Activity.RESULT_CANCELED;
    private Context ctx;
    private List<Fragment> fragment_list= new ArrayList<Fragment>();
    private Hashtable<String, ChatFragment> userChatMap = new Hashtable<String, ChatFragment>();

    IBinder mBinder = new SocketBinder();

    public class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }
    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        host = extras.getString("Host");
        port = extras.getInt("Port");
        ctx = this;
        System.out.println("Trying Connection to " + host + " port = " + String.valueOf(port));
        connected = false;
        MyTask mt = new MyTask();
        mt.execute();
        try {
            mt.get(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Connected = " + connected + logTxt + " Socket = " + socket);
        //Toast.makeText(this, logTxt, 500);
        Messenger messenger = (Messenger) extras.get("MESSENGER");
        Message msg = Message.obtain();
        msg.arg1 = result;
        msg.obj = logTxt;
        try {
            messenger.send(msg);
        } catch (RemoteException e1) {
            Log.w(getClass().getName(), "Exception sending message", e1);
        }

        return super.onStartCommand(intent, flags, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void showToast(String log){
        Toast.makeText(ctx, log, Toast.LENGTH_SHORT).show();
    }
    protected class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);    //To change body of overridden methods use File | Settings | File Templates.
            showToast(logTxt);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                System.out.println("Trying Connection to " + host + " port = " + String.valueOf(port));
                socket = new Socket( host, port );
                dout = new DataOutputStream(socket.getOutputStream());
                din = new DataInputStream(socket.getInputStream());
                result = Activity.RESULT_OK;
                logTxt = "Connection can be made";
                connected = true;
            }
            catch(UnknownHostException uhe){
                logTxt = "Unknow host : " + host;
            }
            catch (ConnectException ce){
                logTxt = "Wrong Port Number";
            }
            catch (IOException e) {
                logTxt = "Invalid Host Name";
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch(IllegalArgumentException iae){
                logTxt = "Port out of range";
            }
            System.out.println("Socket = " + socket + "Logtxt = " + logTxt);
            publishProgress();
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    protected DataOutputStream getDout(){
        return dout;
    }

    protected DataInputStream getDin(){
        return din;
    }

    protected Socket getSocket(){
        return socket;
    }

    protected void writeMessage(String msg) throws IOException {
        dout.writeUTF(msg);

    }

    protected String readMessage() throws IOException {
        return din.readUTF();
    }

    protected void putFragment(Fragment f){
        //fragment_list.add(f);
        fragment_list.add(0, f);
    }

    protected Fragment getFragmentAt(int position){
        return fragment_list.get(position);
    }

    protected List<Fragment> getFragments(){
        return fragment_list;
    }

    protected Boolean removeFragment(Fragment f){
        return fragment_list.remove(f);
    }


    protected void putChatFragment(String user, ChatFragment cf){
        userChatMap.put(user, cf);
    }

    protected ChatFragment getChatFragment(String user){
        return userChatMap.get(user);
    }

    protected Enumeration<String> getUsers(){
        return userChatMap.keys();
    }

}
