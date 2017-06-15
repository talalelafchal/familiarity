UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

Log.d(TAG, "Enumerating connected devices...");

UsbDevice mUsbDevice;

// Getting the CareLink UsbDevice object
Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
while (deviceIterator.hasNext()) {
  mUsbDevice = deviceIterator.next();
    if (mUsbDevice.getVendorId() == VENDOR_ID && mUsbDevice.getProductId() == PRODUCT_ID) {
      break;
    }
}