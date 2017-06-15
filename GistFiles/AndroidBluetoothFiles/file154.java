package linz.jku;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DataReceiver extends AsyncTask<Object, Void, byte[]> {

	private BluetoothSocket mmSocket;
	private InputStream mmInStream;
	private final static String TAG = "DataReceiver";
	private Context CONTEXT;

	@Override
	protected byte[] doInBackground(Object... args) {

		Log.i("DataReceiver", "DataReceiver Launched.");
		this.mmSocket = (BluetoothSocket) args[0];
		this.CONTEXT = (Context) args[1];
		InputStream tmpIn = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = mmSocket.getInputStream();
		} catch (IOException e) {
			Log.e(TAG, "Error getting InputStream... " + e.getMessage());
		}

		mmInStream = tmpIn;

		byte[] buffer = null; // buffer store for the stream

		/*
		 * First: Read what type of data we're receiving. Only one byte.
		 */
		byte[] rawDataType = new byte[1];
		try {
			mmInStream.read(rawDataType);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int dataType = new Integer(new String(rawDataType)); // Decoding byte.
		/*
		 * Type 1 is for Contact. 2 for MP3.
		 */
		if (dataType == 1) {
			try {
				Log.d(TAG, "Type is CONTACT");
				// Reads bytes to contact file. We couldn't save it to a
				// temporary folder where Android address book could read, so
				// you need a sdcard plugged in your phone.
				readToFileFromBuffer(Environment.getExternalStorageDirectory()
						.toString() + File.separator + "contact.vcf");
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			launchContactAdder();

		} else if (dataType == 2) {
			// Reads bytes to a file in sdcard. Since this method isn't working
			// properly, no filename is read from stream and file is stored
			// statically in sdcard only.
			Log.d(TAG, "Type is MP3");
			readToFileFromBuffer(Environment.getExternalStorageDirectory()
					.toString() + File.separator + "music.mp3");

		}

		return buffer;
	}

	/**
	 * Method to read bytes from the inputStream to the given path.
	 * 
	 * @param path
	 *            absolute path where to store bytes.
	 */
	private void readToFileFromBuffer(String path) {
		try {
			// Opens the file, and if it exists, just overwrite it.
			File file = new File(path);
			if (file.exists())
				file.delete();
			FileOutputStream fOutputStream = new FileOutputStream(file);

			byte[] buffer = new byte[1024]; // Read buffer.

			if (mmInStream.available() > 0) {
				mmInStream.read(buffer, 0, 1024);
			}
			/*
			 * First token of 1024 bytes stores this information in this order:
			 * - Integer with the size of the content sending. - "\n" separator.
			 * - Bytes of the content. So, this is the decoding steps:
			 */
			int sizeOfContent = Integer.valueOf(new String(new String(buffer)
					.split("\n")[0])) + 1;
			// Calculating offset to read bytes of content.
			int offset = (new String(String.valueOf(sizeOfContent)).getBytes().length) + 1;
			byte[] content = new byte[1024 - offset]; // Allocating space to
														// read content.
			Log.v(TAG, "Size of content: " + sizeOfContent);

			/*
			 * First if case runs if content size is bigger to be sent in the
			 * first token. Most cases will use second case.
			 */
			if (sizeOfContent > (1024 - offset)) {
				/*
				 * Reassign content bytes from buffer to content before writing
				 * to file.
				 */
				int counterBytes = 0;
				for (int i = 0; i < (1024 - offset); i++) {
					content[i] = buffer[i + offset];
					counterBytes++;
				}
				// Writing current content to file.
				fOutputStream.write(content);
				content = new byte[1024];

				int nextLoopContentSize;
				while (counterBytes < (sizeOfContent - 1)) {
					// If next loop's the last one, we change buffer size to
					// match.
					if ((sizeOfContent - counterBytes) < 1024) {
						nextLoopContentSize = sizeOfContent - counterBytes - 1;
						buffer = new byte[nextLoopContentSize];
					} else {
						nextLoopContentSize = 1024;
					}
					if (mmInStream.available() > 0) {
						mmInStream.read(buffer, 0, nextLoopContentSize);
					}
					counterBytes = counterBytes + buffer.length;
					fOutputStream.write(buffer);
				}

			} else {
				/*
				 * Reassign content bytes from buffer to content before writing
				 * to file.
				 */
				content = new byte[sizeOfContent - 2];
				for (int i = 0; i < content.length; i++) {
					content[i] = buffer[i + offset];
				}
				fOutputStream.write(content);
			}
			return;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return;
	}

	/**
	 * Launchs Android activity to add contact to address book.
	 */
	private void launchContactAdder() {
		Intent i = new Intent();
		i.setAction(android.content.Intent.ACTION_VIEW);

		i.setDataAndType(
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory().toString()
						+ File.separator + "contact.vcf"), "text/x-vcard");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CONTEXT.startActivity(i);
	}

	@Override
	protected void onPostExecute(byte[] buffer) {
		try {
			this.mmSocket.close();
		} catch (IOException e) {
			Log.e("DataReceiver",
					"EXCEPTION HANDLED CLOSING BLUETOOTH SOCKET ON POST EXECUTE");
			e.printStackTrace();
		}
	}

}