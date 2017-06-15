//Source: http://stackoverflow.com/a/20784322
public static void setKitKatWebViewProxy(Context appContext, String host, int port) {
    System.setProperty("http.proxyHost", host);
    System.setProperty("http.proxyPort", port + "");
    System.setProperty("https.proxyHost", host);
    System.setProperty("https.proxyPort", port + "");
    try {
        Class applictionCls = Class.forName("android.app.Application");
        Field loadedApkField = applictionCls.getDeclaredField("mLoadedApk");
        loadedApkField.setAccessible(true);
        Object loadedApk = loadedApkField.get(appContext);
        Class loadedApkCls = Class.forName("android.app.LoadedApk");
        Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
        receiversField.setAccessible(true);
        ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
        for (Object receiverMap : receivers.values()) {
            for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                Class clazz = rec.getClass();
                if (clazz.getName().contains("ProxyChangeListener")) {
                    Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                    Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);

                    /*********** optional, may be need in future *************/
                    final String CLASS_NAME = "android.net.ProxyProperties";
                    Class cls = Class.forName(CLASS_NAME);
                    Constructor constructor = cls.getConstructor(String.class, Integer.TYPE, String.class);
                    constructor.setAccessible(true);
                    Object proxyProperties = constructor.newInstance(host, port, null);
                    intent.putExtra("proxy", (Parcelable) proxyProperties);
                    /*********** optional, may be need in future *************/

                    onReceiveMethod.invoke(rec, appContext, intent);
                }
            }
        }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (NoSuchFieldException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    } catch (InstantiationException e) {
        e.printStackTrace();
    }
}