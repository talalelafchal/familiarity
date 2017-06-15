package com.drc.paytm_example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

public class MainActivity extends Activity 
{
	private int randomInt = 0;
	private PaytmPGService Service = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.d("LOG", "onCreate of MainActivity");
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);

		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(1000000000);
		
		//Service = PaytmPGService.getStagingService(); //for testing environment
		
		Service = PaytmPGService.getProductionService();
		

		/*PaytmMerchant constructor takes two parameters
		1) Checksum generation url
		2) Checksum verification url
		Merchant should replace the below values with his values*/

        PaytmMerchant Merchant = new PaytmMerchant("<checksum_signing_url>","<checksum_validation_url>");
        	
        //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
        		
		Map<String, String> paramMap = new HashMap<String, String>();
		
		//these are mandatory parameters
		paramMap.put("REQUEST_TYPE", "DEFAULT");
		paramMap.put("ORDER_ID", String.valueOf(randomInt));
		paramMap.put("MID", "mymerchantid");
		paramMap.put("CUST_ID", "CUST123");
		paramMap.put("CHANNEL_ID", "WAP");
		paramMap.put("INDUSTRY_TYPE_ID", "Retail120");
		paramMap.put("WEBSITE", "mywebsite");
		paramMap.put("TXN_AMOUNT", "1");
		paramMap.put("THEME", "merchant");
		
		
						
		PaytmOrder Order = new PaytmOrder(paramMap);


    	Service.initialize(Order, Merchant, null);


        Service.startPaymentTransaction(this, false, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionSuccess(Bundle bundle) {


                Log.i("Success","onTransactionSuccess :"+bundle);
            }

            @Override
            public void onTransactionFailure(String s, Bundle bundle) {
                Log.i("Failure", "onTransactionFailure " + s);
            }

            @Override
            public void networkNotAvailable() {
                Log.i("Failure", "networkNotAvailable");
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.i("Failure", "clientAuthenticationFailed " + s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.i("Failure", "someUIErrorOccurred " + s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.i("Failure", "onErrorLoadingWebPage" + s + " " + s1);
            }
        });



	}
}
