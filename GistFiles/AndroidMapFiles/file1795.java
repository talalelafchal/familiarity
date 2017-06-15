package th.co.runnables.bblipayexample;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sun on 8/1/2016 AD.
 */
public class BBLiPayActivity extends Activity {

    private WebView webView;
    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAIL = 1;
    public static final int RESULT_CODE_CANCEL = 2;
    public static final int RESULT_CODE_INVALID_PARAMS = 3;

    public static final int CURR_CODE_USD = 840;
    public static final int CURR_CODE_THB = 764;

    private static final String BBL_IPAY_SERVICE_URL = "https://ipay.bangkokbank.com/b2c/eng/payment/payForm.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_bbl_ipay);

        FrameLayout fl = new FrameLayout(this);
        setContentView(fl);

        webView = new WebView(this);
        fl.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final HashMap<String,String> fields = new HashMap<String, String>();

        Bundle b = getIntent().getExtras();
        String merchantId = b.getString("merchantId");
        double amount = b.getDouble("amount");
        String orderRef = b.getString("orderRef");
        String currency = b.getString("currency");

        if(merchantId == null || orderRef == null){
            // Invalid Params
            this.setResult(RESULT_CODE_INVALID_PARAMS);
            finish();
        }

        int currCode = 0;
        if(currency.equalsIgnoreCase("USD")){
            currCode = CURR_CODE_USD;
        }else if(currency.equalsIgnoreCase("THB")){
            currCode = CURR_CODE_THB;
        }else{
            // Unsupported Currency
            this.setResult(RESULT_CODE_INVALID_PARAMS);
            finish();
        }

        fields.put("merchantId", merchantId);
        fields.put("amount", "" + amount);
        fields.put("orderRef", orderRef);
        fields.put("currCode", "" + currCode);
        fields.put("successUrl", "http://native-app/payment-success");
        fields.put("failUrl", "http://native-app/payment-failed");
        fields.put("cancelUrl", "http://native-app/payment-canceled");
        fields.put("payType", "N");
        fields.put("lang", "E");
        fields.put("remark", "-");

        //webView = (WebView) this.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebView", "shouldOverrideLoading" + url);
                if(url.indexOf(fields.get("successUrl")) == 0){
                    BBLiPayActivity.this.setResult(RESULT_CODE_SUCCESS);
                    finish();
                    return true;
                }else if(url.indexOf(fields.get("failUrl")) == 0){
                    BBLiPayActivity.this.setResult(RESULT_CODE_FAIL);
                    finish();
                    return true;
                }else if(url.indexOf(fields.get("cancelUrl")) == 0){
                    BBLiPayActivity.this.setResult(RESULT_CODE_CANCEL);
                    finish();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        String postData = "";
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            postData = postData + entry.getKey() + "=" + Uri.encode(entry.getValue()) + "&";
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.postUrl(BBL_IPAY_SERVICE_URL, postData.getBytes());

    }


}
