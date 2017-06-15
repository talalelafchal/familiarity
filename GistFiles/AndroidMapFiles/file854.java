/**
 * @author douzifly
 * @date 2014-12-4
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.WebView;

/**
 *
 * 提供全版本兼容的安全JsInterface
 * <br />由于系统JsInterface在4.2之前有bug，导致js代码可以访问到ClassLoader，从而做出非法的事情
 * <br />利用WebView的WebChromeClient中的onJsPrompt功能，可以拦截js的protmpt函数，并模拟函数调用
 * <br />js调用java方式 protocol:MethodName:arg1:arg2:arg3...
 *
 *
 * @author douzifly
 *
 */
public class SafeWebViewJs {

    static final String TAG = "SafeWebView";

    // key: protocal value:[methodName, method]
    private HashMap<String, HashMap<String, IJsMethod>> mProtocols
        = new HashMap<String, HashMap<String,IJsMethod>>();

    /**
     * 注册方法
     * @param method
     */
    public void registerMethod(String protocalName, String methodName, IJsMethod method) {
        if (TextUtils.isEmpty(methodName) || method == null || TextUtils.isEmpty(protocalName)) {
            throw new IllegalArgumentException("methodName or method or protocalName is empty");
        }
        HashMap<String, IJsMethod> protocal = mProtocols.get(protocalName);
        if (protocal == null) {
            protocal = new HashMap<String, SafeWebViewJs.IJsMethod>();
            mProtocols.put(protocalName.toLowerCase(), protocal);
        }

        protocal.put(methodName, method);
    }

    public void unregisterMethod(String methodName) {
        if (TextUtils.isEmpty(methodName)) {
            return;
        }
        mProtocols.remove(methodName);
    }

    public static interface IJsMethod {
        String callFromJs(List<String> args);
    }

    /**
     * 解析并处理 Js 中调用prompt的函数，如果符合调用 protocol，那么拦截并调用java函数
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result return true 符合调用协议
     * @return
     */
    public boolean handleJsPrompt(WebView view, String url, String message,
            String defaultValue, JsPromptResult result) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        LogUtils.d(TAG, "handleJsPrompt webview:" + view.hashCode() + " url:" + url
                + " message:" + message + " defaultValue:" + defaultValue + " result:" + result);
        String[] params = message.split("\\^_\\^");
        LogUtils.d(TAG, "params len:" + params.length);
        if (params.length < 2) {
            LogUtils.e(TAG, "params len < 2, but must >= 2");
            return false;
        }
        // [0] is protocol, check protocol
        String protocol = params[0];
        LogUtils.d(TAG, "protocol:" + protocol);
        if (TextUtils.isEmpty(protocol)) {
            LogUtils.d(TAG, "protocol is empty");
            return false;
        }
        if (mProtocols.containsKey(protocol.toLowerCase())) {
            String method = params[1];
            LogUtils.d(TAG, "method name:" + method);
            if (TextUtils.isEmpty(method)) {
                LogUtils.e(TAG, "no method name");
                return false;
            }

            IJsMethod jsmethod = mProtocols.get(protocol).get(method);
            if (jsmethod == null) {
                LogUtils.e(TAG, "no such method:" + method);
                return false;
            }

            // check args
            List<String> args = new ArrayList<String>(params.length - 2);
            for (int i = 2; i < params.length; i++) {
                String arg = params[i];
                LogUtils.d(TAG, "arg:" + arg);
                if (TextUtils.isEmpty(arg)) {
                    arg = "null";
                }
                args.add(arg);
            }
            // call back
            LogUtils.d(TAG, "do callback");
            try {
                String ret = jsmethod.callFromJs(args);
                result.confirm(ret);
                return true;
            } catch(Exception e) {
                LogUtils.d(TAG, "callFromJs exp:" + e);
                return false;
            }
        } else {
            LogUtils.e(TAG, "protocol not matched");
        }

        return false;
    }

    public static boolean isArgNull(String arg) {
        return arg == null || arg.equalsIgnoreCase("null");
    }

    /**
     * 注册一个对象，分析对象中的方法，如果标注了 {@link SafeJavascriptInterface} 那么将自动注册 <br />
     * 自动注册的函数必须满足如此签名 String methodName(List<String> args);
     * @param protocol
     * @param obj
     */
    public void registerObject(String protocol, final Object  obj) {
        LogUtils.d(TAG, "registerObject:" + protocol + " obj:" + obj);
        Class c = obj.getClass();
        Method[] methods = c.getMethods();
        LogUtils.d(TAG, "methods count:" + methods.length);
        for (final Method m : methods) {
            SafeJavascriptInterface jsInterface = m.getAnnotation(SafeJavascriptInterface.class);
            LogUtils.d(TAG, "method:" + m.getName() + " jsInterface:" + jsInterface);
            if (jsInterface != null) {
                // check return type
                if (m.getReturnType() != String.class) {
                    LogUtils.d(TAG, "return type not String, ignore");
                    continue;
                }
                // check param
                Class[] params = m.getParameterTypes();
                LogUtils.d(TAG, "params count:" + params.length);
                if (params.length != 1) {
                    LogUtils.d(TAG, "params count must == 1");
                    continue;
                }
                Class param = params[0];
                if (!param.isAssignableFrom(List.class)) {
                    LogUtils.d(TAG, "params type must List<String>");
                    continue;
                }
                // register
                IJsMethod jsMethod = new IJsMethod() {

                    @Override
                    public String callFromJs(List<String> args) {
                        String ret = "";
                        try {
                            ret = (String) m.invoke(obj, args);
                            LogUtils.d(TAG, "invoke method sucess:" + m.getName() + " ret:" + ret);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.e(TAG, "invoke failed, method:" + m.getName());
                        }
                        return ret;
                    }
                };

                registerMethod(protocol, m.getName(), jsMethod);
            }
        }
    }
}

// --------------- SafeJsInterface ----------------

/**
 * 标注方法为安全js调用方法
 * @author Xiaoyuan
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeJavascriptInterface {


}



//------------------USAGE--------------------------

// Register manual
IJsMethod test = new IJsMethod() {

    @Override
    public String callFromJs(List<String> args) {
        String result = "name:" + args.get(0) + " from:" + args.get(1);
        return result;
    }
};
mSafeWebJs.registerMethod("jscall", "test", test);

// Register by Annotation

class SafeObject {
    @SafeJavascriptInterface
    public String test(final List<String> args) {
        String str = "test, name:" + args.get(0) + " from:" + args.get(1);
        return str;
    }
}

SafeWebJs.registerObject("jscall", new SafeObject());

// ----------webView must call this method ----------
webView.setWebChromeClient(new WebChromeClient(){
    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
        String defaultValue, JsPromptResult result) {
        if (mSafeWebJs != null) {
            boolean handled = mSafeWebJs.handleJsPrompt(view, url, message, defaultValue, result);
            if (handled) {
                return true;
            }
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
});

// -------------------------JS PART----------------------------------
<script>
var result = prompt("jscall^_^test^_^douzifly^_^Chengdu");
alert (result);
</script>
