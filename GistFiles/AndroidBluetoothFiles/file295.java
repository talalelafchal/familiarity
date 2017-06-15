import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                // 取得したbluetooth情報を取得
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);               
                mAdapter.add(device.getAddress());
                mAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {               
            }
        });

        IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothSearchReceiver, bluetoothFilter);

        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery(); //検索開始
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery(); //検索キャンセル
        unregisterReceiver(mBluetoothSearchReceiver); //filter解除
    }
}