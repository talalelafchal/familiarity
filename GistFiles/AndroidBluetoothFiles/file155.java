package icyfox.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 蓝牙测试代码片段
 * icyfox 2015-03-18
 */
public class MainActivity extends Activity{

    private BluetoothAdapter btAdapter;
    private Button btnSearch;
    private BlueToothReceiver recv;

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == btnSearch){ //开始搜索
                btAdapter = BluetoothAdapter.getDefaultAdapter(); //获得系统自带的蓝牙适配器
                if (btAdapter == null){ //如果没有蓝牙
                    showToast("这个手机竟然不支持蓝牙");
                    return;
                }
                btAdapter.enable(); //启动蓝牙适配器
                recv = new BlueToothReceiver(); //添加一个消息接收器
                IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND); //添加消息接收器可以接受的内容
                while (!btAdapter.startDiscovery()); //开始搜索
                showToast("开始搜索蓝牙设备");
                registerReceiver(recv, ifilter); //注册消息接收器
            }
        }
    };

    /**
     * 初始化程序，添加了一个按钮，点击开始搜索蓝牙设备
      * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = (Button) findViewById(R.id.search);
        btnSearch.setOnClickListener(listener);
    }

    /**
     * 工具类显示一个提示文字
     * @param msg
     */
    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * 消息接收器，ACTION_FOUND的意思是，如果搜索到了蓝牙设备会触发这个方法。
     */
    class BlueToothReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String derp = device.getName() + " - " + device.getAddress(); //获得蓝牙设备的名字和MAC地址
                Log.i("bt", derp); //在logcat里打印出来
            }
        }

    }
}
