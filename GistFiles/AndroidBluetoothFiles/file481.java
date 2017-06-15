public final class RxGattWrapper {
    private SerializedSubject<BluetoothGattEvent, BluetoothGattEvent> eventSubject = new SerializedSubject<>(PublishSubject.<BluetoothGattEvent>create());
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;

    public RxGattWrapper(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void connect(Context context) {
        bluetoothGatt = bluetoothDevice.connectGatt(context, false, new RxBluetoothGattCallback());
    }

    public void writeToCharacteristic(byte[] data, UUID service, UUID characteristic) {
        BluetoothGattService gattService = bluetoothGatt.getService(service);
        if (gattService != null) {
            BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(characteristic);
            gattCharacteristic.setValue(data); // TODO Handle failure
            bluetoothGatt.writeCharacteristic(gattCharacteristic); // TODO Handle failure
        }
    }

    public Observable<BluetoothGattEvent> asObservable() {
        return eventSubject.asObservable();
    }

    public static final class BluetoothGattEvent {
        public EventType eventType;
        public int rssi;
        public int mtu;
        public int status;
        public BluetoothGattDescriptor descriptor;
        public BluetoothGattCharacteristic characteristic;
        public int connectionState;
    }

    public enum EventType {
        MtuChanged, ReadRemoteRssi, ReliableWriteCompleted, DescriptorWrite, DescriptorRead, CharacteristicChanged, CharacteristicWrite, CharacteristicRead, ServicesDiscovered, ConnectionStateChange
    }

    private class RxBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.MtuChanged;
            event.mtu = mtu;
            eventSubject.onNext(event);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.ReadRemoteRssi;
            event.rssi = rssi;
            eventSubject.onNext(event);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.ReliableWriteCompleted;
            event.status  = status;
            eventSubject.onNext(event);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.DescriptorWrite;
            event.status = status;
            event.descriptor = descriptor;
            eventSubject.onNext(event);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.DescriptorRead;
            event.status = status;
            eventSubject.onNext(event);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.CharacteristicChanged;
            event.characteristic = characteristic;
            eventSubject.onNext(event);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.CharacteristicWrite;
            event.status = status;
            event.characteristic = characteristic;
            eventSubject.onNext(event);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.CharacteristicRead;
            event.status = status;
            event.characteristic = characteristic;
            eventSubject.onNext(event);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.ServicesDiscovered;
            event.status = status;
            eventSubject.onNext(event);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BluetoothGattEvent event = new BluetoothGattEvent();
            event.eventType = EventType.ConnectionStateChange;
            event.status = status;
            event.connectionState = newState;
            eventSubject.onNext(event);
            if(status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices(); // TODO Handle failure or discovery start
            }
        }
    }
}