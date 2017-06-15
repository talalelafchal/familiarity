import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.http.HttpHost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

/**
 * setup proxy in webview
 * @see <a href="http://stackoverflow.com/questions/4488338/webview-android-proxy">
 * http://stackoverflow.com/questions/4488338/webview-android-proxy</a>
 */
public class WebViewHttpProxy {

	private static final String LOG_TAG = WebViewHttpProxy.class.getSimpleName();

	public static boolean setProxy(WebView webview, String host, int port) {
	    // 3.2 (HC) or lower
	    if (Build.VERSION.SDK_INT <= 13) {
	        return setProxyUpToHC(webview, host, port);
	    }
	    // ICS: 4.0
	    else if (Build.VERSION.SDK_INT <= 15) {
	        return setProxyICS(webview, host, port);
	    }
	    // 4.1-4.3 (JB)
	    else if (Build.VERSION.SDK_INT <= 18) {
	        return setProxyJB(webview, host, port);
	    }
	    // 4.4 (KK)
	    else {
	        return setProxyKK(webview, host, port, "android.app.Application");
	    }
	}

	/**
	 * Set Proxy for Android 3.2 and below.
	 */
	@SuppressWarnings("all")
	private static boolean setProxyUpToHC(WebView webview, String host, int port) {
	    Log.d(LOG_TAG, "Setting proxy with <= 3.2 API.");

	    HttpHost proxyServer = new HttpHost(host, port);
	    // Getting network
	    Class networkClass = null;
	    Object network = null;
	    try {
	        networkClass = Class.forName("android.webkit.Network");
	        if (networkClass == null) {
	            Log.e(LOG_TAG, "failed to get class for android.webkit.Network");
	            return false;
	        }
	        Method getInstanceMethod = networkClass.getMethod("getInstance", Context.class);
	        if (getInstanceMethod == null) {
	            Log.e(LOG_TAG, "failed to get getInstance method");
	        }
	        network = getInstanceMethod.invoke(networkClass, new Object[]{webview.getContext()});
	    } catch (Exception ex) {
	        Log.e(LOG_TAG, "error getting network: " + ex);
	        return false;
	    }
	    if (network == null) {
	        Log.e(LOG_TAG, "error getting network: network is null");
	        return false;
	    }
	    Object requestQueue = null;
	    try {
	        Field requestQueueField = networkClass
	                .getDeclaredField("mRequestQueue");
	        requestQueue = getFieldValueSafely(requestQueueField, network);
	    } catch (Exception ex) {
	        Log.e(LOG_TAG, "error getting field value");
	        return false;
	    }
	    if (requestQueue == null) {
	        Log.e(LOG_TAG, "Request queue is null");
	        return false;
	    }
	    Field proxyHostField = null;
	    try {
	        Class requestQueueClass = Class.forName("android.net.http.RequestQueue");
	        proxyHostField = requestQueueClass
	                .getDeclaredField("mProxyHost");
	    } catch (Exception ex) {
	        Log.e(LOG_TAG, "error getting proxy host field");
	        return false;
	    }

	    boolean temp = proxyHostField.isAccessible();
	    try {
	        proxyHostField.setAccessible(true);
	        proxyHostField.set(requestQueue, proxyServer);
	    } catch (Exception ex) {
	        Log.e(LOG_TAG, "error setting proxy host");
	    } finally {
	        proxyHostField.setAccessible(temp);
	    }

	    Log.d(LOG_TAG, "Setting proxy with <= 3.2 API successful!");
	    return true;
	}

	@SuppressWarnings("all")
	private static boolean setProxyICS(WebView webview, String host, int port) {
	    try
	    {
	        Log.d(LOG_TAG, "Setting proxy with 4.0 API.");

	        Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
	        Class params[] = new Class[1];
	        params[0] = Class.forName("android.net.ProxyProperties");
	        Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

	        Class wv = Class.forName("android.webkit.WebView");
	        Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
	        Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webview);

	        Class wvc = Class.forName("android.webkit.WebViewCore");
	        Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
	        Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

	        Class bf = Class.forName("android.webkit.BrowserFrame");
	        Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
	        Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

	        Class ppclass = Class.forName("android.net.ProxyProperties");
	        Class pparams[] = new Class[3];
	        pparams[0] = String.class;
	        pparams[1] = int.class;
	        pparams[2] = String.class;
	        Constructor ppcont = ppclass.getConstructor(pparams);

	        updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));

	        Log.d(LOG_TAG, "Setting proxy with 4.0 API successful!");
	        return true;
	    }
	    catch (Exception ex)
	    {
	        Log.e(LOG_TAG, "failed to set HTTP proxy: " + ex);
	        return false;
	    }
	}

	/**
	 * Set Proxy for Android 4.1 - 4.3.
	 */
	@SuppressWarnings("all")
	private static boolean setProxyJB(WebView webview, String host, int port) {
	    Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API.");

	    try {
	        Class wvcClass = Class.forName("android.webkit.WebViewClassic");
	        Class wvParams[] = new Class[1];
	        wvParams[0] = Class.forName("android.webkit.WebView");
	        Method fromWebView = wvcClass.getDeclaredMethod("fromWebView", wvParams);
	        Object webViewClassic = fromWebView.invoke(null, webview);

	        Class wv = Class.forName("android.webkit.WebViewClassic");
	        Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
	        Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webViewClassic);

	        Class wvc = Class.forName("android.webkit.WebViewCore");
	        Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
	        Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

	        Class bf = Class.forName("android.webkit.BrowserFrame");
	        Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
	        Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

	        Class ppclass = Class.forName("android.net.ProxyProperties");
	        Class pparams[] = new Class[3];
	        pparams[0] = String.class;
	        pparams[1] = int.class;
	        pparams[2] = String.class;
	        Constructor ppcont = ppclass.getConstructor(pparams);

	        Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
	        Class params[] = new Class[1];
	        params[0] = Class.forName("android.net.ProxyProperties");
	        Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

	        updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));
	    } catch (Exception ex) {
	        Log.e(LOG_TAG,"Setting proxy with >= 4.1 API failed with error: " + ex.getMessage());
	        return false;
	    }

	    Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API successful!");
	    return true;
	}

	/**
	 * @see <a href="https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
">
	https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
</a>
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("all")
	private static boolean setProxyKK(WebView webView, String host, int port, String applicationClassName) {
	    Log.d(LOG_TAG, "Setting proxy with >= 4.4 API.");

	    Context appContext = webView.getContext().getApplicationContext();
	    System.setProperty("http.proxyHost", host);
	    System.setProperty("http.proxyPort", port + "");
	    System.setProperty("https.proxyHost", host);
	    System.setProperty("https.proxyPort", port + "");
	    try {
	        Class applictionCls = Class.forName(applicationClassName);
	        Field loadedApkField = applictionCls.getField("mLoadedApk");
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

	        Log.d(LOG_TAG, "Setting proxy with >= 4.4 API successful!");
	        return true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return false;
	}

	private static Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
	    boolean oldAccessibleValue = field.isAccessible();
	    field.setAccessible(true);
	    Object result = field.get(classInstance);
	    field.setAccessible(oldAccessibleValue);
	    return result;
	}
}