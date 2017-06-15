public StorageService(Context context) {
    mContext = context;		
	try {
		mClient = new MobileServiceClient("https://mobileserviceurl.azure-mobile.net/", "applicationkey", mContext);
		mTableTables = mClient.getTable("Tables");		
		mTableTableRows = mClient.getTable("TableRows");
		mTableContainers = mClient.getTable("BlobContainers");
		mTableBlobs = mClient.getTable("BlobBlobs");
	} catch (MalformedURLException e) {
		Log.e(TAG, "There was an error creating the Mobile Service. Verify the URL");
	}
}