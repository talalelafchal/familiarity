package com.jalatif.Chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/14/13
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class AvailableUsers extends Activity implements ListView.OnItemClickListener, Runnable {
    private ListView lvOnline;
    private ListView lvOffline;
    private Context ctx;
    protected Hashtable getChatWindow = new Hashtable();
    private Set<String> buddy;
    private String toUser = "";
    private String userN = "";
    private Thread stat;
    private Timer timer;// = new Timer();
    //private Socket socket;
    //private DataOutputStream dout;
    //private DataInputStream din;
    boolean mBound = false;
    private SocketService mService;
    private boolean visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.availableusers);
        userN = getIntent().getExtras().getString("username");
        setTitle(userN + "'s Available Users");
        initVars();
        ctx = this;
        visible = true;
        //final String ap[] = {"Asd", "Sad", "ewr", "ewrwer"};
        //final String ap2[] = {"Abhinav", "Abhishek", "Digvijay", "Anoop", "Anshuman", "Naman", "Naveen", "Rahul"};
        //lvOnline.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ap));
        //lvOffline.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ap2));
        lvOnline.setOnItemClickListener(this);
        lvOffline.setOnItemClickListener(this);

        //Intent mIntent = new Intent(this, SocketService.class);
        //bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        /*int seconds = 1;
        timer.schedule(new statusCheck(), 0, seconds*1000);

        //stat = new Thread(this, "Status");
        stat = new Thread(this, "Status");
        stat.start();
        */
    }

    @Override
    protected void onStart() {
        super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
        visible = true;
        Intent mIntent = new Intent(this, SocketService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        int seconds = 1;
        timer = new Timer();
        timer.schedule(new statusCheck(), 0, seconds*1000);

        //stat = new Thread(this, "Status");
        stat = new Thread(this, "Status");
        stat.start();

    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            mService = binder.getService();
            mBound = true;
            System.out.println("Service Connected to AvailableUsers");
            //dout = mService.getDout();//new SocketService().getDout();
            //din = mService.getDin();//new SocketService().getDin();
            //socket = mService.getSocket();//new SocketService().getSocket();
            //System.out.println("Jalatif Socket is " + socket);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        while(visible){
            try {
                if (mService == null)
                    continue;
                String msp = mService.readMessage();//din.readUTF();
                String message = "";
                if (msp.startsWith("AlUsr~*~@")){
                    message = msp.substring(9, msp.length());
                    String all[] = message.split("&");
                    buddy = new HashSet<String>(Arrays.asList(all));
                }
                if (msp.startsWith("StsOf~*~@")){
                    message = msp.substring(9, msp.length());
                    String membs[] = message.split("&");
                    this.setUsers(membs);

                }


            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void setUsers(String[] users){
        final String usrs[] = users;
        Set<String> online = new HashSet<String>(Arrays.asList(users));
        buddy.removeAll(online);
        String buds[] = new String[buddy.size()];
        buddy.toArray(buds);
        final String budd[] = buds;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                lvOnline.setAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, usrs));
                lvOffline.setAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, budd));
            }
        });
    }


    private class statusCheck extends TimerTask {
        public void run(){
            try {
                if (mService != null && visible){
                    mService.writeMessage("AlUsr@*@~");
                    mService.writeMessage("Status@*@~");
                }
                //textField1.setText(toUser);
                //if (dout!=null){
                //    dout.writeUTF("AlUsr@*@~");
                //    dout.writeUTF("Status@*@~");
                //}
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void initVars(){
        lvOnline = (ListView) findViewById(R.id.lvOnline);
        lvOffline = (ListView) findViewById(R.id.lvOffline);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //To change body of implemented methods use File | Settings | File Templates.
        //System.out.println("MyView = " + String.valueOf(view.getId()) + "Parent = " + String.valueOf(parent.getId()) + "Position = " + String.valueOf(position) + String.valueOf(R.id.lvOnline) + String.valueOf(R.id.lvOffline));
        //Intent chatWindow = new Intent(ctx, ChatWindow.class);
        Intent chatWindow = new Intent(ctx, TabbedChat.class);
        switch(parent.getId()){
            case R.id.lvOnline:
                toUser = (String) lvOnline.getItemAtPosition(position);
                break;
            case R.id.lvOffline:
                toUser = (String) lvOffline.getItemAtPosition(position);
                break;
        }
        chatWindow.putExtra("toUser", toUser);
        chatWindow.putExtra("UserName", userN);
        startActivity(chatWindow);
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        timer.cancel();
        visible = false;
        if (mBound){
            unbindService(mConnection);
            mBound = false;
        }
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        if (mBound){
            unbindService(mConnection);
            mBound = false;
        }
        //stopService(new Intent(this, SocketService.class));
    }
}
