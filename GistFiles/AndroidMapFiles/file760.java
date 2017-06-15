public class FileSender extends AsyncTask<Void, Void, Void> {
    private Asset asset;
    private Context context;
    private GoogleApiClient mGoogleAppiClient;
    private static final String TAG = "AssetsSender";

    public FileSender(Asset asset, Context context) {
        this.asset = asset;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        sendData(asset);
        return null;
    }

    @Override
    protected void onPreExecute() {
        mGoogleAppiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        mGoogleAppiClient.connect();
    }

    private void sendData(Asset asset) {
        if(asset == null){
            return;
        }
        PutDataMapRequest dataMap = PutDataMapRequest.create("/ourAppDatabase");
        byte[] arr = asset.getData();
        dataMap.getDataMap().putByteArray("realmDatabase", arr);//for some reason .putAsset wasn't working for me in some cases
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "onResult result:" + dataItemResult.getStatus());
            }
        });
    }

    public static void syncRealm(Context context){
        File writableFolder = context.getFilesDir();
        File realmFile = new File(writableFolder, Realm.DEFAULT_REALM_NAME);
        Asset realAsset = Tools.assetFromFile(realmFile);
        new FileSender(realAsset, context).execute();
    }
}