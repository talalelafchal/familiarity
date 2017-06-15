package com.riot.projetoriotboothrfid;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
    Socket nsocket; //Network Socket
    InputStream nis; //Network Input Stream
    OutputStream nos; //Network Output Stream
    BufferedReader inFromServer;//Buffered reader to store the incoming bytes

    Boolean connected=false;//stores the connectionstatus

    NetworkTask networktask;//networktask is the included class to handle the socketconnection

    /** Called when the activity is first created. */

    @Override
    protected void onPreExecute() {
        //change the connection status to "connected" when the task is started
        //connect the view and the objects
        Log.d("APPLog","Starting Client");//log that the App launched
        changeConnectionStatus(true);

    }

    public void sendData(String valueOfseekbar){
        SendDataToNetwork("set:"+valueOfseekbar+'\n');
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {
            SocketAddress sockaddr = new InetSocketAddress("192.168.0.11", 8080);
            nsocket = new Socket();
            Log.d("APPLog","Socket");//log that the App launched
            nsocket.connect(sockaddr, 5000);//connect and set a 10 second connection timeout
            if (nsocket.isConnected()) {//when connected
                Log.d("APPLog","Conectado");//log that the App launched
                nis = nsocket.getInputStream();//get input
                nos = nsocket.getOutputStream();//and output stream from the socket
                inFromServer = new BufferedReader(new InputStreamReader(nis));//"attach the inputstreamreader"
                SendDataToNetwork("Conecta ai");
                while(true){//while connected
                    String msgFromServer = inFromServer.readLine();//read the lines coming from the socket
                    doGetMessage(msgFromServer);
                }
            }
            //catch exceptions
        } catch (IOException e) {
            e.printStackTrace();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = true;
        } finally {
            closeSocket();
        }
        return result;
    }

    //Method closes the socket
    public void closeSocket(){
        try {
            nis.close();
            nos.close();
            nsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method tries to send Strings over the socket connection
    public void SendDataToNetwork(String cmd) { //You run this from the main thread.
        try {
            if (nsocket.isConnected()) {
                nos.write(cmd.getBytes());
            } else {
                outputText("SendDataToNetwork: Cannot send message. Socket is closed");
            }
        } catch (Exception e) {
            outputText("SendDataToNetwork: Message send failed. Caught an exception");
        }
    }

    private void doGetMessage(String command) {
        outputText("Recebendo Socket");//log
        if (command != "") {//if the recieved data is at least one byte
            if(command.indexOf("setPoti:")==0){//if the string starts with "setPoti"
                command=command.replace("setPoti:", "");//remove the command
                User user = new User();
                user.setrfid(command);
            }
            if(command.indexOf("setFoto:")==0){//if the string starts with "setPoti"
                Instruction inst = Instruction.instance();
                inst.mudaCamera();
            }
        }
    }

    //Methods is called everytime a new String is recieved from the socket connection
    @Override
    protected void onProgressUpdate(byte[]... values) {
        outputText("onProgress");//log        ;
        if (values.length > 0) {//if the recieved data is at least one byte
            String command=new String(values[0]);//get the String from the recieved bytes
            Log.d("APPLog",command);
            outputText(command);
            if(command.indexOf("setPoti:")==0){//if the string starts with "setPoti"
                command=command.replace("setPoti:", "");//remove the command
            }
            if(command.indexOf("setFoto:")==0){//if the string starts with "setPoti"
                command=command.replace("setFoto:", "");//remove the command
                Instruction inst = Instruction.instance();
                inst.mudaCamera();
            }
        }
    }

    //Method is called when task is cancelled
    @Override
    protected void onCancelled() {
        changeConnectionStatus(false);//change the connection to "disconnected"
    }

    //Method is called after taskexecution
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            outputText("onPostExecute: Completed with an Error.");
            Instruction inst = Instruction.instance();
            if(inst != null)  { // your activity can be seen, and you can update it's context
                inst.voltaSplash();
            }
        } else {
            outputText("onPostExecute: Completed.");
        }
        changeConnectionStatus(false);//change connectionstaus to disconnected
    }

    public void changeConnectionStatus(Boolean isConnected) {
        connected=isConnected;//change variable
        if(isConnected){//if connection established
            outputText("successfully connected to server");//log
        }else{
            outputText("disconnected from Server!");//log
            Instruction inst = Instruction.instance();
            if(inst != null)  { // your activity can be seen, and you can update it's context
                inst.voltaSplash();
            }
        }
    }

    //Method appends changes the value of the progressbar
    public void setPoti(int position) {

    }

    //Method appends text to the textfield and adds a newline character
    public void outputText(String msg) {
        Log.d("APPLog", msg);
    }

}