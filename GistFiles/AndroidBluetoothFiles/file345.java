package com.curso.bluetoothlistadpt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/*Example 2*/
import java.util.Set;  
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;  
import android.content.Intent;  
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/*End Example 2*/

public class MainActivity extends Activity {

	/*Example 2*/
	TextView textview1;  
    private static final int REQUEST_ENABLE_BT = 1;  
    BluetoothAdapter btAdapter;
    /*End Example 2*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*Example 2*/
		textview1 = (TextView) findViewById(R.id.textView1);
		final Button button1 = (Button) findViewById(R.id.button1);  
	    final Button button2 = (Button) findViewById(R.id.button2);

	    // Obtiene el Adaptador Bluetooth 
        btAdapter = BluetoothAdapter.getDefaultAdapter(); 
	    
        // Cuando se presiona SHOW DEVICES manda llamar CheckBluetoothState()
        button1.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	textview1.setText("Showing Paired Devices:\n");
                textview1.append("\nAdapter: " + btAdapter); 
            	CheckBluetoothState();
            }  
        });  
        // Limpia el textview1
        button2.setOnClickListener(new View.OnClickListener() {  
         @Override  
            public void onClick(View arg0) {  
        	 textview1.setText("Showing Paired Devices:\n");
            }  
        });
        
      }  
         
      // Se manda llamar cuando el Carga por completo el Activity  
      @Override  
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        //Si esta el REQUEST_ENABLE_BT vuelve a llamar CheckBluetoothState()
        if (requestCode == REQUEST_ENABLE_BT) {  
          CheckBluetoothState();  
        }  
      }  
       
      @Override  
      protected void onDestroy() {  
        super.onDestroy();  
      }  
       
      // Revisa si soporta Bluetooth Muestra la lista de Dispositivos Pareados
      private void CheckBluetoothState() {  
        // Revisa si soporta Bluetooth 
        if(btAdapter==null) {   
          textview1.append("\nBluetooth NOT supported.");  
          return;  
        } else {  
        	//Revisa Bluetooth si esta encencido
          if (btAdapter.isEnabled()) {  
            textview1.append("\nBluetooth is enabled...");  
            
            /* LISTA DE DISPOSITIVOS PAREADOS */
            textview1.append("\nPaired Devices are:"); 
            // Crea una lista de Dispositivos Pareados
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices(); 
            // Se crea un for para mostrar en el Etiquetado.
            for (BluetoothDevice device : devices) {  
              textview1.append("\n\nDevice: \n" + device.getName() + ", " + device);  
            }  
            /* END LISTA */
          } else {  
            //Muestra al usuario que encienda Bluetooth 
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  
          }  
        }  
		/*End Example 2*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
