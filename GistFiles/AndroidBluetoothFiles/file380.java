package tech.sungkim.drop;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothConnector {

    public void simpleComm(Context cont,BluetoothAdapter bluetoothAdapter, BluetoothDevice blueToothDevice, BluetoothSocket socket, Integer port, String var ) {
        bluetoothAdapter.cancelDiscovery();

        Log.d(this.toString(), "Port = " + port);
        try {
            // This is a hack to access "createRfcommSocket which is does not
            // have public access in the current api.
            // Note: BlueToothDevice.createRfcommSocketToServiceRecord (UUID
            // uuid) does not work in this type of application. .
            Method m = blueToothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            socket = (BluetoothSocket) m.invoke(blueToothDevice, port);

            // debug check to ensure socket was set.
            assert (socket != null) : "Socket is Null";

            // attempt to connect to device
            socket.connect();
            try {
                Log.d(cont.toString(), "************ CONNECTION SUCCESS! *************");

                // Grab the outputStream. This stream will send bytes to the
                // external/second device. i.e it will sent it out.
                // Note: this is a Java.io.OutputStream which is used in several
                // types of Java programs such as file io.
                OutputStream outputStream = socket.getOutputStream();
                String message = var;

                // Convert the message to bytes and blast it through the
                // bluetooth to the second device. Might use :
                // public byte[] getBytes (Charset charset) for proper String to
                // byte conversion.
                outputStream.write(message.getBytes());

//                    String string = textView.getText().toString();
//                    string.concat("\n");
//                    try {
//                        outputStream.write(string.getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    textView.append("\nSent Data:"+string+"\n");

            } finally {
                // close the socket and we are done.
                socket.close();
            }
            // IOExcecption is thrown if connect fails.
        } catch (IOException ex) {
            Log.e(this.toString(), "IOException " + ex.getMessage());
            // NoSuchMethodException IllegalAccessException
            // InvocationTargetException
            // are reflection exceptions.
        } catch (NoSuchMethodException ex) {
            Log.e(this.toString(), "NoSuchMethodException " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            Log.e(this.toString(), "IllegalAccessException " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            Log.e(this.toString(),
                    "InvocationTargetException " + ex.getMessage());
        }
    }
}
