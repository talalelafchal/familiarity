public class ListenerService extends WearableListenerService {
    //Remember to decler it as well in Manifest.xml
    private static final String TAG = ListenerService.class.getSimpleName();
    
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if ("/ourAppDatabase".equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                byte[] realmAsset = item.getDataMap().getByteArray("realmDatabase");
                if(realmAsset != null){
                    toFile(realmAsset);
                    //I recommend here calling a Broadcast - getBaseContext().sendBroadcast(new Intent(yourbroadcast here));
                }
            }
        }
    }

    private void toFile(byte [] byteArray){
        File writableFolder = ListenerService.this.getFilesDir();
        File realmFile = new File(writableFolder, Realm.DEFAULT_REALM_NAME);
        if (realmFile.exists()) {
            realmFile.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(realmFile.getPath());
            fos.write(byteArray);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.d(TAG, "toFile exception: " + e.getLocalizedMessage());
        }
    }
}
