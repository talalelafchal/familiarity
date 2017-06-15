package linz.jku;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;


/*
 * AsyncTask class to send data over bluetooth socket.
 * If code == 1, we send the contact.
 * If code == 2, we send the MP3 (Only if we shake the phone meanwhile a song is being played.) NOT WORKING YET.
 * 
 */

public class DataSender extends AsyncTask<Object, Void, String> {

	private BluetoothSocket mmSocket;
	private OutputStream mmOutStream;
	private final String TAG = "DataSender";
	private ProgressDialog progressDialog;

	
	/*
	 * Async Method to start a new thread and send the data.
	 * 
	 */
	
	@Override
	protected String doInBackground(Object... args) {
		Log.i("DATASENDER", "DATA SENDER STARTING");
		mmSocket = (BluetoothSocket) args[0];
		
		this.progressDialog = (ProgressDialog) args[2];
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpOut = mmSocket.getOutputStream();
		} catch (IOException e) {
			Log.e("DataSender", "Error getting OutputStream...");
		}

		mmOutStream = tmpOut;

		try {

			Log.e("DataSender", "WRITING MESSAGE");

			int code = Shaking.getCode();

			if (code != -1) {
				switch (code) {

				// Sending a contact
				case 1:

					byte[] b = Main.getDefaultContact();
					// Sends dataType
					mmOutStream.write(("1").getBytes());

					// Get the length of the message
					Log.v(TAG, "Contacto: " + new String(b));
					int length = b.length;
					mmOutStream.write((String.valueOf(length) + "\n")
							.getBytes());
					mmOutStream.write(b);

					break;

				// Sending a MP3 file: NOT WORKING YET
				case 2:

					Log.v(TAG, "ENVIAMOS EL TIPO");
					mmOutStream.write(("2").getBytes());

					// Get the length of the message

					File mp3file = new File(Shaking.getUri().toString());

					long tam = mp3file.length();

					Log.v(TAG, "ENVIAMOS EL TAMANOO");
					mmOutStream.write((String.valueOf(tam) + "\n").getBytes());
					
					//mmOutStream.write((mp3file.getName() + ".mp3" + "\n").getBytes());

					Log.v(TAG, "MP3 File: " + mp3file.getName()
							+ "  ---  TAM: " + tam);

					InputStream in = null;
					in = new BufferedInputStream(new FileInputStream(mp3file),4096);

					byte[] buf = new byte[1024];
					int read;

					while ((read = in.read(buf)) != -1) {
						mmOutStream.write(buf);						
					}
					mmOutStream.close();
					Shaking.setCode(1);
					
					in.close();
					break;}								
				
			}

		} catch (IOException e) {
			Log.e("DataSender",
					"Error Writing throught outStream...  " + e.getMessage());
		}
		return "Message";
	}

	
	/*
	 * Method that is called just after doInBackground().
	 * Here we close the ProgressDialog and mmSocket. 
	 * 
	 */
	protected void onPostExecute(String result) {
		this.progressDialog.dismiss();
		try {
			this.mmSocket.close();
		} catch (IOException e) {
			Log.e("DataTransfer",
					"EXCEPTION HANDLED CLOSING BLUETOOTH SOCKET ON POST EXECUTE");
			e.printStackTrace();
		}
	}

}