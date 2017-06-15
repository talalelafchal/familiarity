public class Connectivity {
    /**
     * @return Returns the type of Internet connection device, the possible return values ​​are: 
     * WIFI, WIMAX, MOBILE NETWORKS, BLUETOOTH and ETHERNET.
     */
    public String typeConnection() {

        ConnectivityManager connManager1 = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        ConnectivityManager connManager2 = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        ConnectivityManager connManager3 = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mBluetooth = connManager3.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        ConnectivityManager connManager4 = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mEthernet = connManager4.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        ConnectivityManager connManager5 = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWimax = connManager5.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        if (mWifi.isConnected()) {
            return "WIFI";
        }
        if (mMobile.isConnected()) {
            return "MOBILE NETWORKS";
        }
        if (mBluetooth.isConnected()) {
            return "BLUETOOTH";
        }
        if (mEthernet.isConnected()) {
            return "ETHERNET";
        }
        if (mWimax.isConnected()) {
            return "WIMAX";
        }
        return "NOT CONNECTED";
    }
}
