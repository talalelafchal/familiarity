    public static String readQRImage(Activity activity, Bitmap bitmap) {
        String contents = "";

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(activity)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        SparseArray<Barcode> detectedBarcodes = barcodeDetector.detect(new Frame.Builder()
                .setBitmap(bitmap)
                .build());

        if (detectedBarcodes.size() > 0 && detectedBarcodes.valueAt(0) != null) {
            contents = detectedBarcodes.valueAt(0).rawValue;
        }
        return contents;
    }