public final class NetworkUtils {

    public static boolean isOnline() {
        NetworkInfo networkInfo = ((ConnectivityManager) Application.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (null == networkInfo || !networkInfo.isConnectedOrConnecting()) {
            Log.d(NetworkUtils.class.getName(), "No network connection");
            return false;
        }

        return true;
    }
}