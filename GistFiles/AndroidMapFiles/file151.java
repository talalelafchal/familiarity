package com.jalatif.Chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/17/13
 * Time: 10:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TabbedChat extends FragmentActivity implements Runnable, ViewPager.OnPageChangeListener{

    private ChatPageAdapter pageAdapter;
    private Context ctx;
    private String userN = "";
    private String wUser = "";
    private Timer stsat = new Timer();
    boolean mBound = false;
    protected SocketService mService; //Jalatif Please remove static after trying
    private boolean visible = true;
    private boolean ouserStat = true;
    private Intent mIntent;
    //private Hashtable<String, ChatFragment> getChatFragment = new Hashtable<String, ChatFragment>();
    private ChatFragment cf;
    protected static List<String> titles = new ArrayList<String>();
    private SoundPool notify;
    private int soundID;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatview);
        ctx = getApplicationContext();
        Bundle b = getIntent().getExtras();
        userN = b.getString("UserName");
        wUser = b.getString("toUser");
        //setTitle("Chat B/w " + userN + " and " + wUser);
        setTitle(userN+ "'s Chat");
        mIntent = new Intent(this, SocketService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        int seconds = 2;
        stsat.schedule(new statusCheck(), 0, seconds * 1000);

        Thread onliner = new Thread(this, "Tabb");
        onliner.start();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        notify = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundID = notify.load(this, R.raw.doorbell, 1);

    }

    private void playSound(){
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        android.util.Log.v("SOUND","["+volume+"]["+notify.play(soundID, volume, volume, 1, 0, 1f)+"]");
    }

    private void generateNotification(String from, String msg){
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(ctx, ChatFragment.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("New msg from " + from)
                .setContentText(msg).setSmallIcon(R.drawable.talk)
                .setContentIntent(pIntent).setStyle(new Notification.BigTextStyle().bigText(msg)).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            mService = binder.getService();
            try {
                mService.writeMessage(userN + "SendTo@*@~" + "");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch(NullPointerException npe){
                System.out.println("Got An Npe : " + mService);
            }
            mBound = true;
            System.out.println("Service Connected to TabbedChat");
            //dout = mService.getDout();//new SocketService().getDout();
            //din = mService.getDin();//new SocketService().getDin();
            //socket = mService.getSocket();//new SocketService().getSocket();
            //System.out.println("Jalatif Socket is " + socket);
            if (mService.getChatFragment(wUser) == null){
                cf = new ChatFragment(wUser, mService);
                mService.putFragment(cf);
                mService.putChatFragment(wUser, cf);
                titles.add(0, wUser);
            }
            /*cf = new ChatFragment("anime", mService);
            mService.putFragment(cf);
            getChatFragment.put("anime", cf);
            */
            pageAdapter = new ChatPageAdapter(getSupportFragmentManager(), mService.getFragments());//fragments);

            ViewPager pager = (ViewPager)findViewById(R.id.vpager);

            pager.setAdapter(pageAdapter);
            pager.setCurrentItem(titles.indexOf(wUser));

            //Bind the title indicator to the adapter
            TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.cvtitles);
            titleIndicator.setViewPager(pager);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPageSelected(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
        //setTitle(titles.get(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private class statusCheck extends TimerTask {
        public void run(){
            try {
                if (mService != null && visible){
                    //mService.writeMessage("StsAt@*@~" + wUser);
                    mService.writeMessage("Status@*@~");
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        try {
            while (visible) {
                //dout.writeUTF("Status@*@~");
                if (mService == null)
                    continue;
                System.out.println("Working here");
                String msp = mService.readMessage();//din.readUTF();
                String message = "";
                System.out.println("De Villiers got message : " + msp);
                if (msp.contains("MsgRx~*~@")){
                    int loc = msp.indexOf("MsgRx~*~@");
                    final String from = msp.substring(0, loc);
                    message = msp.substring(loc + 9, msp.length());
                    System.out.println("De Villiers got message : " + message);
                    final String uiMsg = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //To change body of implemented methods use File | Settings | File Templates.
                            if (!uiMsg.equals("")){
                                try{
                                    mService.getChatFragment(from).updateChat(uiMsg);
                                    playSound();
                                }
                                catch(NullPointerException e){
                                    if (mService.getChatFragment(wUser) == null){
                                        cf = new ChatFragment(wUser, mService);
                                        mService.putFragment(cf);
                                        mService.putChatFragment(wUser, cf);
                                        titles.add(0, wUser);
                                        try {
                                            mService.writeMessage("");
                                        } catch (IOException e1) {
                                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                    }
                                }
                            }
                                //getChatFragment.get(from).updateChat(uiMsg);//talk.insert(uiMsg, 0);
                            //talk.add(uiMsg);
                        }
                    });
                }
                /*if(msp.startsWith("StsAt~*~@")){
                    final String status = msp.substring(9, msp.length());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //To change body of implemented methods use File | Settings | File Templates.
                            if (status.equals("true")){
                                //tvStatus.setText("Online");
                                //ouserStat = true;
                            }
                            else{
                                //tvStatus.setText("Offline");
                                //ouserStat = false;
                            }
                        }
                    });
                }*/
                if (msp.startsWith("StsOf~*~@")){
                    message = msp.substring(9, msp.length());
                    String membs[] = message.split("&");
                    Set<String> checkin = new HashSet<String>(Arrays.asList(membs));
                    //this.setUsers(membs);
                    Enumeration<String> e = mService.getUsers();//getChatFragment.keys();
                    while(e.hasMoreElements()){
                        String stt = "";
                        final String checkUser = e.nextElement();
                        if (checkin.contains(checkUser))
                            stt = "true";
                        else
                            stt = "false";
                        final String status = stt;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //To change body of implemented methods use File | Settings | File Templates.
                                if (status.equals("true")){
                                    mService.getChatFragment(checkUser).updateStatus(true);//getChatFragment.get(checkUser).updateStatus(true);
                                    //tvStatus.setText("Online");
                                    //ouserStat = true;
                                }
                                else{
                                    mService.getChatFragment(checkUser).updateStatus(false);//getChatFragment.get(checkUser).updateStatus(false);
                                    //tvStatus.setText("Offline");
                                    //ouserStat = false;
                                }
                            }
                        });

                    }
                }
            }
        }
        catch( IOException ie ) { System.out.println( ie ); }

    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        stsat.cancel();
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
