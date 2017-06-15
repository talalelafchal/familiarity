package idris.android.s3mini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity
{
  public String toastText = "";
  private BluetoothAdapter btAdapter;
  private BluetoothDevice btDevice;
  private BluetoothSocket btSocket;
  private InputStream inputStream;
  private OutputStream outputStream;
  public ProgressDialog progressBar;
  protected static final int DISCOVERY_REQUEST = 1;
  public boolean flag = false;
  public boolean sw1flag = false;
  public TextView textView;
  private Handler textViewHandler = new Handler();
  public boolean led1Mode = true;
  public boolean led2Mode = true;
    
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    setup();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
	
  private void setup()
  {
    final TextView status = (TextView)findViewById(R.id.status);
    final Button connect = (Button)findViewById(R.id.connectBtn);
    final Button disconnect = (Button)findViewById(R.id.disconnectBtn);
    final ToggleButton led1 = (ToggleButton)findViewById(R.id.led1TogBtn);
    final ToggleButton led2 = (ToggleButton)findViewById(R.id.led2TogBtn);
    final TextView sw1 = (TextView)findViewById(R.id.sw1Txt);
    final TextView sw2 = (TextView)findViewById(R.id.sw2Txt);
    	
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    if(btAdapter.isEnabled())
    {
      status.setText("Not Connected");
    }
    else
    {
      status.setText("Turn On Your Bluetooth");
    }
    	
    connect.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        status.setText("Connecting...");
        String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);
        textView = new TextView(v.getContext());
      }
    });
    	
    disconnect.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        try
        {
          btSocket.close();
        }
        catch(Exception closeException)
        {
          toastText = "Socket close failed: " + closeException.getMessage();
          Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
        }
      }
    });
    	
    led1.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        if(led1Mode == false) write((byte)0xA0);
        else if(led1Mode == true) write((byte)0xA1);
        led1Mode = !led1Mode;
      }
    });
    	
    led2.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        if(led2Mode == false) write((byte)0xB0);
        else if(led2Mode == true) write((byte)0xB1);
        led2Mode = !led2Mode;
      }
    });
    	
    int delay = 1000;  // Delay in ms
    int period = 100;  // Repeat in ms
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask()
    {
      public void run()
      {
        if(flag == true)
        {
          final int dataRx = read();
          textViewHandler.post(new Runnable()
          {
            public void run()
            {
              if(dataRx == 0xC0) sw1.setText("SW1 is released");
              else if(dataRx == 0xC1) sw1.setText("SW1 is PRESSED");
              else if(dataRx == 0xD0) sw2.setText("SW2 is released");
              else if(dataRx == 0xD1) sw2.setText("SW2 is PRESSED");
            }
          });
        }
      }
    }, delay, period);
  }
	
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(requestCode==DISCOVERY_REQUEST)
    {
      setup();
      connectToDevice();
    }
  }
    
  private void connectToDevice()
  {
    final TextView status = (TextView)findViewById(R.id.status);
    final EditText text = (EditText)findViewById(R.id.bluetoothName);
    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
    if(pairedDevices.size() > 0)
    {
      for(BluetoothDevice pairedDevice : pairedDevices)
      {
        if(pairedDevice.getName().equals(text.getText().toString()))
        {
          btDevice = pairedDevice;
          UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
          try
          {
            btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
          }
          catch(Exception createException)
          {
            toastText = "Socket create failed: " + createException.getMessage();
            Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
          }
    		        
          try
          {
            String address = btDevice.getAddress();
            status.setText("MAC " + address);
            btSocket.connect();
            flag = true;
          }
          catch(IOException connectException)
          {
            try
            {
              toastText = "Socket connect failed: " + connectException.getMessage();
              Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
              btSocket.close();
            }
            catch(IOException closeException)
            {
              toastText = "Socket close failed: " + closeException.getMessage();
              Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
            }
          }
    		        
          try
          {
            inputStream = btSocket.getInputStream();
            outputStream = btSocket.getOutputStream();
          }
          catch(IOException streamException)
          {
            toastText = "Failed to get input and output streams: " + streamException.getMessage();
            Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
          }
        }
      }
    }
  }
    
  private void write(byte dataWrite)
  {
    try
    {
      outputStream.write(dataWrite);
      Thread.sleep(20);
      outputStream.flush();
    }
    catch(Exception writeException)
    {
      toastText = "Failed to write to output stream: " + writeException.getMessage();
      Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
    }
  }
    
  private void writeString(String data)
  {
    char[] dataChar = new char[data.length()];
    int i;
    dataChar = data.toCharArray();
    for (i = 0; i < data.length(); i++) write((byte)dataChar[i]);
  }
    
  private int read()
  {
    int dataRead = 0;
    try
    {
      dataRead = (int) inputStream.read();
    }
    catch(IOException readException)
    {
      toastText = "Failed to read from input stream: " + readException.getMessage();
      Toast.makeText(Main.this, toastText, Toast.LENGTH_SHORT).show();
    }
    return dataRead;
  }
}
