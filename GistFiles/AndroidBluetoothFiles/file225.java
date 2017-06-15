package com.example.mathieu.obdiireader;

        import android.app.Activity;
        import android.app.Application;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.os.Bundle;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.Set;
        import java.util.UUID;

/**
 * Created by Mathieu on 03/06/2017.
 */

public class BTState{

    public static BluetoothAdapter btAdapter;
    public static BluetoothSocket btSocket;
    //UUID de communication par port série
    public static final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String DEVICE_ADDRESS = "device_address";

    public static InputStream in;
    public static OutputStream out;
    public static BufferedReader buff;

    static ArrayList deviceStrs = new ArrayList();
    static final ArrayList devices = new ArrayList();

    static Set<BluetoothDevice> pairedDevices;

    public BTState(){   //constructeur de la classe BTState
        setBtAdapter();
        Log.d("CONSTRUCTOR","BTSTATE"+deviceStrs);
    }

    public void setBtAdapter(){     //récupère les caractéristiques du module Bluetooth intégré au smartphone
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setPaireDevices(){      //met dans pairedDevices la liste des appareils Bluetooth appairés au smartphone
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice btDevice : pairedDevices)
            {
                deviceStrs.add(btDevice.getName() + "\n" + btDevice.getAddress());
                devices.add(btDevice.getAddress());
            }
        }
    }

    public void setIn(){    //enregistre dans in le canal de transmission entrant
        try {
            this.in = this.btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOut(){       //enregistre dans out le canal de transmission sortant
        try {
            this.out = this.btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBuff (){     //enregistre le canal de transmission entrant dans la variable buff
        try {
            this.buff = new BufferedReader(new InputStreamReader(this.btSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}