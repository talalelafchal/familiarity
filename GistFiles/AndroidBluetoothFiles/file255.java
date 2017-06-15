
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.UUID;
/**
* Helper which enables or disables BLE gatt updates from connected peripheral, not tested in production
*/
public class GattUpdatesHelper {
    public static final String TAG = GattUpdatesHelper.class.getSimpleName();
    public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @NonNull
    private final BluetoothGatt mBluetoothGatt;
    private final boolean enableDebug;

    public GattUpdatesHelper(@NonNull BluetoothGatt mBluetoothGatt, boolean enableDebug) {
        this.mBluetoothGatt = mBluetoothGatt;
        this.enableDebug = enableDebug;
    }

    public boolean enableUpdates(BluetoothGattCharacteristic characteristic) {
        return setCharacteristicNotificationOrIndication(characteristic, true);
    }

    public boolean disableUpdates(BluetoothGattCharacteristic characteristic) {
        return setCharacteristicNotificationOrIndication(characteristic, false);
    }

    public boolean setCharacteristicNotificationOrIndication(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (enableDebug) {
            Log.d(TAG, "setCharacteristicNotificationOrIndication: " + characteristic.getUuid().toString() + (enable ? " enable" : " disable"));
        }

        boolean success = this.mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        if (!success) {
            if (enableDebug) {
                Log.e(TAG, "Failed to set proper notification status for characteristic with UUID: : " + characteristic.getUuid());
            }
            return false;
        } else {
            return this.writeDescriptorForNotificationOrIndication(characteristic, enable);
        }

    }

    private boolean writeDescriptorForNotificationOrIndication(BluetoothGattCharacteristic ch, boolean enabled) {
        BluetoothGattDescriptor descriptor = ch.getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR);
        if (descriptor == null) {
            return false;
        } else {
            int properties = ch.getProperties();
            byte[] success;
            if ((16 & properties) != 0) {
                success = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(success);
            } else if ((32 & properties) != 0) {
                success = enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(success);
            }

            boolean success1 = this.writeDescriptor(descriptor);
            return success1;
        }
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return this.mBluetoothGatt.writeDescriptor(descriptor);
    }
}
