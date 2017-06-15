import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView)findViewById(R.id.listView);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices(); //ペアリグデバイス群取得
		for(BluetoothDevice device : paireDevices){
			mAdapter.add( device.getAddress() );
		}
		mListView.setAdapter(mAdapter);
	}
}
