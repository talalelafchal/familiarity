boolean BluetoothOnOff(boolean status, Context context) {
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		try {
			if (status) {
				if (!mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.enable();
				}
			} else {
				if (mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.disable();
				}
			}
			return true;
		} catch (Exception e) {
			Log.e("Bluetooth", "error turning on/off bluetooth");
			return false;
		}
	}