package com.example.mathieu.obdiireader;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class main extends AppCompatActivity implements View.OnClickListener{

    Button btnValues,btnQuestions,btnInitBT;
    private String stringHelp = "On this page you can either : \n" +
            "      - press the \"Values\" button to open a page on which you will be able to display the informations you desire\n"+
            "      - press the \"Questions\" button to show up the list of questions you might want to know about your engine\n" +
            "      -pres the \"Connect to the device\" button and then select the OBDII device to establish the connection\n" +
            "\n" +
            "!!!   WARNING  !!!  You need to establish the connection before doing anything else";

    public BTState BT;    //BTState object used to save the Bluetooth datas to retrieve them in other classes

    final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //Universal identifier for serial port communication

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //method called when Main is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //displays activity_main
		
		//initialization of the views
        btnValues = (Button)findViewById(R.id.btnValues);
		btnQuestions = (Button)findViewById(R.id.btnQuestions);
		btnInitBT = (Button)findViewById(R.id.btnInitBT);
		
		//make the Button clickable
        btnValues.setOnClickListener(this);     
		btnQuestions.setOnClickListener(this);
		 btnInitBT.setOnClickListener(this);
       
        BT = new BTState();     //creation of the BT object

        enableBT();     //calls the method enableBT to activate Bluetooth if is not already activated
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { 	//called at the creation of Main
        getMenuInflater().inflate(R.menu.menu_main, menu);      //adds the menu menu_main to the activity activity_main
        return true;
    }


    @Override
    public void onClick(View v) {       //when a view is clicked
        int i = v.getId();      //stores the id of the clicked view
        switch(i){
            case R.id.btnValues:        //if the Button pressed is the Button "Values"
                Intent intV = new Intent(getApplicationContext(),Values.class);     //creates an intent to naviguate to Values.class
                startActivity(intV);        //Runs the intent to switch class
                break;
            case R.id.btnQuestions:     //if the Button pressed is the Button "Questions"
                Intent intQ = new Intent(getApplicationContext(),questions.class);      //creates an intent to naviguate to questions.class
                startActivity(intQ);        //Runs the intent to switch class
                break;
            case R.id.btnInitBT:        //if the Button pressed is the Button "Connection to the device"
                    initBT();       //calls initBT
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){        //called when the help button is pressed
        switch (item.getItemId()) {
            case R.id.action_main_help:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);       //Creates a dialog
                builder1.setTitle("Need help ?");       //Defines the title
                builder1.setMessage(stringHelp);        //the dialog message will be the string variable stringHelp
                builder1.setCancelable(true);       //The dialog can be exited with the smartphone's back key
                builder1.setNeutralButton(android.R.string.ok,      //One "OK" button 
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();        //closes the dialog when the "OK" button is pressed
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();     //displays the dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void enableBT(){
        if (BT.btAdapter == null) {     //if the bluetooth doesn't exist on the smartphone
            Toast.makeText(getApplicationContext(), "Bluetooth is not available on this device", Toast.LENGTH_SHORT).show();
        }

        if (!BT.btAdapter.isEnabled()) {        //If the bluetooth isn't activated
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);     //Creates a predefined dialog to activate Bluetooth
            startActivityForResult(enableBtIntent, 1);      //Displays the dialog and activates the bluetooth if the user decided so
        }
    }


    public void initBT (){      //when the button "connect to a bluetooth device" is pressed
        BT.setPaireDevices();       //calls the method setPairedDevices in BTState class
        
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(main.this);     //creates a dialog
        //displays all the paired devices of the smartphone
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice,BT.deviceStrs.toArray(new String[BT.deviceStrs.size()]));
        //only one device can be selected
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String deviceAddress = (String) BT.devices.get(position);       //gets the MAC adress of the selected device
                String address = deviceAddress.substring(deviceAddress.length() - 17);      //modifies the adress to get connected to the device
                BT.DEVICE_ADDRESS = address;        //updates the DEVICE_ADDRESS variable in BTState 
                connect(address);       //calls the connect method to get connected 
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();     //displays the dialog
    }


    public void connect(String txt){        //method used to get connected to a device

        BluetoothDevice device = BT.btAdapter.getRemoteDevice(txt);     //creates a Bluetooth objet with a MAC adress
        try {
            BT.btSocket = device.createRfcommSocketToServiceRecord(sppUuid);        //Creates a communication socket between the two devices
        } catch (IOException ex) {
            sendLogMessage("Failed to create RfComm socket: " + ex.toString());
            return;
        }
        try {
            BT.btSocket.connect();      //Connection between the two devices
        } catch (IOException ex) {
            sendLogMessage("Failed to connect to RfComm socket: " + ex.toString());
            return;
        }
        sendLogMessage("Your smartphone is connected to the device");
        try {
            BT.out = BT.btSocket.getOutputStream();     //saves the outputStream of the socket 
            BT.setOut();        //updates the outputStream in BTState
            BT.in = BT.btSocket.getInputStream();       //saves the InputStream of the socket 
            BT.setIn();     //updates the InputStream in BTState
            BT.buff = new BufferedReader(new InputStreamReader(BT.in));     //saves a variable used to read the datas of the InputStream easily
            BT.setBuff();       //updates the BufferedReader in BTState
        } catch (IOException e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendLogMessage(String txt){        //method used to display a simple message on the screen
        Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT).show();
    }

}
